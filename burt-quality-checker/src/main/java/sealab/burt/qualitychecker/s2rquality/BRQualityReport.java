package sealab.burt.qualitychecker.s2rquality;

import seers.bugrepcompl.entity.BugReport;

import java.util.List;

/**
 * Class that contains the information of the quality assessment of a bug report
 */
public class BRQualityReport {

    //execution token (same as used in the DeviceServer
    private String token;

    //bug report
    private BugReport bugReport;

    //app info
    private String appName;
    private String appVersion;

    //the quality feedback for each S2R
    private List<QualityFeedback> qualityFeedback;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setBugReport(BugReport bugReport) {
        this.bugReport = bugReport;
    }

    public void setS2RQualityFeedback(List<QualityFeedback> qualityFeedback) {
        this.qualityFeedback = qualityFeedback;
    }

    public BugReport getBugReport() {
        return bugReport;
    }

    public List<QualityFeedback> getS2RQualityFeedback() {
        return qualityFeedback;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
}
