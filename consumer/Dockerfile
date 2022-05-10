FROM maven:3.8.4-openjdk-17-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

ENV PULSAR_BROKER_URL='broker:6650'
ENV TOPIC_NAME = 'Testtopic'
ENV SUBSCRIPTION_NAME = 'subscription'
ENV TOPICS_PATTERN = ''
ENV SUBSCRIPTION_TYPE = 'Shared'
ENV RECEIVER_QUEUE_SIZE = 1000
ENV ACKNOWLEDGEMENTS_GROUP_TIME_MICROS = 100
ENV CONSUMER_NAME = 'Consumer'
ENV ACK_TIMEOUT_MILLIS = 0
ENV TICK_DURATION_MILLIS = 1000

FROM gcr.io/distroless/java17-debian11
COPY --from=build /home/app/target/pulsar-consumer.jar /usr/local/lib/pulsar-consumer.jar
ENTRYPOINT ["java", "-jar", "/usr/local/lib/pulsar-consumer.jar"]