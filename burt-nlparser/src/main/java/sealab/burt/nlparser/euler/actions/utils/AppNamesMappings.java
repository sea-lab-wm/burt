package sealab.burt.nlparser.euler.actions.utils;

import java.util.*;

public class AppNamesMappings {

    // the first element of each set is the representative/main one
    static List<LinkedHashSet<String>> APP_NAMES = new ArrayList<>();

    static {
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Aard Dictionary", "Aard-Dictionary", "aarddict", "Aard")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Mileage", "android-mileage", "mileage")));
//        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Time Tracker", "A Time Tracker", "A-Time-Tracker",
//                "TimeTracker",
//                "ATimeTracker")));

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
        //APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Omni-notes", "omni-notes")));
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
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("AnkiDroid", "Anki-Android", "ankidroid")));
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
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("fastnfitness", "Fast N Fitness")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("anglerslog")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("trainerapp", "Trainer App", "TrainerApp", "Trainer-App")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("streetcomplete")));

        //------------------------
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList( "lrkfm")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList( "pedometer")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList( "wifianalyzer")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList( "k-9mail")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList( "transistor")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList( "vinyl")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList( "openmap")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList( "odkcollect")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList( "cgeo")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList( "trebleshot")));

        //------------------------
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList( "aegis", "Aegis")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList( "phimp.me", "Phimp.me", "Phimp. Me")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList( "mementocalendar", "Mementocalendar", "Memento Calendar")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList( "c:geo", "C:geo")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList( "kissقاذفة", "Kiss", "kiss", "Kissقاذفة")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList( "vinyldebug", "Vinyldebug", "Vinyl Debug")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList( "ultrasonic", "Ultrasonic", "Ultra Sonic")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList( "chessclock", "Chessclock", "Chess Clock")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList( "noadplayer", "Noadplayer", "No Ad Player")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList( "trainingsschedule", "trainingsschedule", "Trainings Schedule")));
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
        //APP_NAMES_PACKAGES.put("AntennaPod", Arrays.asList("de.danoeh.antennapod.debug"));
        //APP_NAMES_PACKAGES.put("antennapod", Arrays.asList("de.danoeh.antennapod.debug"));
        APP_NAMES_PACKAGES.put("AntennaPod", Arrays.asList("de.danoeh.antennapod"));
        APP_NAMES_PACKAGES.put("antennapod", Arrays.asList("de.danoeh.antennapod"));
        APP_NAMES_PACKAGES.put("Android Token", Arrays.asList("uk.co.bitethebullet.android.token"));
        APP_NAMES_PACKAGES.put("android token", Arrays.asList("uk.co.bitethebullet.android.token"));
        APP_NAMES_PACKAGES.put("Grow Tracker", Arrays.asList("me.anon.grow"));
        APP_NAMES_PACKAGES.put("growtracker", Arrays.asList("me.anon.grow"));
        //APP_NAMES_PACKAGES.put("familyfinance", Arrays.asList("familyfinance"));
        APP_NAMES_PACKAGES.put("familyfinance", Arrays.asList("io.github.zwieback.familyfinance.debug"));
        APP_NAMES_PACKAGES.put("trickytripper", Arrays.asList("de.koelle.christian.trickytripper"));
        APP_NAMES_PACKAGES.put("calendula", Arrays.asList("es.usc.citius.servando.calendula"));
        APP_NAMES_PACKAGES.put("andotp", Arrays.asList("org.shadowice.flocke.andotp"));
        APP_NAMES_PACKAGES.put("inaturalist", Arrays.asList("org.inaturalist.android"));
        APP_NAMES_PACKAGES.put("files", Arrays.asList("me.zhanghai.android.files"));
        APP_NAMES_PACKAGES.put("omninotes", Arrays.asList("it.feio.android.omninotes"));
        APP_NAMES_PACKAGES.put("focus", Arrays.asList("org.mozilla.focus"));
        APP_NAMES_PACKAGES.put("gpstest", Arrays.asList("com.android.gpstest"));
        APP_NAMES_PACKAGES.put("streetcomplete", Arrays.asList("de.westnordost.streetcomplete"));
        APP_NAMES_PACKAGES.put("markor", Arrays.asList("net.gsantner.markor"));
        APP_NAMES_PACKAGES.put("openfoodfacts", Arrays.asList("openfoodfacts.github.scrachx.openfood"));
        APP_NAMES_PACKAGES.put("gnucash", Arrays.asList("org.gnucash.android"));
        APP_NAMES_PACKAGES.put("createpdf", Arrays.asList("swati4star.createpdf"));
        APP_NAMES_PACKAGES.put("hex", Arrays.asList("com.hexforhn.hex"));
        APP_NAMES_PACKAGES.put("transistor", Arrays.asList("org.y20k.transistor"));
        APP_NAMES_PACKAGES.put("fieldbook", Arrays.asList("com.fieldbook.tracker"));
        APP_NAMES_PACKAGES.put("kiss", Arrays.asList("fr.neamar.kiss.debug"));
        APP_NAMES_PACKAGES.put("anuto", Arrays.asList("ch.logixisland.anuto"));
        APP_NAMES_PACKAGES.put("atimetracker", Arrays.asList("com.markuspage.android.atimetracker"));
        APP_NAMES_PACKAGES.put("commons", Arrays.asList("fr.free.nrw.commons"));
        APP_NAMES_PACKAGES.put("phimpme", Arrays.asList("org.fossasia.phimpme"));
        APP_NAMES_PACKAGES.put("simplenote", Arrays.asList("com.automattic.simplenote"));
        APP_NAMES_PACKAGES.put("trainerapp", Arrays.asList("com.german_software_engineers.trainerapp"));
        //APP_NAMES_PACKAGES.put("fastnfitness", Arrays.asList("com.easyfitness.debug"));
        APP_NAMES_PACKAGES.put("fastnfitness", Arrays.asList("com.easyfitness"));
        APP_NAMES_PACKAGES.put("anglerslog", Arrays.asList("com.cohenadair.anglerslog"));

        APP_NAMES_PACKAGES.put("lrkfm", Arrays.asList("io.lerk.lrkFM"));
        APP_NAMES_PACKAGES.put("pedometer", Arrays.asList("org.secuso.privacyfriendlyactivitytracker"));
        APP_NAMES_PACKAGES.put("wifianalyzer", Arrays.asList("com.vrem.wifianalyzer"));
        APP_NAMES_PACKAGES.put("k-9mail", Arrays.asList("com.fsck.k9"));
        APP_NAMES_PACKAGES.put("vinyl", Arrays.asList("com.poupa.vinylmusicplayer.debug"));
        APP_NAMES_PACKAGES.put("openmap", Arrays.asList("org.osmdroid"));
        APP_NAMES_PACKAGES.put("odkcollect", Arrays.asList("org.odk.collect.android"));
        APP_NAMES_PACKAGES.put("cgeo", Arrays.asList("cgeo.geocaching"));
        APP_NAMES_PACKAGES.put("pdfconverter", Arrays.asList("swati4star.createpdf"));
        APP_NAMES_PACKAGES.put("ankidroid", Arrays.asList("com.ichi2.anki"));
        APP_NAMES_PACKAGES.put("trebleshot", Arrays.asList("com.genonbeta.TrebleShot"));

        APP_NAMES_PACKAGES.put("aegis", Arrays.asList("com.beemdevelopment.aegis"));
        APP_NAMES_PACKAGES.put("phimp.me", Arrays.asList("org.fossasia.phimpme"));
        APP_NAMES_PACKAGES.put("mementocalendar", Arrays.asList("com.alexstyl.specialdates"));
        APP_NAMES_PACKAGES.put("c:geo", Arrays.asList("cgeo.geocaching"));
        APP_NAMES_PACKAGES.put("kissقاذفة", Arrays.asList("fr.neamar.kiss.debug"));
        APP_NAMES_PACKAGES.put("vinyldebug", Arrays.asList("com.poupa.vinylmusicplayer.debug"));
        APP_NAMES_PACKAGES.put("ultrasonic", Arrays.asList("org.moire.ultrasonic"));
        APP_NAMES_PACKAGES.put("chessclock", Arrays.asList("com.chess.clock"));
        APP_NAMES_PACKAGES.put("noadplayer", Arrays.asList("com.droidheat.musicplayer"));
        APP_NAMES_PACKAGES.put("trainingsschedule", Arrays.asList("com.genonbeta.TrebleShot"));

        //APP_NAMES_PACKAGES.put("focus", Arrays.asList("focus"));
        //APP_NAMES_PACKAGES.put("gpstest", Arrays.asList("gpstest"));
        //APP_NAMES_PACKAGES.put("markor", Arrays.asList("markor"));
        //APP_NAMES_PACKAGES.put("materialfiles", Arrays.asList("materialfiles"));
        //APP_NAMES_PACKAGES.put("calendula", Arrays.asList("calendula"));
        //APP_NAMES_PACKAGES.put("inaturalist", Arrays.asList("inaturalist"));
        //APP_NAMES_PACKAGES.put("pdfconverter", Arrays.asList("pdfconverter"));


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


