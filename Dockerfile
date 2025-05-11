FROM openjdk:21-jdk
ADD target/operator-controller.jar operator.jar
ENTRYPOINT ["java", "-jar", "operator.jar"]