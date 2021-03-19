package sealab.burt.nlparser.euler.actions;

import sealab.burt.nlparser.euler.actions.trace.StackTrace;
import sealab.burt.nlparser.euler.actions.utils.StackTraceParser;
import seers.bugrepcompl.entity.regularparse.ParsedBugReport;
import seers.bugrepcompl.entity.regularparse.ParsedBugReportDescription;
import seers.bugrepcompl.entity.regularparse.ParsedDescriptionParagraph;
import seers.bugrepcompl.entity.regularparse.ParsedDescriptionSentence;

import java.util.List;
import java.util.stream.Collectors;

public class BugStackTraceParser {

	public static List<StackTrace> parseTraces(ParsedBugReport bugReport) {
		throw new UnsupportedOperationException();
	}

	public static StackTrace parseFirstTrace(ParsedBugReport bugReport) {

		ParsedBugReportDescription description = bugReport.getDescription();
		if (description == null || description.getParagraphs().isEmpty()) {
			return null;
		}

		List<ParsedDescriptionParagraph> paragraphs = description.getParagraphs();

		for (ParsedDescriptionParagraph paragraph : paragraphs) {

			StackTrace trace = getTrace(paragraph);
			if (trace != null) {
				return trace;
			}
		}

		return null;
	}

	private static StackTrace getTrace(ParsedDescriptionParagraph paragraph) {

		if (paragraph == null) {
			return null;
		}

		List<ParsedDescriptionSentence> sentences = paragraph.getSentences();

		if (sentences == null || sentences.isEmpty()) {
			return null;
		}

		List<String> traceLines = sentences.stream().map(ParsedDescriptionSentence::getValue).collect(Collectors.toList());
		return StackTraceParser.parseFirstTrace(traceLines);
	}

}
