package sealab.burt.server.actions.appselect;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.nlparser.euler.actions.utils.AppNamesMappings;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.KeyValues;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.msgparsing.Intent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static sealab.burt.server.StateVariable.APP_ASKED;
import static sealab.burt.server.StateVariable.PARTICIPANT_ID;

public @Slf4j
class SelectAppAction extends ChatBotAction {


    public static final List<KeyValues> ALL_APPS;
    private static final String NO_APP_LOGO = "NO_APP_LOGO.png";

    static {
        Path crashScopeDataPath = Paths.get("..", "data", "CrashScope-Data");
        Path appLogosPath = Paths.get("..", "data", "app_logos");

        List<Path> directories = null;
        try {
            directories = Files.walk(crashScopeDataPath, 1)
                    .filter(path -> Files.isDirectory(path) && !path.equals(crashScopeDataPath))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Could not read the list of apps", e);
        }

        if (directories == null)
            ALL_APPS = new ArrayList<>();
        else {
            List<Path> finalDirectories = directories;
            ALL_APPS = IntStream.range(0, directories.size())
                    .mapToObj(i -> {
                        Path dir = finalDirectories.get(i);
                        return new KeyValues(Integer.toString(i),
                                getAppNameVersion(dir), getLogoFileName(appLogosPath, dir));
                    })
                    .collect(Collectors.toList());
        }
    }

    public SelectAppAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    private static String getLogoFileName(Path appLogosPath, Path appDir) {
        String fileName = appDir.getFileName().toString();

        Path logoPath = appLogosPath.resolve(NO_APP_LOGO);
        try {
            logoPath = Files.find(appLogosPath, 1,
                    (path, attr) -> path.getFileName().toString().startsWith(fileName))
                    .findFirst().orElse(null);

            if (logoPath == null)
                logoPath = appLogosPath.resolve("NO_APP_LOGO.png");

        } catch (IOException e) {
            log.error("Could not find app logo file", e);
        }
        return logoPath.toFile().getName();
    }

    private static String getAppNameVersion(Path appDir) {
        String fileName = appDir.getFileName().toString();

        int i = fileName.indexOf("-");
        String packageName = fileName.substring(0, i);
        String appVersion = fileName.substring(i + 1);

        List<String> appNames = AppNamesMappings.getAppNamesFromPackage(packageName);

        if(appNames==null)
            throw new RuntimeException("Could not find app name for package: " + packageName);

        return String.format("%s v. %s", appNames.get(0), appVersion);
    }

    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state) {
        state.put(APP_ASKED, true);
        String participant = state.get(PARTICIPANT_ID).toString();
        MessageObj messageObj = new MessageObj(
                participant + ", please select the app that is having the problem", "AppSelector");
        return createChatBotMessages(
                new ChatBotMessage(messageObj, ALL_APPS, false)
        );

    }

}
