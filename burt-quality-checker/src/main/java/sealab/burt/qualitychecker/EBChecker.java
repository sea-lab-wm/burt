package sealab.burt.qualitychecker;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.BurtConfigPaths;
import sealab.burt.nlparser.NLParser;
import sealab.burt.nlparser.euler.actions.DeviceActions;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.qualitychecker.actionmatcher.ActionMatchingException;
import sealab.burt.qualitychecker.actionmatcher.NLActionS2RMatcher;
import sealab.burt.qualitychecker.graph.AppGuiComponent;
import sealab.burt.qualitychecker.graph.GraphState;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static sealab.burt.qualitychecker.QualityResult.Result.NOT_PARSED;

public @Slf4j
class EBChecker {

    private final String app;
    private final String appVersion;
    private final NLActionS2RMatcher s2rParser;

    public EBChecker(String app, String appVersion) {
        this.app = app;
        this.appVersion = appVersion;
        this.s2rParser = new NLActionS2RMatcher(BurtConfigPaths.qualityCheckerResourcesPath, true, app);
    }

    public QualityResult checkEb(String ebDescription, GraphState obState, String obDescription) throws Exception {
        List<NLAction> nlActions = NLParser.parseText(BurtConfigPaths.nlParsersBaseFolder, app, ebDescription);
        if (nlActions.isEmpty()) return new QualityResult(NOT_PARSED);
        if (nlActions.stream().noneMatch(nlAction -> nlAction.isOBAction() || nlAction.isEBAction()))
            return new QualityResult(NOT_PARSED);
        return matchActions(nlActions, obState, obDescription);
    }

    private QualityResult matchActions(List<NLAction> nlActions, GraphState obState, String obDescription)
            throws Exception {
        log.debug("All actions: " + nlActions);

        List<NLAction> obNlActions = null;
        if (obDescription != null) {
            obNlActions = NLParser.parseText(BurtConfigPaths.nlParsersBaseFolder, app, obDescription);
        }

        //------------------------------------------------

        if (nlActions.stream().anyMatch(NLAction::containsCrashInfo) && obNlActions != null &&
                obNlActions.stream().anyMatch(NLAction::containsCrashInfo))
            return new QualityResult(QualityResult.Result.MATCH);

        //---------------------------------------------------

        if (obState != null) {
            for (NLAction nlAction : nlActions) {

                Map.Entry<AppGuiComponent, Double> component = matchActionToState(obState, nlAction);

                if (component != null)
                    return new QualityResult(QualityResult.Result.MATCH);

            }
        }

        return new QualityResult(QualityResult.Result.NO_MATCH);
    }

    private Map.Entry<AppGuiComponent, Double> matchActionToState(GraphState obState, NLAction currNLAction) {
        log.debug("Checking state/screen: " + obState.getUniqueHash());

        //-------------------------------------
        // Get the components of the current candidate screen

        //filter out those components associated with a step, which duplicate existing components
        List<AppGuiComponent> stateComponents = obState.getComponents();
        stateComponents = stateComponents.stream()
                .filter(c -> c.getParent() != null || "NO_ID".equals(c.getIdXml()))
                .collect(Collectors.toList());

        //-------------------------------------
        // Determine the component
        Map.Entry<AppGuiComponent, Double> component = null;
        try {
            //FIXME: may need other device actions
            component = s2rParser.determineComponentForOb(currNLAction,
                    stateComponents, DeviceActions.CLICK, false);
        } catch (ActionMatchingException e) {
            //OK if there is a parsing error
        }
        return component;
    }
}
