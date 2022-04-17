package com.autostreams.pulsar;

import com.autostreams.pulsar.observer.Observer;
import com.autostreams.pulsar.observer.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class representing a timer for the consumer, intended to keep track of how long it takes to
 * receive a specified amount of segments.
 *
 * @version 1.0
 * @since 1.0
 */
public class ConsumerTimer implements Observer {
    int transactionGoal = 0;
    int transactions = 0;
    long startTime = 0;
    long endTime = 0;
    long intervalStart = 0;
    boolean tracking = true;
    private final Logger logger = LoggerFactory.getLogger(ConsumerTimer.class);

    /**
     * Constructor for the ConsumerTimer class. Its related consumer and amount of transactions to
     * time are provided as parameters.
     *
     * @param consumer consumer to time for. Must implement the Subject interface
     * @param transactionGoal Amount of transactions to time
     */
    public ConsumerTimer(Subject consumer, int transactionGoal) {
        consumer.subscribe(this);
        this.setTransactionGoal(transactionGoal);
    }

    /**
     * Sets the goal transactions to time for.
     *
     * @param transactionGoal amount of transactions to time
     */
    public void setTransactionGoal(int transactionGoal) {
        this.transactionGoal = transactionGoal;
    }

    /**
     * Starts the interval timer.
     */
    private void startTimer() {
        this.startTime = System.currentTimeMillis();
        this.intervalStart = System.currentTimeMillis();
    }

    /**
     * Stops the timer towards transaction goal.
     */
    private void startIntervalTimer() {
        this.intervalStart = System.currentTimeMillis();
    }

    public void endTimer() {
        this.endTime = System.currentTimeMillis();
    }

    /**
     * Takes two timestamps and returns the time between them in seconds.
     *
     * @param startTime timestamp for the starting time
     * @param endTime Timestamp for the end time
     * @return time between start and end in seconds
     */
    private double getTimeSpanInSeconds(long startTime, long endTime) {
        long timeMillis = (endTime - startTime);
        return timeMillis / 1000.0;
    }

    /**
     * Resets the timer and transaction counts.
     */
    private void resetTimer() {
        this.startTime = 0;
        this.endTime = 0;
        this.transactions = 0;
    }

    /**
     * Updates the timer based on current amount of transactions received.
     */
    @Override
    public void update() {
        int loggerInterval = 100000;
        if (tracking) {
            this.transactions++;
            if (transactions == 1) {
                this.startTimer();
                this.startIntervalTimer();
                logger.info("First message of new sequence received. Start time has been set");
            }

            if (this.transactions % loggerInterval == 0) {
                long intervalEnd = System.currentTimeMillis();
                double intervalDurationInSeconds = this.getTimeSpanInSeconds(
                        intervalStart,
                        intervalEnd);

                String formattedSeconds = String.format("%.2f", intervalDurationInSeconds);
                logger.info("Transactions {} of goal {} | {}s | {}msg/s",
                        this.transactions,
                        this.transactionGoal,
                        formattedSeconds,
                        (int) (loggerInterval / intervalDurationInSeconds));

                this.startIntervalTimer();
            }

            if (this.transactions >= this.transactionGoal) {
                this.endTimer();

                double timeToTransactionGoal = this.getTimeSpanInSeconds(
                        this.startTime,
                        this.endTime);

                String formattedTimeToGoal = String.format("%.2f", timeToTransactionGoal);
                logger.info("Total amount of time until {} transactions: {} s",
                        transactionGoal,
                        formattedTimeToGoal);

                this.resetTimer();
            }
        }
    }
}
