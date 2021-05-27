package sealab.burt.server.output;
import static sealab.burt.server.StateVariable.*;

import org.apache.commons.io.FileUtils;
import sealab.burt.server.StateVariable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HTMLOutputGenerator {

    public void generateOutput(File outputFile, ConcurrentHashMap<StateVariable, Object> state) throws Exception {

        File htmlTemplate = new File(Path.of(".", "example", "template.html").toString());

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("APP", getApplicationName(state));
        parameters.put("APP_VERSION", getApplicationVersion(state));
        List<OutputMessageObj> OB = (List<OutputMessageObj>) state.get(REPORT_OB);
        List<OutputMessageObj> EB = (List<OutputMessageObj>) state.get(REPORT_EB);
        List<OutputMessageObj> S2R = (List<OutputMessageObj>) state.get(REPORT_S2R);
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
        Element contentAPP = doc.getElementById("appinfo");
        //Bug Report [1], App and Version: [2]
        contentAPP.append("Bug Report" + ", App and Version: " +  parameters.get("APP") + " v." + parameters.get("APP_VERSION"));
        Element content = doc.getElementById("bugreport");

        content.append("<div class=\"row-fluid\" id=\"obeb\">");
        Element obebRow= doc.getElementById("obeb");
        //OB
        obebRow.append("<div class=\"span5\" id=ob>");
        Element obSpan = doc.getElementById("ob");
        obSpan.append("<h2>Observed Behavior</h2>");

        List<OutputMessageObj> OBList = (List<OutputMessageObj>) parameters.get("OB");
        for (OutputMessageObj messageObj: OBList) {
            String message =  messageObj.getMessage();
            String screenshotPath = messageObj.getScreenshotPath();
            if (screenshotPath != null){
                System.out.println("OB:" + screenshotPath);
                obSpan.append("<img class=\"screenshot\" src=\"" + screenshotPath + "\" >");
            }
            if (message != null && message.length() > 0){
                obSpan.append("<p>"+ message +  "</p>");
                if (screenshotPath != null){
                    obSpan.append("<div class=\"btn\" href=\"#\"  data=\"" + screenshotPath + "\"  title=\"" + message + "\"> Enlarge the screenshot <i class=\"fa fa-hand-o-up\" style=\"font-size:15px\"></i> </div>\n"); }
            }
        }
        //EB
        obebRow.append("<div class=\"span5\" id=eb>");
        Element ebSpan = doc.getElementById("eb");
        ebSpan.append("<h2>Expected Behavior</h2>");
        List<OutputMessageObj> EBList = (List<OutputMessageObj>) parameters.get("EB");
        for (OutputMessageObj messageObj : EBList) {
            String message =  messageObj.getMessage();
            String screenshotPath = messageObj.getScreenshotPath();
            if (screenshotPath != null){
                System.out.println("EB:" + screenshotPath);
                ebSpan.append("<img class=\"screenshot\" src=\"" + screenshotPath + "\" >");
            }
            if (message != null && message.length() > 0){
                ebSpan.append("<p>"+ message +  "</p>");
                if (screenshotPath != null){
                    ebSpan.append("<div class=\"btn\" href=\"#\"  data=\"" + screenshotPath + "\"  title=\"" + message + "\"> Enlarge the screenshot <i class=\"fa fa-hand-o-up\" style=\"font-size:15px\"></i> </div>\n"); }
            }
        }
        //
        content.append("<h3>Steps to Reproduce </h3>");

        //S2R
        List<OutputMessageObj> S2RList = (List<OutputMessageObj>) parameters.get("S2R");
        if(S2RList !=null){
            int numOfRows = S2RList.size()/5;
            for (int i = 0; i < numOfRows; i++){
                int indexOfRow = i + 1;
                content.append("<div class=\"row-fluid\" id=\"row" + indexOfRow + "\">");
            }
            if (S2RList.size() % 5 > 0){
                content.append("<div class=\"row-fluid\" id=\"row" + (numOfRows + 1) + "\">");
            }
        }
        for (int i = 0; i < S2RList.size(); i++) {
            String message = S2RList.get(i).getMessage();
            String screenshotPath = S2RList.get(i).getScreenshotPath();
            int rowIndex = i/5 + 1;
            Element s2rRow = doc.getElementById("row" + rowIndex);
            s2rRow.append("<div class=\"span4\" id=\"step" + (i+1) + "\">");
            Element s2rSpan = doc.getElementById("step" + (i+1));
            if (screenshotPath != null) {
                System.out.println("S2R:" + screenshotPath);
                s2rSpan.append("<img class=\"screenshot\" src=\"" + screenshotPath + "\" >");
            }
            if (message != null && message.length() > 0) {
                s2rSpan.append("<p>" + message + "</p>");
                if (screenshotPath != null) {
                    s2rSpan.append("<div class=\"btn\" href=\"#\"  data=\"" + screenshotPath + "\"  title=\"" + message + "\"> Enlarge the screenshot <i class=\"fa fa-hand-o-up\" style=\"font-size:15px\"></i> </div>\n");
                }
            }
        }
        return doc.html();
    }
}


