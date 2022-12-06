set JAVA_HOME="C:\PROGRA~1\Java\jdk-12.0.2"

set CUR_DIR=%CD%

cd burt-nlparser && call mvn clean install -DskipTests && @echo on
cd ..\trace-replayer\lib && call 0_install-maven-deps.bat && @echo on
cd ..\..\trace-replayer &&  call mvn clean install -DskipTests && @echo on
cd ..\crashscope && call mvn clean install -DskipTests && @echo on
cd ..\burt-quality-checker && call mvn clean install -DskipTests && @echo on
cd ..\burt-tools && call mvn clean package -DskipTests && @echo on
cd ..\burt-state-matching && call mvn clean package -DskipTests && @echo on

cd "%CUR_DIR%"
