
set CUR_DIR=%CD%

cd ..\burt-nlparser && call mvn clean install -DskipTests && @echo on
rem cd ..\crashscope && call mvn clean install -DskipTests && @echo on
cd ..\burt-quality-checker && call mvn clean install -DskipTests && @echo on

cd "%CUR_DIR%"

call mvnw spring-boot:run