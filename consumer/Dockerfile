FROM maven:3.8.4-openjdk-17-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM gcr.io/distroless/java17-debian11
COPY --from=build /home/app/target/pulsar-consumer-1.0-SNAPSHOT-jar-with-dependencies.jar /usr/local/lib/data-consumer.jar
ENTRYPOINT ["java", "-jar", "/usr/local/lib/data-consumer.jar"]