
set CUR_DIR=%CD%

set REPOSITORIES_PATH=C:\Users\ojcch\Documents\Repositories\projects
rem set REPOSITORIES_PATH=D:\Projects

set APPCORE_REPO_PATH=%REPOSITORIES_PATH%\appcore
set TXT_ANALYZER_REPO_PATH=%REPOSITORIES_PATH%\text-analyzer
set BUG_REPORT_COMPLETION_REPO_PATH=%REPOSITORIES_PATH%\bug_report_completion
rem set ANDROID_CORE_REPO_PATH=%REPOSITORIES_PATH%\SEMERU-Code\Android-Core

rem repo update
cd "%APPCORE_REPO_PATH%" && git pull
cd "%TXT_ANALYZER_REPO_PATH%" && git pull
cd "%BUG_REPORT_COMPLETION_REPO_PATH%" && git pull
rem cd "%ANDROID_CORE_REPO_PATH%" && git pull origin burt-jdk8plus


REM project building
cd "%APPCORE_REPO_PATH%\appcore" && call gradlew clean testClasses install && @echo on
cd "%TXT_ANALYZER_REPO_PATH%\text-analyzer" && call gradlew clean testClasses install && @echo on
cd "%BUG_REPORT_COMPLETION_REPO_PATH%\code\bug_report_coding" && call gradlew clean testClasses install && @echo on
cd "%BUG_REPORT_COMPLETION_REPO_PATH%\code\bug_report_patterns" && call gradlew clean testClasses install && @echo on
cd "%BUG_REPORT_COMPLETION_REPO_PATH%\code\bug_report_classifier" && call gradlew clean testClasses install && @echo on
cd "%BUG_REPORT_COMPLETION_REPO_PATH%\code\bug_report_parser\bugparser" && call gradlew clean testClasses install && @echo on
rem cd "%ANDROID_CORE_REPO_PATH%" && call mvn clean install -DskipTests && @echo on

cd "%CUR_DIR%"

cd ..\burt-nlparser && call mvn clean install -DskipTests && @echo on
cd ..\trace-replayer\lib && 0_install-maven-deps.bat
cd ..\..\trace-replayer && mvn clean install -DskipTests && @echo on
cd ..\crashscope && call mvn clean install -DskipTests && @echo on
cd ..\burt-quality-checker && call mvn clean install -DskipTests && @echo on

cd "%CUR_DIR%"

call mvnw spring-boot:run