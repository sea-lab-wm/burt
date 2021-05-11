export CUR_DIR=`pwd`


cd $CUR_DIR

cd ../burt-nlparser && mvn clean install -DskipTests
cd ../burt-quality-checker && mvn clean install -DskipTests
cd $CUR_DIR


./mvnw spring-boot:run