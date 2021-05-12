package sealab.burt.nlparser.euler.actions;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import sealab.burt.nlparser.euler.actions.BugStackTraceParser;
import sealab.burt.nlparser.euler.actions.trace.StackTrace;
import seers.appcore.xml.XMLHelper;
import seers.bugrepcompl.entity.regularparse.ParsedBugReport;

import java.io.File;
import java.util.Collection;

public class BugStackTraceParserTest {

	@Test
	public void testParseFirstTrace() throws Exception {

		Collection<File> xmlFiles = FileUtils.listFiles(new File("test_data/nlactions/bugs/"), null, false);

		for (File file : xmlFiles) {
			System.out.println("Trace in " + file.getName());

			ParsedBugReport bugReport = XMLHelper.readXML(ParsedBugReport.class, file);

			StackTrace trace = BugStackTraceParser.parseFirstTrace(bugReport);
			System.out.println(trace);

		}
	}

}
