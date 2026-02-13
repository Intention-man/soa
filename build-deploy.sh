mvn clean install
#scp -r -P 2222 route-service-javaee/route-ear/target/route-ear-1.0-SNAPSHOT.ear s367044@helios.cs.ifmo.ru:~/wildfly/wildfly-35.0.0.Final/standalone/deployments/

cd navigator-service-spring-cloud
scp -r -P 2222 \
config-repo \
config-server/target/config-server-1.0-SNAPSHOT.jar \
navigator-service/target/navigator-service-spring-1.0-SNAPSHOT.jar \
s367785@helios.cs.ifmo.ru:~/services/navigator-services-dir
