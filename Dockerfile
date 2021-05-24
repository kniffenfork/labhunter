FROM openjdk:11
WORKDIR /opt
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dspring.config.location=config/application.yaml, secrets/db/db.yaml","-jar","/app.jar"]