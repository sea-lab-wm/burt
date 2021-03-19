package sealab.burt.nlparser.euler.actions.trace;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StackTrace {

	@SerializedName("ex")
	private String exception;
	@SerializedName("mg")
	private String message;
	@SerializedName("el")
	private List<TraceElement> traceElements;
	@SerializedName("cb")
	private List<StackTrace> causedBy;

	public StackTrace(String exception, String message, List<TraceElement> traceElements, List<StackTrace> causedBy) {
		super();
		this.exception = exception;
		this.message = message;
		this.traceElements = traceElements;
		this.causedBy = causedBy;
	}

	public String getException() {
		return exception;
	}

	public String getMessage() {
		return message;
	}

	public List<TraceElement> getTraceElements() {
		return traceElements;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setTraceElements(List<TraceElement> traceElements) {
		this.traceElements = traceElements;
	}

	public List<StackTrace> getCausedBy() {
		return causedBy;
	}

	public void setCausedBy(List<StackTrace> causedBy) {
		this.causedBy = causedBy;
	}

	@Override
	public String toString() {
		return "[ex=" + exception + ", msg=" + message + ", el={\n\t" + getTraceElementsStr() + "},cb={\n"
				+ getCausedByStr() + "}]";
	}

	private String getCausedByStr() {
		StringBuilder builder = new StringBuilder();
		for (StackTrace trace : causedBy) {
			builder.append("Caused by: {");
			builder.append(trace.getException());
			builder.append(": ");
			builder.append(trace.getMessage());
			builder.append("\n\t");
			builder.append(trace.getTraceElementsStr());
			builder.append("},\n");
		}
		
		if (builder.length() != 0) {
			builder.delete(builder.length() - 3, builder.length());
			builder.append("}\n");
		}

		return builder.toString().trim();
	}

	private String getTraceElementsStr() {
		StringBuilder builder = new StringBuilder();
		for (TraceElement element : traceElements) {
			builder.append("\t");
			builder.append(element);
			builder.append(",\n");
		}

		if (builder.length() != 0) {
			builder.delete(builder.length() - 2, builder.length());
			builder.append("\n");
		}

		return builder.toString().trim();
	}

}
