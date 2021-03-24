package sealab.burt.qualitychecker.graph;

import java.io.Serializable;

/**
 * The persistent class for the app database table.
 *
 */
public class Appl implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    private String name;

    private String packageName;

    private String mainActivity;

    private String version;
    private String apkPath;

    /**
     * @return the apkPath
     */
    public String getApkPath() {
        return apkPath;
    }

    /**
     * @param apkPath the apkPath to set
     */
    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }

    public Appl() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    /**
     * @return the mainActivity
     */
    public String getMainActivity() {
        return mainActivity;
    }

    /**
     * @param mainActivity the mainActivity to set
     */
    public void setMainActivity(String mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public String toString() {
        return "app{" +
                "i=" + id +
                ", n='" + name + '\'' +
                ", p='" + packageName + '\'' +
                ", v='" + version + '\'' +
                '}';
    }
}
