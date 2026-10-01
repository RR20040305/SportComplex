@echo on
chcp 65001
mvn clean package
java --enable-native-access=ALL-UNNAMED -jar target/my-console-app-1.0-SNAPSHOT.jar