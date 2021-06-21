package sealab.burt.server.statecheckers;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.output.BugReportElement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.actions.ActionName.*;

public class AffirmativeAnswerStateChecker extends StateChecker {
    public AffirmativeAnswerStateChecker() {
        super(null);
    }

    @Override
    public ActionName nextAction(ConcurrentHashMap<StateVariable, Object> state) {
        ActionName nextAction = null;

        if (state.containsKey(APP_ASKED)) {
            state.remove(APP_ASKED);
            nextAction = PROVIDE_OB;
        } else if (state.containsKey(COLLECTING_OB) || state.containsKey(OB_SCREEN_SELECTED)) {
            state.remove(COLLECTING_OB);
            state.remove(OB_SCREEN_SELECTED);
            nextAction = PROVIDE_EB;
        } else if (state.containsKey(COLLECTING_EB)) {
            state.remove(COLLECTING_EB);
            // add selected EB screen to report summary
            if (!state.containsKey(REPORT_EB)) {
                List<BugReportElement> bugReportElementList = new ArrayList<>();
                bugReportElementList.add(new BugReportElement((String) state.get(EB_DESCRIPTION), null,
                        (String) (state.get(EB_SCREEN))));
                state.put(REPORT_EB, bugReportElementList);
            } else {
                List<BugReportElement> bugReportElementList = (List<BugReportElement>) state.get(REPORT_EB);
                bugReportElementList.add(new BugReportElement((String) state.get(EB_DESCRIPTION),
                        null, (String) (state.get(EB_SCREEN))));
            }
            nextAction = PROVIDE_S2R_FIRST;
        } else if (state.containsKey(StateVariable.CONFIRM_LAST_STEP)) {
            state.remove(COLLECTING_S2R);
            state.remove(StateVariable.CONFIRM_LAST_STEP);
            // CHECK LAST STEP HERE
            nextAction = REPORT_SUMMARY;
        }

        return nextAction;
    }
}
