import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.protobuf.Message;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import seers.appcore.csv.CSVHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Analysis {

    public static void main(String[] args) throws IOException {

        String path = Paths.get("../evaluation/data/conversation_dumps").toString();

        Collection<File> conversations = FileUtils.listFiles(new File(path), new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return accept(file, file.getName());
            }

            @Override
            public boolean accept(File dir, String name) {
                return sessions.stream().anyMatch(s -> name.contains(s));
            }
        }, null);

//        System.out.println(conversations);
        System.out.println(conversations.size());


        List<Conv> data = new ArrayList<>();
        Gson gson = new GsonBuilder().create();
        final Type typeOf = new TypeToken<List<Object>>() {
        }.getType();

        for (File conversationFile : conversations) {
            System.out.println(conversationFile);
            List<LinkedTreeMap> messages = gson.fromJson(new FileReader(conversationFile), typeOf);

            String[] split = conversationFile.getName().replace(".json", "").split("-");

//            System.out.println(newMap);

            Conv conv = new Conv();

            conv.participant = split[1];
            conv.app = split[2];
            conv.appVersion = split[3];
            conv.session = String.join("-", Arrays.asList(split[4], split[5], split[6], split[7], split[8]));


            Integer msgId = 1;
            for (LinkedTreeMap message : messages) {
                Msg msg = new Msg();
                msg.id = msgId.toString();
                msg.messages = getMessages(message);
                msg.type = getType(message);
                msg.currentAction = getCurrentAction(message);
                msg.nextIntents = getNextIntents(message);
                conv.addMessage(msg);
                msgId++;
            }

            //--------------

            for (int i = 0; i < conv.messages.size(); i++) {
                Msg currentMsg = conv.messages.get(i);

                if (currentMsg.type.equals("bot")) {

//                if(currentMsg.type)

                    Msg nextUserMsg = null;
                    for (int j = i + 1; j < conv.messages.size(); j++) {
                        Msg currentMsg2 = conv.messages.get(j);
                        if (currentMsg2.type.equals("user")) {
                            nextUserMsg = currentMsg2;
                            break;
                        }
                    }

                    if (nextUserMsg != null) {
                        currentMsg.response = nextUserMsg;
                        if (String.valueOf(Integer.parseInt(currentMsg.id) + 1).equals(nextUserMsg.id)) {
                            currentMsg.responseNextMsg = true;
                        }
                    }
                } else {
                    Msg nextBotMsg = null;
                    for (int j = i + 1; j < conv.messages.size(); j++) {
                        Msg currentMsg2 = conv.messages.get(j);
                        if (currentMsg2.type.equals("bot")) {
                            nextBotMsg = currentMsg2;
                            break;
                        }
                    }

                    if (nextBotMsg != null) {
                        currentMsg.response = nextBotMsg;
                        if (String.valueOf(Integer.parseInt(currentMsg.id) + 1).equals(nextBotMsg.id)) {
                            currentMsg.responseNextMsg = true;
                        }
                    }
                }
            }

            //--------------

            data.add(conv);

        }


        //-----------------------


        List<String> header = Arrays.asList("Participant", "Session", "MsgId", "Type", "Messages", "CurrentAction",
                "NextIntents", "ResponseNextMsg?",
                "ResponseMsgId", "ResponseMessages", "ResponseCurrentAction",
                "ResponseNextIntents",
                "App", "AppVersion");
        File outFile = new File("conversations.csv");
        Function<Conv, List<List<String>>> fn = conv -> {
            List<List<String>> rows = new ArrayList<>();
            conv.messages.forEach(msg -> rows.add(Arrays.asList(
                    conv.participant,
                    conv.session,

                    //----------------
                    msg.id,
                    msg.type,
                    msg.messages,
                    msg.currentAction,
                    msg.nextIntents,

                    //----------------------

                    msg.response == null ? "" : (msg.responseNextMsg ? "y" : "n"),
                    msg.response == null ? "" : msg.response.id,
                    msg.response == null ? "" : msg.response.messages,
                    msg.response == null ? "" : msg.response.currentAction,
                    msg.response == null ? "" : msg.response.nextIntents,
                    //----------------------
                    conv.app,
                    conv.appVersion
            )));
            return rows;
        };
        CSVHelper.writeCsvMultiple(outFile, header, data, Collections.emptyList(), fn, ',');

        //-------------------------------

        computeStats(data);

    }

    private static void computeStats(List<Conv> data) {

        LinkedHashMap<String, LinkedHashMap<String, String>> stats = new LinkedHashMap<>();
        for (Conv conv : data) {
            List<Msg> messages = conv.messages;

            LinkedHashMap<String, String> convStats = new LinkedHashMap<>();
            stats.put(conv.session, convStats);

            for (int i = 0; i < messages.size(); i++) {

                //------------------------

                //ob stats
                long countRephrase = messages.stream().filter(msg -> msg.currentAction.equals("REPHRASE_OB")).count();
                long countNoParse =
                        messages.stream().filter(msg -> msg.currentAction.equals("PROVIDE_OB_NO_PARSE")).count();
                long countProvideOB = messages.stream().filter(msg -> msg.currentAction.equals("PROVIDE_OB")).count();


                long countObScreenSuggestions =
                        messages.stream().filter(msg -> msg.currentAction.equals("SELECT_OB_SCREEN")).count();
                long countSelectedOBScreen =
                        messages.stream().filter(msg -> msg.currentAction.equals("CONFIRM_SELECTED_OB_SCREEN")).count();


                long countMatchedOB =
                        messages.stream().filter(msg -> msg.currentAction.equals("CONFIRM_MATCHED_OB")).count();

                String ob_match = "OB_MATCH";
                String ob_suggestions = "OB_SUGGESTIONS_SELECTED";

                boolean obMatched = false;

                {
                    if ((countRephrase + countNoParse + countProvideOB) > 0 && (countObScreenSuggestions + countSelectedOBScreen) == 0) {
                        convStats.put("NO_OB_MATCH", "1");
                    } else {

                        if (countMatchedOB > 0) {
                            long countConfirmedMatchedOB = messages.stream().filter(msg -> msg.currentAction.equals(
                                    "CONFIRM_MATCHED_OB")
                                    && msg.response.messages.equals("yes")).count();
                            if (countConfirmedMatchedOB > 0) {
                                convStats.put(ob_match, "y");
                                obMatched = true;
                            } else
                                convStats.put(ob_match, "n");
                        } else {
                            long countSelectedOBScreens = messages.stream().filter(msg -> msg.currentAction.equals(
                                    "SELECT_OB_SCREEN")
                                    && msg.response.messages.startsWith("done")
                            ).count();

                            if (countSelectedOBScreens > 0) {
                                convStats.put(ob_suggestions, "y");
                                obMatched = true;
                            } else {
                                long countConfirmSelectedOBScreens =
                                        messages.stream().filter(msg -> msg.currentAction.equals(
                                                "CONFIRM_SELECTED_OB_SCREEN")
                                                && msg.response.messages.startsWith("done")
                                        ).count();
                                if (countConfirmSelectedOBScreens > 0) {
                                    convStats.put(ob_suggestions, "y");

                                    obMatched = true;
                                } else {
                                    convStats.put(ob_suggestions, "n");
                                }
                            }
                        }
                    }
                }

                //------------------------

                if (obMatched) {
                    //eb stats

                    //CLARIFY_EB
                    //PROVIDE_EB
                    //PROVIDE_EB_NO_PARSE
                    long countProvideEB =
                            messages.stream().filter(msg -> msg.currentAction.equals("PROVIDE_EB")).count();
                    long countProvideEBNoParse = messages.stream().filter(msg -> msg.currentAction.equals(
                            "PROVIDE_EB_NO_PARSE")).count();
                    long countConfirmEB =
                            messages.stream().filter(msg -> msg.currentAction.equals("CLARIFY_EB")).count();

                    if ((countProvideEBNoParse + countConfirmEB) == 0) {
                        if (countProvideEB == 0) {
                            long countConfirmSelectedOBScreens =
                                    messages.stream().filter(msg -> msg.currentAction.equals(
                                            "CONFIRM_SELECTED_OB_SCREEN")
                                            && msg.nextIntents.equals("EB_DESCRIPTION")
                                    ).count();
                            if (countConfirmSelectedOBScreens > 0)
                                convStats.put("EB_MATCH", "1");
                            else
                                throw new RuntimeException();
                        } else
                            convStats.put("EB_MATCH", "1");
                    } else {

                        if (countProvideEBNoParse > 0) {
                            convStats.put("EB_NO_MATCH", "1");
                        } else {
                            long countConfirmedEB =
                                    messages.stream().filter(msg -> msg.currentAction.equals("CLARIFY_EB") &&
                                            msg.response.messages.equals("yes")).count();
                            if (countConfirmedEB > 0) {
                                convStats.put("EB_SUGGESTION_CONFIRMED", "y");
                            } else {
                                convStats.put("EB_SUGGESTION_CONFIRMED", "n");
                            }
                        }

                    }
                }


            }


        }

        for (Map.Entry<String, LinkedHashMap<String, String>> entry : stats.entrySet()) {
            System.out.println(entry);
        }
    }

    private static String getNextIntents(LinkedTreeMap message) {
        String nextInt = "";
//        System.out.println(message);
        if (message.containsKey("nextIntents")) {
            nextInt = String.join(", ", (ArrayList) message.get("nextIntents"));
        }
        return nextInt;
    }

    private static String getCurrentAction(LinkedTreeMap message) {
        String action = "";
//        System.out.println(message);
        if (message.containsKey("currentAction")) {
            action = message.get("currentAction").toString();
        }
        return action;
    }

    private static String getType(LinkedTreeMap message) {
        String type = "user";
//        System.out.println(message);
        if (message.containsKey("messages")) {
            type = "bot";
        }
        return type;
    }

    private static String getMessages(LinkedTreeMap message) {
        String messages = "";
//        System.out.println(message);
        if (message.containsKey("message")) {
            messages = message.get("message").toString();
        } else if (message.containsKey("messages")) {
            ArrayList messages1 = (ArrayList) message.get("messages");
            List<String> messageObjs = (List<String>) messages1.stream()
                    .map(messageObj -> {
                        LinkedTreeMap message1 = (LinkedTreeMap) ((LinkedTreeMap) messageObj).get("messageObj");
                        if (message1 == null) return null;
                        return message1.get("message").toString();
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            messages = String.join(". ", messageObjs);
        }
        /*else {
            throw new RuntimeException("No message");
        }*/

        if (message.containsKey("selectedValues")) {
            messages += ", selectedValues = " + String.join(", ", (ArrayList) message.get("selectedValues"));
        }
        return messages.replace(",", ";").replace("\"", "\"\"");
    }


    public static class Msg {
        String id;
        String messages;
        String type;
        String currentAction;
        String nextIntents;

        Msg response;
        boolean responseNextMsg = false;
    }

    public static class Conv {
        String participant;
        String session;
        String app;
        String appVersion;
        List<Msg> messages = new ArrayList<>();

        public void addMessage(Msg m) {
            messages.add(m);
        }
    }


    static final List<String> sessions = Arrays.asList("6f6325ac-d8cc-46cf-b37c-b54a6c7fbacf",
            "03a788b4-cd21-4e97-9c63-1d20cc9a9fe7",
            "36e34089-985f-4fe5-84ed-bf6896529f59",
            "b6fdab35-ff27-48b9-8dcf-62695f8c5cf2",
            "d8dca593-a878-4306-bd08-81a982b1dd59",
            "8934ed17-6513-4b21-b774-8e1a728951b7",
            "8f6449c3-34ac-4fa2-b287-544383c2641c",
            "6ae5ed6b-0dce-44ad-b5ed-110aaaa86d17",
            "c9fce465-cc97-4219-abfd-7e52d18fa93a",
            "765edb48-103d-4760-8e22-bb7b396cb872",
            "93d70141-472d-44c9-aebf-70c64cd95308",
            "1eb34d09-c594-42e3-b29c-f6d36cf1abe9",
            "94eb7f91-b8e5-4152-9f20-0936eadb7102",
            "25d41c1f-a5b1-46aa-a472-2c8543c59dd7",
            "a018a21a-3200-4a46-a405-8df10f559e52",
            "efb6bf4c-64b4-424f-a152-0730937870dc",
            "4bb447df-d095-4219-b6b5-c08dfd4f7162",
            "81fc2e5e-51e9-467a-8875-46c586fad67d",
            "761f4bc0-7001-4634-91b7-5ccd5ae3b665",
            "14062819-6c89-4459-ab7e-4dcb8f7d865c",
            "2ddd0f77-d3b8-46c5-ad05-5a0e07bc3004",
            "130e049f-fb2b-4458-a031-a079937d05fa",
            "6d171258-430d-4ba0-8328-f9a9a5ec6c19",
            "1bf66f2b-9a9b-4dc9-af69-9c1b2712e65d",
            "1bbd5c41-d8a6-4f97-afc2-8eb6e20974e8",
            "0dd08354-c060-4ebf-aa17-2ce4451fd2e0",
            "a2658201-c402-41e3-b342-5f9700442692",
            "07755275-3506-4f34-9e00-642f7a002850",
            "277f4968-92c7-4253-af2e-6f367725e832",
            "dbf28320-fefe-4257-95f1-4b6efd93c690",
            "ce7cc88b-cef7-43f4-ae50-039f89654a3c",
            "0d31a8e5-929a-463d-bd03-81bb01dcccc0",
            "dcd76237-a0c7-4717-a1f4-21ca2a03a06e",
            "849b0ee4-9b02-4a66-97cf-b19be6db97de",
            "ce4a3f12-eeb2-43b8-9298-5f4b31375cb1",
            "4f92e80b-c4e9-48b4-9f71-b9aa7c73f5c8",
            "143b3591-ec1a-406e-8ed3-35e80eae90d3",
            "97317f1b-5c1a-491e-89a1-d4b37d84e5d6",
            "8ea78c05-e3bc-4c21-88f7-4ff67777bbba",
            "c683c1ce-535f-49f1-bde0-e4353564b928",
            "bda811c5-5650-4f97-8505-8ec7e9ede8a5",
            "f6f47824-f1e5-4ee7-adcf-3b7d57740781",
            "979b79be-fd8d-4d35-b7ca-783d0cfc3bd8",
            "46bbba6c-f31e-4def-a1bb-055a496a2171",
            "cc9b64a5-f6e8-4d09-bd35-5748e266b731",
            "34a1616a-122f-4307-8c05-3f9b2af59d54",
            "5c4ab1c0-de07-4552-9faf-ccd7d2eeff06",
            "41a37778-36b4-4bfd-9002-9860d24b3cbe",
            "b6bf0805-20a7-42d9-a610-3fe8f72f2139",
            "c72a2697-6f9f-4c3e-9736-fc33bc58f8ff",
            "06187680-47a8-47db-9ab9-82697f8e3dbe",
            "6e17a1cc-c4c4-4096-acb8-ec26266b021c",
            "cdda1d1e-b528-4120-a05d-6f0bfe7255d4",
            "c9b8f500-34c5-455d-bae6-14c5c7f35d77");
}
