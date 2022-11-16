package sealab.burt.server.actions.newapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import edu.semeru.android.core.entity.model.fusion.Execution;
import lombok.extern.slf4j.Slf4j;
import sealab.burt.BurtConfigPaths;
import sealab.burt.qualitychecker.JSONGraphReader;
import sealab.burt.server.actions.appselect.SelectAppAction;

@Slf4j
public class NewAppController {

    public static void addNewApp(String sessionId, MultipartFile appIcon, MultipartFile crashScopeZip,
            MultipartFile traceReplayerZip) throws Exception {

        boolean error = true;
        String packageName = "";
        String appVersion = "";

        Paths.get(BurtConfigPaths.tempPath).toFile().mkdir();

        // ---------------
        // Decompress CS data in a temp folder
        Path crashScopeZipLocal = Paths.get(BurtConfigPaths.tempPath,
                sessionId + "--" + crashScopeZip.getOriginalFilename());
        Path csTargetDir = Paths.get(BurtConfigPaths.tempPath,
                FilenameUtils.removeExtension(crashScopeZipLocal.toFile().getName()));
        FileUtils.forceMkdir(csTargetDir.toFile());
        crashScopeZip.transferTo(crashScopeZipLocal);
        unzipFile(crashScopeZipLocal, csTargetDir);

        try {

            // ---------------
            // Try to find this information based on the CS data


            //validate basic information (package, name, version)
            List<Object> csAppInfo = validateAndGetAppInfo(csTargetDir);

            packageName = csAppInfo.get(0).toString();
            String appName = csAppInfo.get(1).toString();
            appVersion = csAppInfo.get(2).toString();

            // ------------------

            // Upload the app icon
            String extension = FilenameUtils.getExtension(appIcon.getOriginalFilename());
            Path iconPathLocal = Paths.get(BurtConfigPaths.appLogosPath,
                    String.format("%s-%s.%s", packageName, appVersion, extension));
            // if (iconPathLocal.toFile().exists())
            //     throw new RuntimeException("File already exists: " + iconPathLocal);
            appIcon.transferTo(iconPathLocal); //this would replace the file

            // ------------------
            // Move CS data to corresponding folder

            Boolean isAppRootFolder = (Boolean) csAppInfo.get(3);

            File destDir = Paths
                    .get(BurtConfigPaths.crashScopeDataPath, String.format("%s-%s", packageName, appVersion)).toFile();
            File srcDir = csTargetDir.toFile();
            if (!isAppRootFolder) {
                srcDir = csTargetDir.toFile().listFiles()[0];
            }

            // replace the directory
            if(destDir.exists())
                FileUtils.deleteDirectory(destDir);
            FileUtils.moveDirectory(srcDir, destDir);

            // -----------------
            // Decompress TR data into corresponding folder

            if (traceReplayerZip != null) {

                //decompress on the temp folder
                Path traceReplayerZipLocal = Paths.get(BurtConfigPaths.tempPath,
                        sessionId + "--" + traceReplayerZip.getOriginalFilename());
                Path trTargetDir = Paths.get(BurtConfigPaths.tempPath,
                        FilenameUtils.removeExtension(traceReplayerZipLocal.toFile().getName()));
                FileUtils.forceMkdir(trTargetDir.toFile());
                traceReplayerZip.transferTo(traceReplayerZipLocal);
                unzipFile(traceReplayerZipLocal, trTargetDir);

                //validate basic information (package, name, version)
                List<Object> trAppInfo = validateAndGetAppInfo(trTargetDir);
                Boolean trIsAppRootFolder = (Boolean) trAppInfo.get(3);

                // move data
                File trDestDir = Paths
                        .get(BurtConfigPaths.traceReplayerDataPath, String.format("%s-%s", packageName, appVersion))
                        .toFile();
                File trSrcDir = trTargetDir.toFile();
                if (!trIsAppRootFolder) {
                    trSrcDir = trTargetDir.toFile().listFiles()[0];
                }

                // replace the directory
                if(trDestDir.exists())
                    FileUtils.deleteDirectory(trDestDir);
                FileUtils.moveDirectory(trSrcDir, trDestDir);

            }

            // -------------------

            // load the apps and test the graph can be created
            SelectAppAction.generateAppData();
            JSONGraphReader.getGraph(appName, appVersion); //this validates the execution data is correct

            // Valid file uploaded successfully
            error = false;

        } finally {
            // clean files/dir if corrupted file uploaded
            if(error){
                cleanFiles("corruptedAppLogoPath", sessionId, packageName, appVersion);
                cleanFiles("corruptedCSPath", sessionId, packageName, appVersion);
                cleanFiles("corruptedTRPath", sessionId, packageName, appVersion);
            }

            // clean up temp files/dir
            cleanFiles("tempPath", sessionId, packageName, appVersion);
        }

    }

