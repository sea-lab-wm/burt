package sealab.burt.server.actions.s2r;

import edu.stanford.nlp.objectbank.IdentityFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.xpath.jaxp.XPathImpl;
import org.jgrapht.GraphPath;
import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.graph.GraphTransition;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.*;
import sealab.burt.server.msgparsing.Intent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;

public @Slf4j
class ProvideNextPredictedS2RAction extends ChatBotAction {

    public ProvideNextPredictedS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) throws Exception {

        // get the next predicted path
        List<List<AppStep>> graphPaths = (List<List<AppStep>>) state.get(PREDICTED_S2R_PATHS_WITH_LOOPS);
        List<AppStep> path = graphPaths.get((int) state.get(PREDICTED_S2R_CURRENT_PATH));

        // get screenshots
        List<KeyValues> stepOptions = SelectMissingS2RAction.getStepOptions(path, state);

        if (stepOptions.isEmpty()) {
            setNextExpectedIntents(Collections.singletonList(Intent.S2R_DESCRIPTION));
            return createChatBotMessages("Okay, can you provide the next step?");
        }else{
            // increment the number of tries
            log.debug("Suggesting path #" + state.get(PREDICTED_S2R_CURRENT_PATH));

            MessageObj messageObj = new MessageObj( "Can you select the ones you actually performed next?",
                    WidgetName.S2RScreenSelector);

            return createChatBotMessages(
                    "Okay then, the next steps that you performed might be the following.",
                    new ChatBotMessage(messageObj, stepOptions, true));
        }

    }
}
