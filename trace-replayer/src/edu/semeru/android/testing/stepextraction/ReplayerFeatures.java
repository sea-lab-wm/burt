package edu.semeru.android.testing.stepextraction;

public class ReplayerFeatures {
	private int widthScreen;
	private int heightScreen;
	private String uiDumpLocation;
	private boolean correctAugScreen;
	private String executionType;
	
	ReplayerFeatures() {
		
	}
	
	public int getWidthScreen() {
		return widthScreen;
	}

	public void setWidthScreen(int widthScreen) {
		this.widthScreen = widthScreen;
	}

	public int getHeightScreen() {
		return heightScreen;
	}

	public void setHeightScreen(int heightScreen) {
		this.heightScreen = heightScreen;
	}

	public String getUiDumpLocation() {
		return uiDumpLocation;
	}

	public void setUiDumpLocation(String uiDumpLocation) {
		this.uiDumpLocation = uiDumpLocation;
	}

	public boolean isCorrectAugScreen() {
		return correctAugScreen;
	}

	public void setCorrectAugScreen(boolean correctAugScreen) {
		this.correctAugScreen = correctAugScreen;
	}

	public String getExecutionType() {
		return executionType;
	}

	public void setExecutionType(String executionType) {
		this.executionType = executionType;
	}
    
}
