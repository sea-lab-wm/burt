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
        return createChatBotMessages("Oops, I wasn't able to get the input value",
                "Can you please provide the input once more?"
//                "Please <b>enclose the input in quotes</b> (e.g., \"<b>5</b>\")"
        );
    }
}
