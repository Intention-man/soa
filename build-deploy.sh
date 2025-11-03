mvn clean install
scp -r -P 2222 route-ear/target/route-ear-1.0-SNAPSHOT.ear  s367044@helios.cs.ifmo.ru:~/services