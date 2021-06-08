package sealab.burt.server.actions.others;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class UnexpectedErrorAction extends ChatBotAction {

    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state){
        return createChatBotMessages("I am sorry, there was an unexpected error in the server.",
                "Please try again or restart the conversation.");
    }

}
