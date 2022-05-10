package com.autostreams.pulsar;

import com.autostreams.utils.datareceiver.DataReceiver;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class containing the main entry point of the Pulsar producer prototype application.
 *
 * @version 1.0
 * @since 1.0
 */
public final class Main {
    /**
     * The entrypoint of the application.
     *
     * @param args commandline arguments.
     */
    public static void main(final String[] args) {
        final Logger logger = LoggerFactory.getLogger(Main.class);

        int tries = 100;
        int currentTry = 1;
        int secondsToSleep = 5;
        // Sleep to avoid conflict with broker. Producer must wait for broker to be ready
        // TODO find a better way to determine when producer can connect to broker
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            logger.info("Unable to sleep");
        }

        PulsarPrototypeProducer pulsarPrototypeProducer = new PulsarPrototypeProducer();
        while (!pulsarPrototypeProducer.initialize() && currentTry <= tries) {
            logger.warn(
                "[{}/{}] Failed to initialize PulsarPrototypeProducer, retrying in {} seconds",
                currentTry,
                tries,
                secondsToSleep
            );

            try {
                TimeUnit.SECONDS.sleep(secondsToSleep);
            } catch (InterruptedException e) {
                logger.error("Unable to sleep");
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }

            currentTry++;
        }

        // Failed to connect to the Pulsar broker within given time limit.
        if (currentTry > tries) {
            logger.error(
                "Failed to connect to the Pulsar broker after {} tries, exiting the application",
                tries
            );

            return;
        }

        logger.debug("Creating DataReceiver for PulsarProducer");
        DataReceiver dataReceiver = new DataReceiver(pulsarPrototypeProducer);
        logger.debug("DataReceiver for PulsarProducer has been created");

        logger.debug("DataReceiver running");
        dataReceiver.run();
        logger.debug("DataReceiver ran for PulsarProducer, has finished");
    }
}