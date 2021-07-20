package sealab.burt.server.actions.eb;

import sealab.burt.qualitychecker.EBChecker;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;

public class ProvideEBAction extends ChatBotAction {

    public ProvideEBAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state){
        startEBChecker(state);
        return createChatBotMessages("Okay, can you please tell me how the app is supposed to work instead?");
    }

}
