package sealab.burt.nlparser.euler.actions.trace;

import com.google.gson.annotations.SerializedName;

public class TraceElement {

	@SerializedName("c")
	private String qualifiedClassName;
	@SerializedName("m")
	private String methodName;
	@SerializedName("f")
	private String fileName;
	@SerializedName("l")
	private Integer lineNumber;

	public TraceElement(String fileName, String qualifiedClassName, String methodName, Integer lineNumber) {
		super();
		this.fileName = fileName;
		this.qualifiedClassName = qualifiedClassName;
		this.methodName = methodName;
		this.lineNumber = lineNumber;
	}

	public String getFileName() {
		return fileName;
	}

	public String getQualifiedClassName() {
		return qualifiedClassName;
	}

	public String getMethodName() {
		return methodName;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setQualifiedClassName(String qualifiedClassName) {
		this.qualifiedClassName = qualifiedClassName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	@Override
	public String toString() {
		return "TraceElement [qualifiedClassName=" + qualifiedClassName + ", methodName=" + methodName + ", fileName="
				+ fileName + ", lineNumber=" + lineNumber + "]";
	}

	public int hashCode2() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((lineNumber == null) ? 0 : lineNumber.hashCode());
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result + ((qualifiedClassName == null) ? 0 : qualifiedClassName.hashCode());
		return result;
	}
	

	public boolean equalsRelaxed(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TraceElement other = (TraceElement) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		if (qualifiedClassName == null) {
			if (other.qualifiedClassName != null)
				return false;
		} else if (!qualifiedClassName.equals(other.qualifiedClassName))
			return false;
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TraceElement other = (TraceElement) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (lineNumber == null) {
			if (other.lineNumber != null)
				return false;
		} else if (!lineNumber.equals(other.lineNumber))
			return false;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		if (qualifiedClassName == null) {
			if (other.qualifiedClassName != null)
				return false;
		} else if (!qualifiedClassName.equals(other.qualifiedClassName))
			return false;
		return true;
	}

}
