package sealab.burt.nlparser.euler.actions.utils;

import java.util.*;

// FIXME: Externalize all app and package names
public class AppNamesMappings {

    // the first element of each set is the representative/main one
    static LinkedHashSet<LinkedHashSet<String>> APP_NAMES = new LinkedHashSet<>();

    static {
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Aard Dictionary", "Aard-Dictionary", "aarddict", "Aard")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Mileage", "android-mileage", "mileage")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Time Tracker", "A Time Tracker", "A-Time-Tracker",
                "TimeTracker",
                "ATimeTracker")));

        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Card Game Scores", "cardgamescores", "CardGameScores",
                "Card Games", "Cardgames", "Card-Game-Scores")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Car Report", "Car-Report", "car-report")));
        APP_NAMES.add(new LinkedHashSet<>(
                Arrays.asList("Document Viewer", "Document-Viewer", "document-viewer", "Dokumentenbetrachter",
                        "Document Viewer", "DocumentViewer")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("DroidWeight", "droidweight", "Droid Weight")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("GnuCash", "gnucash-android", "GnuCash - beta", "Gnucash",
                "gnucash")));

        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("NetMBuddy", "netmbuddy")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("NotePad", "notepad_banderlabs")));
        APP_NAMES.add(
                new LinkedHashSet<>(Arrays.asList("Schedule", "schedule-campfahrplan", "31C3 Schedule", "Camp 2015")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Android Comic Viewer", "droid-comic-viewer", "ACV")));

        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Android Token", "Android-Token", "Android token",
                "androidtoken", "Android-token")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("RedReader", "redreader")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("ODK-Collect", "ODK Collect", "collect")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("KeePassDroid", "keepassdroid")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Typpy-Tipper", "Typpy Tipper", "tippytipper")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Omni-notes", "omni-notes")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Amaze", "amaze")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Vanilla", "vanilla")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("AntennaPod", "antennapod")));

        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Box Android SDK", "box-android-sdk")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Exo Player", "ExoPlayer")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Japanese Traditional Time", "jtt_android")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("K-9 Mail", "k-9")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Twidere", "Twidere-Android")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("NextCloud", "nexcloud")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("BeHe ExploreR", "behe-explorer", "BeHe Explorer")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Tachiyomi", "tachiyomi")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Android D-B-MVP", "android-d-b-mvp")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("OCReader", "ocreader")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("AnkiDroid", "Anki-Android")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("ScreenRecorder", "screenrecorder")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("growtracker", "Grow Tracker", "GrowTracker")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("familyfinance")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("focus")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("gpstest")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("markor")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("materialfiles")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("calendula")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("inaturalist")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("pdfconverter")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("trickytripper")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("andotp")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("files")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("omninotes")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("openfoodfacts")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("createpdf")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("hex")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("fieldbook")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("kiss")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("anuto")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("phimpme")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("atimetracker")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("commons")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("fastnfitness")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("anglerslog")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("trainerapp")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("streetcomplete")));

    }

    public static String normalizeAppName(String appName) {
        LinkedHashSet<String> appNames = getAppNames(appName);
        if (appNames == null)
            return null;
        return new ArrayList<>(appNames).get(0);
    }

    public static LinkedHashSet<String> getAppNames(String appName) {
        Optional<LinkedHashSet<String>> appNameSet = APP_NAMES.stream().filter(aps -> aps.contains(appName))
                .findFirst();

        if (appNameSet.isEmpty()) {
            return null;
        }

        return appNameSet.get();
    }

    // -----------------------------------------------

    private static final HashMap<String, LinkedHashSet<String>> APP_NAMES_PACKAGES;
    private static final HashMap<String, LinkedHashSet<String>> PACKAGE_APP_NAMES;

    static {
        APP_NAMES_PACKAGES = new LinkedHashMap<>();

        APP_NAMES_PACKAGES.put("aarddict", new LinkedHashSet<>(Arrays.asList("aarddict.android")));
        APP_NAMES_PACKAGES.put("adsdroid", new LinkedHashSet<>(Arrays.asList("hu.vsza.adsdroid")));
        APP_NAMES_PACKAGES.put("AnagramSolver", new LinkedHashSet<>(Arrays.asList("com.as.anagramsolver")));
        APP_NAMES_PACKAGES.put("ATimeTracker",
                new LinkedHashSet<>(Arrays.asList("com.markuspage.android.atimetracker", "net.ser1" +
                        ".timetracker")));
        APP_NAMES_PACKAGES.put("time tracker",
                new LinkedHashSet<>(Arrays.asList("com.markuspage.android.atimetracker", "net.ser1" +
                        ".timetracker")));

        APP_NAMES_PACKAGES.put("BMI_Calculator", new LinkedHashSet<>(Arrays.asList("com.zola.bmi")));

        APP_NAMES_PACKAGES.put("car-report", new LinkedHashSet<>(Arrays.asList("me.kuehle.carreport")));
        APP_NAMES_PACKAGES.put("cardgamescores", new LinkedHashSet<>(Arrays.asList("org.systemcall.scores")));

        APP_NAMES_PACKAGES.put("document-viewer", new LinkedHashSet<>(Arrays.asList("org.sufficientlysecure.viewer")));
        APP_NAMES_PACKAGES.put("droid-comic-viewer", new LinkedHashSet<>(Arrays.asList("net.androidcomics.acv")));

        APP_NAMES_PACKAGES.put("Droid Weight", new LinkedHashSet<>(Arrays.asList("de.delusions.measure")));
        APP_NAMES_PACKAGES.put("droidweight", new LinkedHashSet<>(Arrays.asList("de.delusions.measure")));

        APP_NAMES_PACKAGES.put("eyeCam", new LinkedHashSet<>(Arrays.asList("ch.hsr.eyecam")));

        APP_NAMES_PACKAGES.put("GnuCash", new LinkedHashSet<>(Arrays.asList("org.gnucash.android")));
        APP_NAMES_PACKAGES.put("gnucash-android", new LinkedHashSet<>(Arrays.asList("org.gnucash.android")));
        APP_NAMES_PACKAGES.put("gnucash", new LinkedHashSet<>(Arrays.asList("org.gnucash.android")));

        APP_NAMES_PACKAGES.put("Mileage", new LinkedHashSet<>(Arrays.asList("com.evancharlton.mileage")));
        APP_NAMES_PACKAGES.put("mileage", new LinkedHashSet<>(Arrays.asList("com.evancharlton.mileage")));
        APP_NAMES_PACKAGES.put("android-mileage", new LinkedHashSet<>(Arrays.asList("com.evancharlton.mileage")));

        APP_NAMES_PACKAGES.put("netmbuddy", new LinkedHashSet<>(Arrays.asList("free.yhc.netmbuddy")));
        APP_NAMES_PACKAGES.put("notepad_banderlabs", new LinkedHashSet<>(Arrays.asList("bander.notepad")));

        APP_NAMES_PACKAGES.put("Olam", new LinkedHashSet<>(Arrays.asList("com.olam")));
        APP_NAMES_PACKAGES.put("openintents", new LinkedHashSet<>(Arrays.asList("org.openintents.notepad")));

        APP_NAMES_PACKAGES.put("schedule-campfahrplan",
                new LinkedHashSet<>(Arrays.asList("nerd.tuxmobil.fahrplan.camp", "nerd.tuxmobil.fahrplan.congress")));
        APP_NAMES_PACKAGES.put("AntennaPod", new LinkedHashSet<>(Arrays.asList("de.danoeh.antennapod.debug")));
        APP_NAMES_PACKAGES.put("antennapod", new LinkedHashSet<>(Arrays.asList("de.danoeh.antennapod.debug")));
        APP_NAMES_PACKAGES.put("Android Token",
                new LinkedHashSet<>(Arrays.asList("uk.co.bitethebullet.android.token")));
        APP_NAMES_PACKAGES.put("android token",
                new LinkedHashSet<>(Arrays.asList("uk.co.bitethebullet.android.token")));
        APP_NAMES_PACKAGES.put("Grow Tracker", new LinkedHashSet<>(Arrays.asList("me.anon.grow")));
        APP_NAMES_PACKAGES.put("growtracker", new LinkedHashSet<>(Arrays.asList("me.anon.grow")));

        APP_NAMES_PACKAGES.put("familyfinance",
                new LinkedHashSet<>(Arrays.asList("io.github.zwieback.familyfinance.debug")));
        APP_NAMES_PACKAGES.put("trickytripper",
                new LinkedHashSet<>(Arrays.asList("de.koelle.christian.trickytripper")));
        APP_NAMES_PACKAGES.put("calendula", new LinkedHashSet<>(Arrays.asList("es.usc.citius.servando.calendula")));
        APP_NAMES_PACKAGES.put("andotp", new LinkedHashSet<>(Arrays.asList("org.shadowice.flocke.andotp")));
        APP_NAMES_PACKAGES.put("inaturalist", new LinkedHashSet<>(Arrays.asList("org.inaturalist.android")));
        APP_NAMES_PACKAGES.put("files", new LinkedHashSet<>(Arrays.asList("me.zhanghai.android.files")));
        APP_NAMES_PACKAGES.put("omninotes", new LinkedHashSet<>(Arrays.asList("it.feio.android.omninotes")));
        APP_NAMES_PACKAGES.put("focus", new LinkedHashSet<>(Arrays.asList("org.mozilla.focus")));
        APP_NAMES_PACKAGES.put("gpstest", new LinkedHashSet<>(Arrays.asList("com.android.gpstest")));
        APP_NAMES_PACKAGES.put("streetcomplete", new LinkedHashSet<>(Arrays.asList("de.westnordost.streetcomplete")));
        APP_NAMES_PACKAGES.put("markor", new LinkedHashSet<>(Arrays.asList("net.gsantner.markor")));
        APP_NAMES_PACKAGES.put("openfoodfacts",
                new LinkedHashSet<>(Arrays.asList("openfoodfacts.github.scrachx.openfood")));
        APP_NAMES_PACKAGES.put("gnucash", new LinkedHashSet<>(Arrays.asList("org.gnucash.android")));
        APP_NAMES_PACKAGES.put("createpdf", new LinkedHashSet<>(Arrays.asList("swati4star.createpdf")));
        APP_NAMES_PACKAGES.put("hex", new LinkedHashSet<>(Arrays.asList("com.hexforhn.hex")));
        APP_NAMES_PACKAGES.put("transistor", new LinkedHashSet<>(Arrays.asList("org.y20k.transistor")));
        APP_NAMES_PACKAGES.put("fieldbook", new LinkedHashSet<>(Arrays.asList("com.fieldbook.tracker")));
        APP_NAMES_PACKAGES.put("kiss", new LinkedHashSet<>(Arrays.asList("fr.neamar.kiss.debug")));
        APP_NAMES_PACKAGES.put("anuto", new LinkedHashSet<>(Arrays.asList("ch.logixisland.anuto")));
        APP_NAMES_PACKAGES.put("atimetracker",
                new LinkedHashSet<>(Arrays.asList("com.markuspage.android.atimetracker")));
        APP_NAMES_PACKAGES.put("commons", new LinkedHashSet<>(Arrays.asList("fr.free.nrw.commons")));
        APP_NAMES_PACKAGES.put("phimpme", new LinkedHashSet<>(Arrays.asList("org.fossasia.phimpme")));
        APP_NAMES_PACKAGES.put("simplenote", new LinkedHashSet<>(Arrays.asList("com.automattic.simplenote")));
        APP_NAMES_PACKAGES.put("trainerapp",
                new LinkedHashSet<>(Arrays.asList("com.german_software_engineers.trainerapp")));
        APP_NAMES_PACKAGES.put("fastnfitness", new LinkedHashSet<>(Arrays.asList("com.easyfitness")));
        APP_NAMES_PACKAGES.put("anglerslog", new LinkedHashSet<>(Arrays.asList("com.cohenadair.anglerslog")));

        // ----------------------------

        PACKAGE_APP_NAMES = new LinkedHashMap<>();

        APP_NAMES_PACKAGES.forEach((appName, packages) -> {
            for (String pack : packages) {
                LinkedHashSet<String> appNames = PACKAGE_APP_NAMES.get(pack);
                if (appNames == null)
                    appNames = new LinkedHashSet<>();
                appNames.add(appName);
                PACKAGE_APP_NAMES.put(pack, appNames);
            }
        });
    }

    public static List<String> getPackageNames(String appName) {
        LinkedHashSet<String> packages = APP_NAMES_PACKAGES.get(appName);
        if (packages == null)
            return null;
        return new ArrayList<>(packages);
    }

    public static List<String> getAppNamesFromPackage(String packageName) {
        return new ArrayList<>(PACKAGE_APP_NAMES.get(packageName));
    }

    public static void addAppNameForPackage(String appName, String packageName) {

        // --------------

        LinkedHashSet<String> appNames = PACKAGE_APP_NAMES.get(packageName);
        if (appNames == null)
            appNames = new LinkedHashSet<>();
        appNames.add(appName);
        PACKAGE_APP_NAMES.put(packageName, appNames);

        // --------------

        LinkedHashSet<String> packages = APP_NAMES_PACKAGES.get(appName);
        if (packages == null)
            packages = new LinkedHashSet<>();
        packages.add(packageName);
        APP_NAMES_PACKAGES.put(appName, packages);

        // ---------------

        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList(appName)));

    }

    /*
     * public static App getApp(String appName, String appVersion, EntityManager em)
     * throws SQLException {
     * 
     * // -----------------------------------------
     * List<App> apps = new ArrayList<>();
     * AppDao appDao = new AppDao();
     * List<String> packages = APP_NAMES_PACKAGES.get(appName);
     * if (packages == null || packages.isEmpty()) {
     * throw new RuntimeException("No packages found for: " + appName + "_" +
     * appVersion);
     * }
     * 
     * if (appVersion != null) {
     * for (String packageName : packages) {
     * App aliasApps = appDao.getUniqueByPackage(packageName, appVersion, em);
     * if (aliasApps != null) {
     * apps.add(aliasApps);
     * }
     * }
     * } else {
     * for (String packageName : packages) {
     * List<App> aliasApps = appDao.getUniqueByPackage(packageName, em);
     * apps.addAll(aliasApps);
     * }
     * }
     * 
     * if (apps.isEmpty()) {
     * throw new RuntimeException("App not found: " + appName + "_" + appVersion);
     * } else if (apps.size() == 1) {
     * return apps.get(0);
     * } else {
     * throw new RuntimeException("Multiple versions for app " + appName + "_" +
     * appVersion);
     * }
     * 
     * }
     */

}
