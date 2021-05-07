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
//    public static void main(String[] args) throws IOException {
//        File htmlTemplate = new File("D:/Projects/burt/burt-server/html_template/template.html");
//        Document doc = Jsoup.parse(htmlTemplate, "UTF-8");
//        Element contentOB = doc.getElementById("OB");
//        contentOB.append("the app crashed");
//        contentOB.append("<br/>");
//        contentOB.append("<img src=" + "../../data/app_logos/OBScreen.png" + " width=200 height=400>");
//
//        Element contentEB = doc.getElementById("EB");
//        contentEB.append("the app should not crash");
//        contentEB.append("<br/>");
//        contentEB.append("<img src=" + "../../data/app_logos/EBScreen.png" + " width=200 height=400>");
//
//        Element contentS2R = doc.getElementById("S2R");
//        contentS2R.append("I open the app");
//        contentS2R.append("<br/>");
//        contentS2R.append("<img src=" + "../../data/app_logos/S2RScreen1.png" + " width=200 height=400>");
//        contentS2R.append("<br/>");
//        contentS2R.append("I clicked some button");
//        contentS2R.append("<br/>");
//        contentS2R.append("<img src=" + "../../data/app_logos/S2RScreen2.png" + " width=200 height=400>");
//
//
//        File outputFile = Paths.get("D:/Projects/burt/burt-server/html_template/test.html").toFile();
//        FileUtils.write(outputFile, doc.html(), Charset.defaultCharset());
//
//    }

    public void generateOutput(File outputFile, ConcurrentHashMap<StateVariable, Object> state) throws Exception {

        File htmlTemplate = new File("html_template/template.html");

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("APP", getApplicationName(state));
        parameters.put("APP_VERSION", getApplicationVersion(state));
        List<outputMessageObj> OB = (List<outputMessageObj>) state.get(REPORT_OB);
        List<outputMessageObj> EB = (List<outputMessageObj>) state.get(REPORT_EB);
        List<outputMessageObj> S2R = (List<outputMessageObj>) state.get(REPORT_S2R);
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
        Element contentAPP = doc.getElementById("app");
        //Bug Report [1], App and Version: [2]
        contentAPP.append("Bug Report" + ", App and Version: " +  parameters.get("APP") + " v." + parameters.get("APP_VERSION"));
        //OB
        Element contentOB = doc.getElementById("OB");
        contentOB.append("<h5>Observed behavior</h5>");
        List<outputMessageObj> OBList = (List<outputMessageObj>) parameters.get("OB");
        for (outputMessageObj messageObj: OBList) {
            String message =  messageObj.getMessage();
            String screenshotPath = messageObj.getScreenshotPath();
            if (message != null && message.length() > 0){
                contentOB.append( "<P>" + message +  "</P>");
            }
            if (screenshotPath != null){
                System.out.println("OB:" + screenshotPath);
                contentOB.append("<img class=\"small_img\"" + "src=\"" + screenshotPath + "\" width=\"50\" height=\"100\" onclick=\"document.getElementById('light').style.display ='block'; document.getElementById('fade').style.display='block'\" alt=\"\"/>");
                contentOB.append( "<img id=\"light\" class=\"big_img\"" + "src=\"" + screenshotPath + "\" onclick = \"document.getElementById('light').style.display ='none' ;document.getElementById('fade').style.display='none'\" alt=\"\"/>");
                contentOB.append("   <div id=\"fade\" class=\"black_overlay\"></div>") ;
            }
        }
        //EB
        Element contentEB = doc.getElementById("EB");
        contentEB.append("<h5>Expected behavior</h5>");
        List<outputMessageObj> EBList = (List<outputMessageObj>) parameters.get("EB");
        for (outputMessageObj messageObj : EBList) {
            String message =  messageObj.getMessage();
            String screenshotPath = messageObj.getScreenshotPath();
            if (message != null && message.length() > 0){
                contentEB.append( "<P>" + message +  "</P>");
            }
            if (screenshotPath != null){
                contentEB.append("<img class=\"small_img\"" + "src=\"" + screenshotPath + "\" width=\"50\" height=\"100\" onclick=\"document.getElementById('light_EB').style.display ='block'; document.getElementById('fade_EB').style.display='block'\" alt=\"\"/>");
                contentEB.append( "<img id=\"light_EB\" class=\"big_img\"" + "src=\"" + screenshotPath + "\" onclick = \"document.getElementById('light_EB').style.display ='none' ;document.getElementById('fade_EB').style.display='none'\" alt=\"\"/>");
                contentEB.append("   <div id=\"fade_EB\" class=\"black_overlay\"></div>") ;
            }
        }
        //S2R
        Element contentS2R = doc.getElementById("S2R");
        List<outputMessageObj> S2RList = (List<outputMessageObj>) parameters.get("S2R");
        if (S2RList != null) {
            for (int i = 0; i < S2RList.size(); i++) {
                String message =  S2RList.get(i).getMessage();
                String screenshotPath = S2RList.get(i).getScreenshotPath();
                contentS2R.append("<div class=\"align\" id=S2R_" + i + ">");
                Element Step = doc.getElementById("S2R_" + i);

                Step.append( "<h5> Step" + i + "</h5>");
//                contentS2R.append("<h5> Step " + i + "</h5>");
                if (message != null && message.length() > 0) {
                    Step.append("<P>" + message +" </P>");
                }
                if (screenshotPath != null ) {
                    Step.append("<img class=\"small_img\"" + "src=\"" + screenshotPath + "\" width=\"50\" height=\"100\" onclick=\"document.getElementById('light_" + i + "').style.display ='block'; document.getElementById('fade_" + i + "').style.display='block'\" alt=\"\"/>");
                    Step.append("<img id=\"light_" + i + "\" class=\"big_img\"" + " src=\"" + screenshotPath + "\" onclick = \"document.getElementById('light_" + i + "').style.display ='none' ;document.getElementById('fade_" + i + "').style.display='none'\" alt=\"\"/>");
                    Step.append("<div id=\"fade_" + i + "\" class=\"black_overlay\"></div>") ;

                }
            }
        }
        return doc.html();









    }


}
