#!/bin/bash
set -x #echo on

export JAVA_HOME=`/usr/libexec/java_home -v 12.0.2`

export CUR_DIR=`pwd`


cd burt-nlparser && mvn clean install -DskipTests
cd ../trace-replayer/lib && ./0_install-maven-deps.sh
cd ../../trace-replayer && mvn clean install -DskipTests
cd ../crashscope && mvn clean install -DskipTests
cd ../burt-quality-checker && mvn clean install -DskipTests
cd ../burt-tools && mvn clean package -DskipTests
cd ../burt-state-matching && mvn clean package -DskipTests
cd $CUR_DIR


