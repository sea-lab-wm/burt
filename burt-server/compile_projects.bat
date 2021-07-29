
set CUR_DIR=%CD%

cd ..\burt-nlparser && call mvn clean install -DskipTests && @echo on
REM cd ..\crashscope && call mvn clean install -DskipTests && @echo on
cd ..\burt-quality-checker && call mvn clean install -DskipTests && @echo on

cd "%CUR_DIR%"

call mvnw clean compile