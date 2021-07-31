
set CUR_DIR=%CD%

cd ..\burt-nlparser && call mvn clean install -DskipTests && @echo on
cd ..\burt-quality-checker && call mvn clean install -DskipTests && @echo on

cd "%CUR_DIR%"

call mvn package -DskipTests
REM call java -Dfile.encoding=UTF-8  -cp target\burt-tools-0.0.1.jar MainJSONGraphGenerator
call java  -cp target\burt-tools-0.0.1.jar MainJSONGraphGenerator