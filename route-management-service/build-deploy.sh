mvn clean install
scp -r -P 2222 route-common/target/route-common-1.0-SNAPSHOT.jar route-ejb/target/route-ejb-1.0-SNAPSHOT.jar route-web/target/route-web-1.0-SNAPSHOT.war s367044@helios.cs.ifmo.ru:~/services
