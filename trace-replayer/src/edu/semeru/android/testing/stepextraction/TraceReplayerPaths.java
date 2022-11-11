package edu.semeru.android.testing.stepextraction;

//https://stackabuse.com/reading-and-writing-yaml-files-in-java-with-jackson/
public class TraceReplayerPaths {
	
	public TraceReplayerPaths(String androidSDKPath, String pythonScriptsPath, String aaptPath, String apkPath,
			String getEventFile, String outputFolder, String avdPort, String adbPort, int executionNum) {
        this.androidSDKPath = androidSDKPath;
        this.pythonScriptsPath = pythonScriptsPath;	
        this.aaptPath = aaptPath;
        this.apkPath = apkPath;
        this.getEventFile = getEventFile;
        this.outputFolder = outputFolder;
        this.avdPort = avdPort;
        this.adbPort = adbPort;
        this.executionNum = executionNum;
    }
	
	private String pythonScriptsPath;
    private String apkPath;
    private String getEventFile;
    private String outputFolder;
    private String avdPort;
    private String adbPort;
    private String aaptPath;
    private int executionNum;

    public int getExecutionNum() {
		return executionNum;
	}

	public void setExecutionNum(int executionNum) {
		this.executionNum = executionNum;
	}

	public String getAaptPath() {
		return aaptPath;
	}

	public void setAaptPath(String aaptPath) {
		this.aaptPath = aaptPath;
	}

	// Without a default constructor, Jackson will throw an exception
    public TraceReplayerPaths() {}

    private String androidSDKPath;
    public String getAndroidSDKPath() {
		return androidSDKPath;
	}

	public void setAndroidSDKPath(String androidSDKPath) {
		this.androidSDKPath = androidSDKPath;
	}

	public String getPythonScriptsPath() {
		return pythonScriptsPath;
	}

	public void setPythonScriptsPath(String pythonScriptsPath) {
		this.pythonScriptsPath = pythonScriptsPath;
	}

	public String getApkPath() {
		return apkPath;
	}

	public void setApkPath(String apkPath) {
		this.apkPath = apkPath;
	}

	public String getGetEventFile() {
		return getEventFile;
	}

	public void setGetEventFile(String getEventFile) {
		this.getEventFile = getEventFile;
	}

	public String getOutputFolder() {
		return outputFolder;
	}

	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}

	public String getAvdPort() {
		return avdPort;
	}

	public void setAvdPort(String avdPort) {
		this.avdPort = avdPort;
	}

	public String getAdbPort() {
		return adbPort;
	}

	public void setAdbPort(String adbPort) {
		this.adbPort = adbPort;
	}

    // Getters and setters

    @Override
    public String toString() {
        return "\nandroidSDKPath: " + androidSDKPath + "\npythonScriptsPath: " + pythonScriptsPath;
    }
}