    private static void cleanFiles(String pathType, String sessionId, String packageName, String appVersion) throws IOException {
        List<Path> tempFiles = null;
        StringBuilder sd = new StringBuilder();
        sd.append(packageName).append("-").append(appVersion);
        switch (pathType){
            case "tempPath":
                tempFiles = Files.find(Paths.get(BurtConfigPaths.tempPath), 1,
                                (path, attr) -> path.toFile().getName().startsWith(sessionId))
                        .collect(Collectors.toList());
                break;
            case "corruptedAppLogoPath":
                tempFiles = Files.find(Paths.get(BurtConfigPaths.appLogosPath), 1,
                                (path, attr) -> path.toFile().getName().startsWith(sd.toString()))
                        .collect(Collectors.toList());
                break;
            case "corruptedCSPath":
                tempFiles = Files.find(Paths.get(BurtConfigPaths.crashScopeDataPath), 1,
                                (path, attr) -> path.toFile().getName().startsWith(sd.toString()))
                        .collect(Collectors.toList());
                break;
            case "corruptedTRPath":
                tempFiles = Files.find(Paths.get(BurtConfigPaths.traceReplayerDataPath), 1,
                                (path, attr) -> path.toFile().getName().startsWith(sd.toString()))
                        .collect(Collectors.toList());
                break;
        }

        for (Path tempFile : tempFiles) {
            if(tempFile.toFile().isDirectory())
                FileUtils.deleteDirectory(tempFile.toFile());
            else
                tempFile.toFile().delete();
        }
    }

    private static List<Object> validateAndGetAppInfo(Path targetDir) throws IOException {

        // --------------
        // Look for execution files

        Boolean isAppRootFolder = true;
        List<Path> executionFiles = Files.find(targetDir, 1,
                (path, attr) -> path.toFile().getName().startsWith("Execution-"))
                .collect(Collectors.toList());

        if (executionFiles == null || executionFiles.isEmpty()) {

            File[] listFiles = targetDir.toFile().listFiles();
            if (listFiles == null || listFiles.length == 0)
                throw new RuntimeException("Folder is empty: " + targetDir.toAbsolutePath().toString());

            isAppRootFolder = false;
            executionFiles = Files.find(listFiles[0].toPath(), 1,
                    (path, attr) -> path.toFile().getName().startsWith("Execution-"))
                    .collect(Collectors.toList());

            if (executionFiles == null || executionFiles.isEmpty())
                throw new RuntimeException("There are no execution files in " + targetDir.toAbsolutePath().toString());

        }

        // ------------

        JsonReader reader = new JsonReader(new InputStreamReader(
                new FileInputStream(executionFiles.get(0).toFile()), StandardCharsets.UTF_8));

        // De-serialize the Execution object.
        Gson gson = new Gson();
        Execution execution = gson.fromJson(reader, Execution.class);

        return Arrays.asList(execution.getApp().getPackageName(), execution.getApp().getName(),
                execution.getApp().getVersion(), isAppRootFolder);
    }

    private static void unzipFile(Path fileToUzip, Path targetDir) throws ZipException, IOException {

        // Open the file
        try (ZipFile zip = new ZipFile(fileToUzip.toFile())) {

            Enumeration<? extends ZipEntry> entries = zip.entries();

            // Iterate over entries
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                //log.debug("Zip entry: " + entry.getName());

                // don't process the MACOSX directory
                if (entry.getName().contains("MACOSX")) {
                    continue;
                }

                File f = new File(targetDir.resolve(Path.of(entry.getName())).toString());

                // If directory then create a new directory in uncompressed folder
                if (entry.isDirectory()) {
                    if (!f.isDirectory() && !f.mkdirs()) {
                        throw new IOException("Failed to create directory " + f);
                    }
                }

                // Else create the file
                else {
                    File parent = f.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    try (InputStream in = zip.getInputStream(entry)) {
                        if (!f.exists()) {
                            Files.copy(in, f.toPath());
                        }
                    }

                }
            }
        }
    }

}
