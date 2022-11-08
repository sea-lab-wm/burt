export CUR_DIR=`pwd`

export JAVA_HOME=`/usr/libexec/java_home -v 12.0.2`

cd $CUR_DIR

cd ../burt-nlparser && mvn clean install -DskipTests
cd ../crashscope && mvn clean install -DskipTests
cd ../burt-quality-checker && mvn clean install -DskipTests
cd $CUR_DIR


./mvnw spring-boot:run