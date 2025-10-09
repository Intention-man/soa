cd route-management-service
mvn clean package compile
cd ../navigator-service
mvn clean package compile
cd ..
scp -r -P 2222 route-management-service/target/route-management-service.war navigator-service/target/navigator-service.war s367044@helios.cs.ifmo.ru:~
