package sealab.burt.server.actions.s2r.prediction;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.actions.s2r.missing.SelectMissingS2RAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.entity.KeyValues;
import sealab.burt.server.conversation.entity.MessageObj;
import sealab.burt.server.conversation.entity.WidgetName;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.Collections;
import java.util.List;

import static sealab.burt.server.StateVariable.*;

public @Slf4j
class ProvideNextPredictedS2RAction extends ChatBotAction {

    public ProvideNextPredictedS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) throws Exception {

        // get the next predicted currentPath
        List<List<AppStep>> graphPaths = (List<List<AppStep>>) state.get(PREDICTED_S2R_PATHS_WITH_LOOPS);
        List<AppStep> currentPath = graphPaths.get((int) state.get(PREDICTED_S2R_CURRENT_PATH));

        // get screenshots
        List<KeyValues> stepOptions = SelectMissingS2RAction.getStepOptions(currentPath, state);

        if (stepOptions.isEmpty()) {
            setNextExpectedIntents(Collections.singletonList(Intent.S2R_DESCRIPTION));
            return createChatBotMessages("Okay, can you provide the <b>next step</b> that you performed?");
        }else{
            // increment the number of tries
            log.debug("Suggesting currentPath #" + state.get(PREDICTED_S2R_CURRENT_PATH));

            MessageObj messageObj = new MessageObj( "Can you <b>select the steps</b> you actually performed next?",
                    WidgetName.S2RScreenSelector, true);

            return createChatBotMessages(
                    "Okay then, the <b>next steps</b> that you performed might be the following",
                    new ChatBotMessage(messageObj, stepOptions));
        }

    }
}
