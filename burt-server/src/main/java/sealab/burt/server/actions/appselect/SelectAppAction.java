package sealab.burt.server.actions.appselect;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import edu.semeru.android.core.entity.model.fusion.Execution;
import lombok.extern.slf4j.Slf4j;
import sealab.burt.BurtConfigPaths;
import sealab.burt.nlparser.euler.actions.utils.AppNamesMappings;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.entity.KeyValues;
import sealab.burt.server.conversation.entity.MessageObj;
import sealab.burt.server.conversation.entity.WidgetName;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import static sealab.burt.server.StateVariable.APP_ASKED;
import static sealab.burt.server.StateVariable.PARTICIPANT_NAME;

public @Slf4j
class SelectAppAction extends ChatBotAction {


    public static List<KeyValues> ALL_APPS = null;
    private static final String NO_APP_LOGO = "NO_APP_LOGO.png";

    public static void generateAppData() {

        Path crashScopeDataPath = Paths.get(BurtConfigPaths.crashScopeDataPath);
        Path appLogosPath = Paths.get(BurtConfigPaths.appLogosPath);

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
                        try {
                            return new KeyValues(Integer.toString(i),
                                    getAppNameVersion(dir), getLogoFileName(appLogosPath, dir));
                        } catch (Exception e) {
                            log.error("Error loading " + dir, e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            //sort by name
            ALL_APPS.sort(Comparator.comparing(KeyValues::getValue1));
        }
    }

    public SelectAppAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
        generateAppData();
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

    private static String getAppNameVersion(Path appDir) throws IOException {
        String fileName = appDir.getFileName().toString();

        int i = fileName.indexOf("-");
        String packageName = fileName.substring(0, i);
        String appVersion = fileName.substring(i + 1);
        String appName = null;

        List<Path> executionFiles = Files.find(appDir, 1,
            (path, attr) -> path.toFile().getName().startsWith("Execution-"))
            .collect(Collectors.toList());

        if (executionFiles == null || executionFiles.isEmpty()) { 

            List<String> appNames = AppNamesMappings.getAppNamesFromPackage(packageName);

            if (appNames == null)
                throw new RuntimeException("Could not find app name for package: " + packageName);

            appName = appNames.get(0);
        }else{
            JsonReader reader = new JsonReader(new InputStreamReader(
                new FileInputStream(executionFiles.get(0).toFile()), StandardCharsets.UTF_8));

            // De-serialize the Execution object.
            Gson gson = new Gson();
            Execution execution = gson.fromJson(reader, Execution.class);

            appName = execution.getApp().getName();
            AppNamesMappings.addAppNameForPackage(appName, packageName);
            AppNamesMappings.addAppNameForPackage(appName.toLowerCase(), packageName);
        }

        return String.format("%s v. %s", appName, appVersion);
    
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) {
        state.put(APP_ASKED, true);
        String participant = state.get(PARTICIPANT_NAME).toString();
        MessageObj messageObj = new MessageObj(
                "Hi " +participant + ", please <b>select the app</b> that is having the problem",
                WidgetName.AppSelector, false);
        return createChatBotMessages(
                new ChatBotMessage(messageObj, ALL_APPS)
        );

    }

}
