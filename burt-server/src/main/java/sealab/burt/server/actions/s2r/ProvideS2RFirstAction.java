package sealab.burt.server.actions.s2r;

import sealab.burt.qualitychecker.BurtConfigPaths;
import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.msgparsing.Intent;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;

public class ProvideS2RFirstAction extends ChatBotAction {

    public ProvideS2RFirstAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state){
        state.put(COLLECTING_S2R, true);
        state.remove(COLLECTING_EB);

        String appName = state.get(APP_NAME).toString();
        String appVersion = state.get(APP_VERSION).toString();
        String resourcesPath = Path.of("..", "burt-quality-checker", "src", "main", "resources").toString();
        String parsersBaseFolder = Path.of("..", "burt-nlparser").toString();
        String crashScopeDataPath = BurtConfigPaths.getCrashScopeDataPath();

        if (!state.containsKey(S2R_CHECKER))
            state.put(S2R_CHECKER,
                    new S2RChecker(appName, appVersion, resourcesPath, parsersBaseFolder, crashScopeDataPath));

        return createChatBotMessages(" Okay. Now I need to know the steps that you performed and caused the problem." ,
                " Can you please tell me the first step that you performed?");
    }


}
