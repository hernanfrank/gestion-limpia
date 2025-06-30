FROM openjdk:17-jdk-slim
LABEL author=hernanfrank
COPY target/gestion-limpia-1.0.jar /gestion-limpia.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/gestion-limpia.jar"]