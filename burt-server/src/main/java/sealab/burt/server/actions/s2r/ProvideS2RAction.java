package sealab.burt.server.actions.s2r;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;

public class ProvideS2RAction extends ChatBotAction {

    public static String[] S2RFormatTip = {"<b>Tip:</b> to express the step you can use the format:",
            "I [action] [UI component or complement]",
            "<b>Examples:</b> I clicked the save button, I entered \"test\" in the comments text field, etc.",
            "Remember that you can say \"<b>This is/was the last step</b>\" to end the reporting"};

    public ProvideS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) {
        List<ChatBotMessage> chatBotMessages;

        if (state.containsKey(StateVariable.ASKED_TO_WRITE_S2R)) {
            chatBotMessages = createChatBotMessages("Got it, what is the step that you performed next?");
        } else {
            state.put(StateVariable.ASKED_TO_WRITE_S2R, true);
            chatBotMessages = createChatBotMessages("Got it, what is the step that you performed next?", S2RFormatTip);
        }

        return chatBotMessages;
    }

}
