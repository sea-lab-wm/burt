package sealab.burt.nlparser.euler.actions.utils;

import sealab.burt.nlparser.euler.actions.trace.StackTrace;
import sealab.burt.nlparser.euler.actions.trace.TraceElement;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StackTraceParser {

    public static StackTrace parseFirstTrace(List<String> traceLines) {

        if (traceLines == null || traceLines.isEmpty()) {
            return null;
        }

        String exception = null;
        String message = null;
        List<TraceElement> traceElements = new ArrayList<>();
        List<StackTrace> causedBy = new ArrayList<>();

        for (int i = 0; i < traceLines.size(); i++) {

            String line = traceLines.get(i);

            TraceElement element = getTraceElement(line);
            if (element != null) {

                if (traceElements.isEmpty() && (i - 1) >= 0) {
                    // process exception and message
                    String[] values = getExceptionAndMessage(null, traceLines.get(i - 1));
                    if (values != null) {
                        exception = values[0];
                        message = values[1];
                    }
                }
                traceElements.add(element);
            } else {

                if (isCausedBySentence(line)) {
                    causedBy = getCausedByTraces(i, traceLines);
                    break;
                }

            }

        }

        // ---------------------------------------------

        if (!traceElements.isEmpty()) {
            return new StackTrace(exception, message, traceElements, causedBy);
        }

        return null;
    }

    private static String[] getExceptionAndMessage(String preReg, String text) {

        if (preReg == null) {
            preReg = "";
        }

        Pattern p = Pattern.compile(preReg + "(((\\w+[\\.\\$])+)(\\w+)Exception)\\s*:?(\\s*.*)");
        Matcher matcher = p.matcher(text);

        if (matcher.find()) {
            String exception = matcher.group(1);
            String msg = matcher.group(5);
            return new String[]{exception, msg};
        }

        return null;
    }

    private static List<StackTrace> getCausedByTraces(int i, List<String> traceLines) {

        String exception;
        String message;
        List<TraceElement> traceElements = null;

        boolean excpAndMsgFound = false;

        List<StackTrace> causedBy = new ArrayList<>();
        for (int j = i; j < traceLines.size(); j++) {

            String line = traceLines.get(j);

            // process exception and message
            String[] values = getExceptionAndMessage(null, line);

            if (values != null) {

                exception = values[0];
                message = values[1];
                traceElements = new ArrayList<>();

                StackTrace trace = new StackTrace(exception, message, traceElements, null);
                causedBy.add(trace);

                excpAndMsgFound = true;

            } else if (excpAndMsgFound) {

                TraceElement element = getTraceElement(line);
                if (element != null) {
                    traceElements.add(element);
                }
            }

            // ---------------------

        }
        return causedBy;
    }

    private static boolean isCausedBySentence(String text) {

        String[] excMsg = getExceptionAndMessage("Caused by:\\s+", text);
        return excMsg != null;
    }

    private static TraceElement getTraceElement(String text) {

        Pattern p = Pattern
                .compile("((\\w+[\\.\\$])+)(\\<?\\w+\\>?)\\((\\w+\\.java\\:\\d+|Native Method|[Unknown Source])\\)");
        Matcher matcher = p.matcher(text);
        if (matcher.find()) {

            String qualifiedClassName = matcher.group(1);
            qualifiedClassName = qualifiedClassName.substring(0, qualifiedClassName.length() - 1);
            String methodName = matcher.group(3);

            String fileNameNumber = matcher.group(4);

            Integer lineNumber = null;
            String fileName = null;

            if (fileNameNumber != null) {
                Pattern p2 = Pattern.compile("(\\w+\\.java)\\:(\\d+)");
                Matcher matcher2 = p2.matcher(text);
                if (matcher2.find()) {
                    fileName = matcher2.group(1);
                    lineNumber = Integer.valueOf(matcher2.group(2));
                }
            }

            return new TraceElement(fileName, qualifiedClassName, methodName, lineNumber);
        }

        return null;
    }

}
