#!/bin/bash
set -x #echo on

export CUR_DIR=`pwd`
# export REPOSITORIES_PATH=C:\Users\ojcch\Documents\Repositories\projects
export REPOSITORIES_PATH=/Users/yangsong/csci-435-burt/

#
#
export APPCORE_REPO_PATH=$REPOSITORIES_PATH/appcore
export TXT_ANALYZER_REPO_PATH=$REPOSITORIES_PATH/text-analyzer
export BUG_REPORT_COMPLETION_REPO_PATH=$REPOSITORIES_PATH/bug_report_completion
#export ANDROID_CORE_REPO_PATH=$REPOSITORIES_PATH/SEMERU-Code/Android-Core
#
#repo update
cd $APPCORE_REPO_PATH && git pull
cd $TXT_ANALYZER_REPO_PATH && git pull
cd $BUG_REPORT_COMPLETION_REPO_PATH && git pull
#cd "%ANDROID_CORE_REPO_PATH%" && git pull origin burt-jdk8plus
#
#
# project building
cd $APPCORE_REPO_PATH/appcore && ./gradlew clean testClasses install
cd $TXT_ANALYZER_REPO_PATH/text-analyzer && ./gradlew clean testClasses install
cd $BUG_REPORT_COMPLETION_REPO_PATH/code/bug_report_coding && ./gradlew clean testClasses install
cd $BUG_REPORT_COMPLETION_REPO_PATH/code/bug_report_patterns && ./gradlew clean testClasses install
cd $BUG_REPORT_COMPLETION_REPO_PATH/code/bug_report_classifier && ./gradlew clean testClasses install
cd $BUG_REPORT_COMPLETION_REPO_PATH/code/bug_report_parser/bugparser && ./gradlew clean testClasses install
#cd $ANDROID_CORE_REPO_PATH && mvn clean install -DskipTests
#



cd $CUR_DIR

cd ../burt-nlparser && mvn clean install -DskipTests
cd ../trace-replayer/lib && ./0_install-maven-deps.sh
cd ../../trace-replayer && mvn clean install -DskipTests
cd ../crashscope && mvn clean install -DskipTests
cd ../burt-quality-checker && mvn clean install -DskipTests
cd $CUR_DIR


./mvnw spring-boot:run
