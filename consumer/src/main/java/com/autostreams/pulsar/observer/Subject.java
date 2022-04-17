package com.autostreams.pulsar.observer;

/**
 * Interface representing an observer in an observer - subject relationship found in the Observer
 * Pattern.
 *
 * @version 1.0
 * @since 1.0
 */
public interface Subject {
    /**
     * Adds the passed Observer to list of observers.
     *
     * @param subscriber Observer to be added to list of observers, subscribing it to the
     *                   implementing subject
     */
    void subscribe(Observer subscriber);

    /**
     * Notifies all observers subscribed to implementing subject.
     */
    void notifyObservers();
}
