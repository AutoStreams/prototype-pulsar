package com.autostreams.pulsar.observer;

/**
 * Interface representing an observer in an observer - subject relationship found in the Observer
 * Pattern.
 *
 * @version 1.0
 * @since 1.0
 */
public interface Observer {

    /**
     * Update method to be called by Subject whenever implementing Observer needs to update based on
     * events or state changes in the observer's subject(s).
     */
    void update();
}