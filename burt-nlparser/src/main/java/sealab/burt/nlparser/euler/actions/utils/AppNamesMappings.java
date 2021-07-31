package sealab.burt.nlparser.euler.actions.utils;

import java.util.*;

public class AppNamesMappings {

    // the first element of each set is the representative/main one
    static List<LinkedHashSet<String>> APP_NAMES = new ArrayList<>();

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

    }

    public static String normalizeAppName(String appName) {
        LinkedHashSet<String> appNames = getAppNames(appName);
        if (appNames == null) return null;
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

    //-----------------------------------------------

    private static final HashMap<String, List<String>> APP_NAMES_PACKAGES;
    private static final HashMap<String, List<String>> PACKAGE_APP_NAMES;

    static {
        APP_NAMES_PACKAGES = new LinkedHashMap<>();

        APP_NAMES_PACKAGES.put("aarddict", Arrays.asList("aarddict.android"));
        APP_NAMES_PACKAGES.put("adsdroid", Arrays.asList("hu.vsza.adsdroid"));
        APP_NAMES_PACKAGES.put("AnagramSolver", Arrays.asList("com.as.anagramsolver"));
        APP_NAMES_PACKAGES.put("ATimeTracker", Arrays.asList("com.markuspage.android.atimetracker", "net.ser1" +
                ".timetracker"));
        APP_NAMES_PACKAGES.put("time tracker", Arrays.asList("com.markuspage.android.atimetracker", "net.ser1" +
                ".timetracker"));

        APP_NAMES_PACKAGES.put("BMI_Calculator", Arrays.asList("com.zola.bmi"));

        APP_NAMES_PACKAGES.put("car-report", Arrays.asList("me.kuehle.carreport"));
        APP_NAMES_PACKAGES.put("cardgamescores", Arrays.asList("org.systemcall.scores"));

        APP_NAMES_PACKAGES.put("document-viewer", Arrays.asList("org.sufficientlysecure.viewer"));
        APP_NAMES_PACKAGES.put("droid-comic-viewer", Arrays.asList("net.androidcomics.acv"));

        APP_NAMES_PACKAGES.put("Droid Weight", Arrays.asList("de.delusions.measure"));
        APP_NAMES_PACKAGES.put("droidweight", Arrays.asList("de.delusions.measure"));

        APP_NAMES_PACKAGES.put("eyeCam", Arrays.asList("ch.hsr.eyecam"));

        APP_NAMES_PACKAGES.put("GnuCash", Arrays.asList("org.gnucash.android"));
        APP_NAMES_PACKAGES.put("gnucash-android", Arrays.asList("org.gnucash.android"));
        APP_NAMES_PACKAGES.put("gnucash", Arrays.asList("org.gnucash.android"));

        APP_NAMES_PACKAGES.put("Mileage", Arrays.asList("com.evancharlton.mileage"));
        APP_NAMES_PACKAGES.put("mileage", Arrays.asList("com.evancharlton.mileage"));
        APP_NAMES_PACKAGES.put("android-mileage", Arrays.asList("com.evancharlton.mileage"));

        APP_NAMES_PACKAGES.put("netmbuddy", Arrays.asList("free.yhc.netmbuddy"));
        APP_NAMES_PACKAGES.put("notepad_banderlabs", Arrays.asList("bander.notepad"));

        APP_NAMES_PACKAGES.put("Olam", Arrays.asList("com.olam"));
        APP_NAMES_PACKAGES.put("openintents", Arrays.asList("org.openintents.notepad"));

        APP_NAMES_PACKAGES.put("schedule-campfahrplan",
                Arrays.asList("nerd.tuxmobil.fahrplan.camp", "nerd.tuxmobil.fahrplan.congress"));
        APP_NAMES_PACKAGES.put("AntennaPod", Arrays.asList("de.danoeh.antennapod.debug"));
        APP_NAMES_PACKAGES.put("antennapod", Arrays.asList("de.danoeh.antennapod.debug"));
        APP_NAMES_PACKAGES.put("Android Token", Arrays.asList("uk.co.bitethebullet.android.token"));
        APP_NAMES_PACKAGES.put("android token", Arrays.asList("uk.co.bitethebullet.android.token"));
        APP_NAMES_PACKAGES.put("growtracker", Arrays.asList("me.anon.grow"));


        //----------------------------

        PACKAGE_APP_NAMES = new LinkedHashMap<>();

        APP_NAMES_PACKAGES.forEach((appName, packages) -> {
            for (String pack : packages) {
                List<String> appNames = PACKAGE_APP_NAMES.get(pack);
                if (appNames == null)
                    appNames = new ArrayList<>();
                appNames.add(appName);
                PACKAGE_APP_NAMES.put(pack, appNames);
            }
        });
    }

    public static List<String> getPackageNames(String appName){
       return APP_NAMES_PACKAGES.get(appName);
    }


    public static List<String> getAppNamesFromPackage(String packageName){
        return PACKAGE_APP_NAMES.get(packageName);
    }

    /*public static App getApp(String appName, String appVersion, EntityManager em) throws SQLException {

        // -----------------------------------------
        List<App> apps = new ArrayList<>();
        AppDao appDao = new AppDao();
        List<String> packages = APP_NAMES_PACKAGES.get(appName);
        if (packages == null || packages.isEmpty()) {
            throw new RuntimeException("No packages found for: " + appName + "_" + appVersion);
        }

        if (appVersion != null) {
            for (String packageName : packages) {
                App aliasApps = appDao.getUniqueByPackage(packageName, appVersion, em);
                if (aliasApps != null) {
                    apps.add(aliasApps);
                }
            }
        } else {
            for (String packageName : packages) {
                List<App> aliasApps = appDao.getUniqueByPackage(packageName, em);
                apps.addAll(aliasApps);
            }
        }

        if (apps.isEmpty()) {
            throw new RuntimeException("App not found: " + appName + "_" + appVersion);
        } else if (apps.size() == 1) {
            return apps.get(0);
        } else {
            throw new RuntimeException("Multiple versions for app " + appName + "_" + appVersion);
        }

    }*/


}
