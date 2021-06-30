package sealab.burt.server.actions.s2r;

import org.jgrapht.GraphPath;
import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.graph.GraphTransition;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.KeyValues;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.msgparsing.Intent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;

public class ProvidePredictedS2RAction2 extends ChatBotAction {

    public ProvidePredictedS2RAction2(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state) throws Exception {

        MessageObj messageObj = new MessageObj("Please click the “done” button when you are done.",
                "S2RScreenSelector");
        S2RChecker s2rchecker = (S2RChecker) state.get(S2R_CHECKER);
        GraphState currentState = s2rchecker.getCurrentState();

        // get the next predicted path
        List<GraphPath<GraphState, GraphTransition>> graphPaths =
                (List<GraphPath<GraphState, GraphTransition>>) state.get(PREDICTED_S2R_PATHS);
        GraphPath<GraphState, GraphTransition> path = graphPaths.get((int) state.get(PREDICTED_S2R_CURRENT_PATH));
        // get screenshots
        List<KeyValues> stepOptions = ProvidePredictedS2RAction.getPredictedStepOptions(s2rchecker, path, state, currentState);

        if (stepOptions.isEmpty()) {
            setNextExpectedIntents(Collections.singletonList(Intent.S2R_DESCRIPTION));
            return createChatBotMessages("Ok, can you provide next step?");
        }else{
            // increment the number of tries
            state.put(PREDICTED_S2R_CURRENT_PATH, (int) state.get(PREDICTED_S2R_CURRENT_PATH) + 1);

            return createChatBotMessages(
                    "Ok, it seems the next steps that you performed are the following.",
                    "Can you confirm which ones are correct?",
                    new ChatBotMessage(messageObj, stepOptions, true));
        }

    }
}
