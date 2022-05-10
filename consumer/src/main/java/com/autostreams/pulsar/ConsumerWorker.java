/**
 * Code adapted from https://www.baeldung.com/apache-pulsar
 */

package com.autostreams.pulsar;

import com.autostreams.utils.fileutils.FileUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Worker that contains the consumer, receiving data from a broker.
 *
 * @version 1.0
 * @since 1.0
 */
public class ConsumerWorker implements Runnable {
    private static final String CONFIG_NAME = "consumerconfig.properties";
    private final Logger logger = LoggerFactory.getLogger(ConsumerWorker.class);
    private Consumer<String> consumer = null;
    private boolean running = false;
    private Set<String> topics = new HashSet<>();

    /**
     * Initializes and prepares the consumer for use.
     */
    public void initialize() {
        try {
            createConsumer();
        } catch (IOException ioe) {
            logger.error("Exception occurred during construction of consumer");
            ioe.printStackTrace();
        }
        running = true;
        this.start();
    }

    /**
     * Starts the thread for the consumer, also starting the consumer itself.
     */
    public void start() {
        Thread t = new Thread(this);
        t.start();
    }

    /**
     * Attempts to gracefully stop the consumer.
     */
    public void stop() {
        running = false;
        try {
            consumer.close();
        } catch (PulsarClientException pce) {
            logger.error("An error occurred while closing the Pulsar consumer");
        }
    }

    /**
     * Creates the consumer object, and sets required configuration before consumption.
     *
     * @throws IOException if there is a problem getting the config file for the worker
     */
    private void createConsumer() throws IOException {
        logger.info("Creating consumer");
        Properties props = FileUtils.loadPropertiesFromFile(CONFIG_NAME);
        Map<String, Object> consumerConfigurations = getConsumerPropertiesAsMap(props);
        String host = System.getenv().getOrDefault("PULSAR_BROKER_URL",
            props.getProperty("pulsar.url", "pulsar://localhost:6650"));

        PulsarClient client = PulsarClient.builder().serviceUrl(host).build();
        this.consumer = client.newConsumer(Schema.STRING)
            .loadConf(consumerConfigurations)
            .subscribe();
        logger.info("Consumer created, topic subscribed to");
    }

    /**
     * Gets a mapped version of supplied properties for the pulsar consumer.
     *
     * @param props properties to carry over into the returned map
     * @return map of all relevant configurations in String-Object pairs
     */
    private Map<String, Object> getConsumerPropertiesAsMap(Properties props) {
        Map<String, Object> consumerProperties = new HashMap<>();

        String topic = System.getenv().getOrDefault("TOPIC_NAME",
            props.getProperty("pulsar.topic", "Testtopic"));
        topics.add(topic);

        String subscriptionName = System.getenv().getOrDefault("SUBSCRIPTION_NAME",
            props.getProperty("pulsar.subscriptionName", "subscription"));

        String subscriptionType = System.getenv().getOrDefault("SUBSCRIPTION_TYPE",
            props.getProperty("pulsar.subscriptionType", "Shared"));

        SubscriptionType st = SubscriptionType.valueOf(subscriptionType);

        int receiverQueueSize = Integer.parseInt(
            System.getenv().getOrDefault("RECEIVER_QUEUE_SIZE",
                props.getProperty("pulsar.receiverQueueSize", "1000")));

        int acknowledgementsGroupTimeMicros = Integer.parseInt(
            System.getenv().getOrDefault("ACKNOWLEDGEMENTS_GROUP_TIME_MICROS",
                props.getProperty("pulsar.acknowledgementsGroupTimeMicros", "1000")));

        int ackTimeoutMillis = Integer.parseInt(
            System.getenv().getOrDefault("ACKNOWLEDGEMENTS_TIMEOUT_MILLIS",
                props.getProperty("pulsar.acknowledgementsTimeoutMillis", "0")));

        int tickDurationMillis = Integer.parseInt(
            System.getenv().getOrDefault("TICK_DURATION_MILLIS",
                props.getProperty("pulsar.tickDurationMillis", "1000")));

        consumerProperties.put("topicNames", topics);
        consumerProperties.put("subscriptionName", subscriptionName);
        consumerProperties.put("subscriptionType", st);
        consumerProperties.put("receiverQueueSize", receiverQueueSize);
        consumerProperties.put("acknowledgementsGroupTimeMicros", acknowledgementsGroupTimeMicros);
        consumerProperties.put("ackTimeoutMillis", ackTimeoutMillis);
        consumerProperties.put("tickDurationMillis", tickDurationMillis);

        return consumerProperties;
    }

    private void receive() {
        while (running) {
            Message<String> message = null;

            try {
                logger.info("Waiting to receive message...");
                message = this.consumer.receive();

                this.consumer.acknowledge(message);
                logger.info("Consumer received message {}", message.getValue());

            } catch (PulsarClientException e) {
                consumer.negativeAcknowledge(message);
                e.printStackTrace();
            }
        }
    }

    /**
     * Continuously receives messages from the broker, and displays the messages in terminal
     * as they are received.
     */
    @Override
    public void run() {
        receive();
    }
}
