package sealab.burt.server.actions.appselect;

import sealab.burt.nlparser.euler.actions.utils.AppNamesMappings;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.entity.KeyValues;
import sealab.burt.server.conversation.entity.UserResponse;
import sealab.burt.server.conversation.state.ConversationState;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.msgparsing.Intent.*;
public class ConfirmAppAction extends ChatBotAction {

    @Override
    public List<ChatBotMessage> execute(ConversationState state) {
        UserResponse msg = (UserResponse) state.get(CURRENT_MESSAGE);

        //-------------------------------------------

        String selectedApp = null;
        List<ChatBotMessage> incorrectOptionMessages = createChatBotMessages(
                "Sorry, I didn't quite get that",
                "Please select one <b>app</b> from the list"
        );

        this.nextExpectedIntents = Collections.singletonList(APP_SELECTED);

        if (!msg.getMessages().isEmpty()) {
            List<String> selectedValues = msg.getMessages().get(0).getSelectedValues();

            //if not selected  values, return an alert message
            if (selectedValues == null || selectedValues.isEmpty())
                return incorrectOptionMessages;

            selectedApp = selectedValues.get(0);
        }

        //--------------------------------------

        String finalSelectedApp = selectedApp;
        Optional<KeyValues> appOption = SelectAppAction.ALL_APPS.stream()
                .filter(app -> app.getKey().equals(finalSelectedApp))
                .findFirst();

        //invalid app selection
        if (appOption.isEmpty()) {
            return incorrectOptionMessages;
        }

        //-------------------------------------------

        //at this point there is a valid app selection

        this.nextExpectedIntents = Arrays.asList(AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER);

        //add app info to the state
        String appNameVersion = appOption.get().getValue1();
        String[] tokens = appNameVersion.split("v\\.");
        String appName = tokens[0].trim();
        state.put(APP_NAME, appName);
        state.put(APP_VERSION, tokens[1].trim());

        List<String> packageNames = AppNamesMappings.getPackageNames(appName);
        if (packageNames == null || packageNames.isEmpty())
            throw new RuntimeException("Could not find packages for " + appName);
        state.put(APP_PACKAGE, packageNames.get(0));

        return createChatBotMessages(MessageFormat.format("You selected \"{0}\", is that right?", appNameVersion));

    }

}
