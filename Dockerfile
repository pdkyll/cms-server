FROM adoptopenjdk/maven-openjdk8:latest as build-stage
WORKDIR /app
COPY ./ /app/
RUN mvn clean install -DskipTests

FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY --from=build-stage /app/target/${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]