package sealab.burt.server.actions.ob;

import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.entity.KeyValues;
import sealab.burt.server.conversation.entity.MessageObj;
import sealab.burt.server.conversation.entity.WidgetName;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.Collections;
import java.util.List;

import static sealab.burt.server.StateVariable.OB_QUALITY_RESULT;

public class ConfirmMatchedOBAction extends ChatBotAction {

    public ConfirmMatchedOBAction(Intent... nextIntents) {
        super(nextIntents);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) throws Exception {

        QualityResult result = (QualityResult) state.get(OB_QUALITY_RESULT);
        GraphState graphState = result.getMatchedStates().get(0);

        state.initOrIncreaseCurrentAttemptObMatched();

        List<KeyValues> optionList =
                SelectOBScreenAction.getObScreenOptions(Collections.singletonList(graphState), state, 0);

        ChatBotMessage optionMessage = new ChatBotMessage(
                new MessageObj("Okay, just to double check, is this the screen that is <b>having or triggering</b> " +
                        "the problem?",
                        WidgetName.OneScreenNoButtons),
                optionList, false);

        state.put(StateVariable.OB_MATCHED_CONFIRMATION, true);

        return createChatBotMessages(optionMessage);
    }
}
