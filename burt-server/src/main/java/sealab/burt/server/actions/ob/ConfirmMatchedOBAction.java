package sealab.burt.server.actions.ob;

import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.*;
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

        List<KeyValues> optionList =
                SelectOBScreenAction.getObScreenOptions(Collections.singletonList(graphState), state, 0);

        ChatBotMessage optionMessage = new ChatBotMessage(
                new MessageObj("Ok, just to double check, is this the screen that is having the problem?",
                        WidgetName.OneScreenNoButtons),
                optionList, false);

        state.put(StateVariable.OB_MATCHED_CONFIRMATION, true);

        return createChatBotMessages(optionMessage);
    }
}
