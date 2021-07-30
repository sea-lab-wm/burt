package sealab.burt.server.output;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import sealab.burt.server.conversation.state.ConversationState;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

import static sealab.burt.server.StateVariable.*;

public @Slf4j
class HTMLBugReportGenerator {

    private static int bugReportId = 1;

    public void generateOutput(File outputFile, ConversationState state) throws Exception {
        File htmlTemplate = new File(Path.of(".", "example", "template.html").toString());
        String finalReport = generateHTML(htmlTemplate, state);
        FileUtils.write(outputFile, finalReport, Charset.defaultCharset());
        bugReportId++;
    }

    String generateHTML(File htmlTemplate,
                        ConversationState state) throws IOException {

        Document doc = Jsoup.parse(htmlTemplate, "UTF-8");
        //APP_VERSION
        Element contentAPP = doc.getElementById("appinfo");
        //Bug Report [1], App and Version: [2]
        if (!state.containsKey(APP_ASKED)) {
            contentAPP.append("Bug Report #" + bugReportId + " for " +
                    state.get(APP_NAME) + " v. " + state.get(APP_VERSION));
        }
        Element content = doc.getElementById("bugreport");

        List<BugReportElement> OBList = (List<BugReportElement>) state.get(REPORT_OB);
        List<BugReportElement> EBList = (List<BugReportElement>) state.get(REPORT_EB);
        List<BugReportElement> S2RList = (List<BugReportElement>) state.get(REPORT_S2R);

        content.append("<div class=\"row-fluid\" id=\"obeb\">");
        Element obebRow = doc.getElementById("obeb");
        //OB
        if (OBList != null) {
            obebRow.append("<div class=\"span5\" id=ob>");
            Element obSpan = doc.getElementById("ob");
            obSpan.append("<h2>Observed Behavior</h2>");
            for (BugReportElement brElement : OBList) {
                String strElement = StringEscapeUtils.escapeHtml4(brElement.getStringElement());
                String screenshotPath = getLinkScreenshotPath(brElement.getScreenshotPath());
//            log.debug("OB:" + screenshotPath);
                if (strElement != null && strElement.length() > 0) {
                    obSpan.append("<img class=\"screenshot\" src=\"" + screenshotPath + "\" title=\"" + strElement + "\">");
                    obSpan.append("<p>" + strElement + "</p>");
                }
            }
        }
        //EB
        if( EBList != null) {
            for (BugReportElement messageObj : EBList) {
                obebRow.append("<div class=\"span5\" id=eb>");
                Element ebSpan = doc.getElementById("eb");
                ebSpan.append("<h2>Expected Behavior</h2>");

                String strElement = StringEscapeUtils.escapeHtml4(messageObj.getStringElement());
                String screenshotPath = getLinkScreenshotPath(messageObj.getScreenshotPath());
//                log.debug("EB:" + screenshotPath);

                if (strElement != null && strElement.length() > 0) {
                    ebSpan.append("<img class=\"screenshot\" src=\"" + screenshotPath + "\" title=\"" + strElement + "\">");
                    ebSpan.append("<p>" + strElement + "</p>");
                }
            }
        }

        //S2R
        if (S2RList != null) {
            content.append("<h3>Steps to Reproduce </h3>");

            int numOfRows = S2RList.size() / 4;
            for (int i = 0; i < numOfRows; i++) {
                int indexOfRow = i + 1;
                content.append("<div class=\"row-fluid\" id=\"row" + indexOfRow + "\">");
            }
            if (S2RList.size() % 4 > 0) {
                content.append("<div class=\"row-fluid\" id=\"row" + (numOfRows + 1) + "\">");
            }
            for (int i = 0; i < S2RList.size(); i++) {
                String strElement = StringEscapeUtils.escapeHtml4(S2RList.get(i).getStringElement());

                String screenshotPath = getLinkScreenshotPath(S2RList.get(i).getScreenshotPath());
                int rowIndex = i / 4 + 1;
                Element s2rRow = doc.getElementById("row" + rowIndex);
                s2rRow.append("<div class=\"span4\" id=\"step" + (i + 1) + "\">");
                Element s2rSpan = doc.getElementById("step" + (i + 1));
//                log.debug("S2R:" + screenshotPath);
                if (strElement != null && strElement.length() > 0) {
                    s2rSpan.append("<img class=\"screenshot\" src=\"" + screenshotPath + "\" title=\"" + strElement + "\" >");
                    s2rSpan.append("<p>" + strElement + "</p>");
                }
                if (i < S2RList.size() - 1) {
                    s2rRow.append("<div class=\"span_arrow\"  id=\"arrow" + (i + 1) + "\">");
                    Element arrowSpan = doc.getElementById("arrow" + (i + 1));
                    arrowSpan.append("<img src=\"bug_report_icons/right-arrow-svgrepo-com.svg\">");
                }



            }
        }
        return doc.html();
    }

    public String getLinkScreenshotPath(String screenshotPath) {
        return FilenameUtils.separatorsToUnix(screenshotPath);
    }
}


