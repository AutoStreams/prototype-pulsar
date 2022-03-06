package com.klungerbo.streams.pulsar;

import com.klungerbo.streams.pulsar.utils.FileUtils;
import java.io.IOException;
import java.util.Arrays;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Partially adapted from https://www.baeldung.com/apache-pulsar
 *
 * @version 1.0
 * @since 1.0
 */
public class ConsumerWorker implements Runnable {
    private Consumer<String> consumer = null;
    private boolean running = false;
    private final Logger logger = LoggerFactory.getLogger(ConsumerWorker.class);
    private static final String CONFIG_NAME = "consumerconfig.properties";
    private Set<String> topics = new HashSet<>(Arrays.asList("Testtopic"));

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
        this.start();
    }

    /**
     * Starts the thread for the consumer, also starting the consumer itself.
     */
    public void start() {
        this.initialize();
        Thread t = new Thread(this);
        t.start();
    }

    /**
     * Attempts to gracefully stop the consumer.
     */
    public void stop(){
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
        Properties props = FileUtils.loadConfigFromFile(CONFIG_NAME);

        String host = System.getenv().getOrDefault("PULSAR_BROKER_URL",
            props.getProperty("pulsar.url", "pulsar://localhost:8080"));

        PulsarClient client = PulsarClient.builder().serviceUrl(host).build();

        Map<String, Object> consumerProperties = new HashMap<>();
        consumerProperties.put("topicNames", topics);
        consumerProperties.put("subscriptionName", props.getProperty("subscriptionName",
            "subscription"));

        consumer = client.newConsumer(Schema.STRING)
            .loadConf(consumerProperties)
            .subscribe();
    }

    /**
     * Continuously polls for messages from the broker, and displays the messages in terminal
     * as they are received.
     */
    @Override
    public void run() {
        while (running) {
            try {
                Message<String> message = consumer.receive();
                logger.debug("Consumer received message {}", message);
            } catch (PulsarClientException e) {
                e.printStackTrace();
            }
        }
    }
}
