package sealab.burt.server.actions.eb;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.entity.KeyValues;
import sealab.burt.server.conversation.entity.MessageObj;
import sealab.burt.server.conversation.entity.WidgetName;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;
import sealab.burt.server.output.BugReportElement;

import java.util.Collections;
import java.util.List;

import static sealab.burt.server.StateVariable.EB_STATE;
import static sealab.burt.server.StateVariable.REPORT_OB;


public class ClarifyEBAction extends ChatBotAction {

    public ClarifyEBAction(Intent... nextIntents) {
        super(nextIntents);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) {

        List<BugReportElement> obReportElements = (List<BugReportElement>) state.get(REPORT_OB);
        BugReportElement bugReportElement = obReportElements.get(0);

        String screenshotPath = bugReportElement.getScreenshotPath();

        List<KeyValues> optionList = Collections.singletonList(new KeyValues("0", "", screenshotPath));
        ChatBotMessage optionMessage = new ChatBotMessage(new MessageObj(
                "Is this the screen that should work fine?", WidgetName.OneScreenNoButtons), optionList);

        state.put(StateVariable.EB_SCREEN_CONFIRMATION, true);
        if(bugReportElement.getOriginalElement() !=null)
            state.put(EB_STATE, bugReportElement.getOriginalElement());

        return createChatBotMessages("Okay, the description of the expected behavior reads rather general.",
                optionMessage);
    }

}
