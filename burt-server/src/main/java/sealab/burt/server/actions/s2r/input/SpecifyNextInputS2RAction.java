package sealab.burt.server.actions.s2r.input;

import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;

public class SpecifyNextInputS2RAction extends ChatBotAction {
    public SpecifyNextInputS2RAction(Intent... nextIntents) {
        super(nextIntents);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) throws Exception {
        return createChatBotMessages("Oops, I couldn't get the input value",
                "Can you please <b>provide the input</b> once more?"
        );
    }
}
