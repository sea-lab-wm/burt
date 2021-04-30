package sealab.burt.server.output;
import static sealab.burt.server.StateVariable.*;

import org.apache.commons.io.FileUtils;
import sealab.burt.nlparser.euler.actions.utils.GeneralUtils;
import sealab.burt.server.StateVariable;
import sealab.burt.server.conversation.MessageObj;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HTMLOutputGenerator {
    public static void main(String[] args) throws IOException {
        File htmlTemplate = new File("D:/Projects/burt/burt-server/html_template/template.html");
        Document doc = Jsoup.parse(htmlTemplate, "UTF-8");
        Element contentOB = doc.getElementById("OB");
        contentOB.append("the app crashed");
        contentOB.append("<br/>");
        contentOB.append("<img src=" + "../../data/app_logos/OBScreen.png" + " width=200 height=400>");

        Element contentEB = doc.getElementById("EB");
        contentEB.append("the app should not crash");
        contentEB.append("<br/>");
        contentEB.append("<img src=" + "../../data/app_logos/EBScreen.png" + " width=200 height=400>");

        Element contentS2R = doc.getElementById("S2R");
        contentS2R.append("I open the app");
        contentS2R.append("<br/>");
        contentS2R.append("<img src=" + "../../data/app_logos/S2RScreen1.png" + " width=200 height=400>");
        contentS2R.append("<br/>");
        contentS2R.append("I clicked some button");
        contentS2R.append("<br/>");
        contentS2R.append("<img src=" + "../../data/app_logos/S2RScreen2.png" + " width=200 height=400>");


        File outputFile = Paths.get("D:/Projects/burt/burt-server/html_template/test.html").toFile();
        FileUtils.write(outputFile, doc.html(), Charset.defaultCharset());

    }

    public void generateOutput(File outputFile, ConcurrentHashMap<StateVariable, Object> state) throws Exception {

        File htmlTemplate = new File("html_template/template.html");

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("APP", getApplicationName(state));
        parameters.put("APP_VERSION", getApplicationVersion(state));
        List<outputMessageObj> OB = (List<outputMessageObj>) state.get(OB_DESCRIPTION);
        List<outputMessageObj> EB = (List<outputMessageObj>) state.get(EB_DESCRIPTION);
        List<outputMessageObj> S2R = (List<outputMessageObj>) state.get(S2R_DESCRIPTION);
        parameters.put("OB", OB);
        parameters.put("EB", EB);
        parameters.put("S2R", S2R);

        String finalReport = generateHTML(htmlTemplate, parameters);
        FileUtils.write(outputFile, finalReport, Charset.defaultCharset());

    }
    String getApplicationName(ConcurrentHashMap<StateVariable, Object> state) {
        return (String) state.get(APP);
    }
    String getApplicationVersion(ConcurrentHashMap<StateVariable, Object> state) {
        return (String) state.get(APP_VERSION);
    }
    String generateHTML(File htmlTemplate, HashMap<String, Object> parameters) throws IOException {
        Document doc = Jsoup.parse(htmlTemplate, "UTF-8");
        //APP_VERSION
        Element contentAPP = doc.getElementById("APP");
        contentAPP.append(parameters.get("APP") + " v." + parameters.get("APP_VERSION"));
        //OB
        Element contentOB = doc.getElementById("OB");
        List<outputMessageObj> OBList = (List<outputMessageObj>) parameters.get("OB");
        for (outputMessageObj messageObj: OBList) {
            String message =  messageObj.getMessage();
            if (message != null && message.length() > 0){
                contentOB.append(message);
            }
            List<Path> screenshots = messageObj.getScreenshots();
            if (screenshots != null && !messageObj.getScreenshots().isEmpty()){
                for (Path screenshot: screenshots) {
                    System.out.println("OB:" + screenshot.toString());
                    contentOB.append("<br/>");
                    System.out.println("<img src=\"" + screenshot.toString() + "\" width=\"200\" height=\"400\">");
                    contentOB.append("<img src=\"" + screenshot.toString() + "\" width=\"200\" height=\"400\">");
                }
            }
        }
        //EB
        Element contentEB = doc.getElementById("EB");
        List<outputMessageObj> EBList = (List<outputMessageObj>) parameters.get("EB");
        for (outputMessageObj messageObj : EBList) {
            String message = messageObj.getMessage();
            if (message != null && message.length() > 0){
                contentEB.append(message);
            }
            List<Path> screenshots = messageObj.getScreenshots();
            if (screenshots != null && !messageObj.getScreenshots().isEmpty()){
                for (Path screenshot: screenshots) {
                    contentEB.append("<br/>");
                    contentEB.append("<img src=\"" + screenshot.toString() + "\" width=\"200\" height=\"400\">");
                }
            }
        }
        //S2R
        Element contentS2R = doc.getElementById("S2R");
        List<outputMessageObj> S2RList = (List<outputMessageObj>) parameters.get("S2R");
        if (S2RList != null) {
            for (outputMessageObj messageObj : S2RList) {
                String message = messageObj.getMessage();
                if (message != null && message.length() > 0) {
                    contentS2R.append(message);
                }
                List<Path> screenshots = messageObj.getScreenshots();
                if (screenshots != null && !messageObj.getScreenshots().isEmpty()) {
                    for (Path screenshot : screenshots) {
                        contentS2R.append("<br/>");
                        contentS2R.append("<img src=\"" + screenshot.toString() + "\" width=\"200\" height=\"400\">");
                    }
                }
            }
        }
        return doc.html();









    }


}
