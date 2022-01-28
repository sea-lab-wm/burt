package sealab.burt.qualitychecker;


import lombok.extern.slf4j.Slf4j;
import sealab.burt.BurtConfigPaths;
import sealab.burt.nlparser.NLParser;
import sealab.burt.nlparser.euler.actions.DeviceActions;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.qualitychecker.actionmatcher.ActionMatchingException;
import sealab.burt.qualitychecker.actionmatcher.NLActionS2RMatcher;
import sealab.burt.qualitychecker.graph.AppGuiComponent;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.similarity.EmbeddingSimilarityComputer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public @Slf4j


class NewEBChecker {

    private final String app;
    private final String appVersion;
    private final NLActionS2RMatcher s2rParser;

    public NewEBChecker(String app, String appVersion) {
        this.app = app;
        this.appVersion = appVersion;
        this.s2rParser = new NLActionS2RMatcher(BurtConfigPaths.qualityCheckerResourcesPath, true, app);
    }

    public QualityResult checkEb(String ebDescription, GraphState obState, String obDescription) throws Exception {

        return matchEB(ebDescription, obState, obDescription);
    }

    private QualityResult matchEB(String ebDescription, GraphState obState, String obDescription)
            throws Exception {

        //---------------------------------------------------

        if (obState != null) {

            log.debug("Checking eb description when obState exists >>>>>>>>>>>>>>>>>>" );
            List<AppGuiComponent> stateComponents = obState.getComponents();
            stateComponents = stateComponents.stream()
                    .filter(c -> c.getPhrases() != null && !c.getPhrases().isEmpty())
                    .collect(Collectors.toList());

            List<String> phrases = new ArrayList<>();

            stateComponents.forEach(e -> phrases.addAll(e.getPhrases()));

            List<Double> scores =  EmbeddingSimilarityComputer.computeSimilarities(ebDescription, phrases);
            log.debug("eb matched components score:" + scores.toString() );

            if (Collections.max(scores) > 0.75){
                return new QualityResult(QualityResult.Result.MATCH);
            }else{
                return new QualityResult(QualityResult.Result.NO_MATCH);
            }

        }

        return new QualityResult(QualityResult.Result.NO_MATCH);
    }


}
