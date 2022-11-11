package edu.semeru.android.testing;

import java.util.ArrayList;

public class CrashScopeConfig {
	public CrashScopeConfig(String apkFile, String aaptPath, String outputFolder, String scriptsPath,
			String androidSDKPath, String avdPort, String adbPort, ArrayList<String> GUITraversal, 
			String textEntry, int executionNum) {
		super();
		this.apkFile = apkFile;
		this.aaptPath = aaptPath;
		this.outputFolder = outputFolder;
		this.scriptsPath = scriptsPath;
		this.androidSDKPath = androidSDKPath;
		this.avdPort = avdPort;
		this.adbPort = adbPort;
		this.GUITraversal = GUITraversal;
		this.textEntry = textEntry;
		this.executionNum = executionNum;
	}
	
	private String apkFile;
	private String aaptPath;
	private String outputFolder;
	private String scriptsPath;
	private String androidSDKPath;
	private String avdPort;
	private String adbPort;
	private ArrayList<String> GUITraversal;
	private String textEntry;
	private int executionNum;
	
	public int getExecutionNum() {
		return executionNum;
	}
	public void setExecutionNum(int executionNum) {
		this.executionNum = executionNum;
	}
	public ArrayList<String> getGUITraversal() {
		return GUITraversal;
	}
	public void setGUITraversal(ArrayList<String> gUITraversal) {
		GUITraversal = gUITraversal;
	}
	public String getTextEntry() {
		return textEntry;
	}
	public void setTextEntry(String textEntry) {
		this.textEntry = textEntry;
	}
	
	public String getApkFile() {
		return apkFile;
	}
	public void setApkFile(String apkFile) {
		this.apkFile = apkFile;
	}
	public String getAaptPath() {
		return aaptPath;
	}
	public void setAaptPath(String aaptPath) {
		this.aaptPath = aaptPath;
	}
	public String getOutputFolder() {
		return outputFolder;
	}
	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}
	public String getScriptsPath() {
		return scriptsPath;
	}
	public void setScriptsPath(String scriptsPath) {
		this.scriptsPath = scriptsPath;
	}
	public String getAndroidSDKPath() {
		return androidSDKPath;
	}
	public void setAndroidSDKPath(String androidSDKPath) {
		this.androidSDKPath = androidSDKPath;
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
	
	
}
