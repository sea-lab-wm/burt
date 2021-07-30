package sealab.burt.server.actions.s2r.otherquality;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ProvideS2RNoParseAction extends ChatBotAction {

    public ProvideS2RNoParseAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state){
        return createChatBotMessages("I am sorry, it seems the language of the step is a little odd and I am not able " +
                "to understand it.",
                " Can you please rephrase and provide the step one more time?");
    }

}
