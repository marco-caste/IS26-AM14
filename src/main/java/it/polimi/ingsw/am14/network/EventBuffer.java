package it.polimi.ingsw.am14.network;

import it.polimi.ingsw.am14.network.messages.events.Event;

import java.util.List;
/**
 * A thread-safe buffer used to queue and manage network events.
 * It provides synchronized methods to append events, check the buffer status,
 * and extract events either all at once or one by one.
 */
public class EventBuffer {
    private final List<Event> buffer;

    public EventBuffer() {
        this.buffer = new java.util.ArrayList<>();
    }
    /**
     * Synchronously adds a new event to the end of the buffer and notifies any waiting threads
     * that a new event is available for processing.
     *
     * @param event the {@link Event} to be added to the buffer.
     */
    public synchronized void addEvent(Event event) {
        this.buffer.addLast(event);
        // essentially sends an event as soon as it is added to the buffer
        notify();
    }
    /**
     * Synchronously retrieves and removes all events currently in the buffer.
     *
     * @return a {@link List} containing all the events that were stored in the buffer.
     */
    public synchronized List<Event> flushBuffer() {
        List<Event> eventsToReturn = new java.util.ArrayList<>(this.buffer);
        this.buffer.clear();
        return eventsToReturn;
    }
    /**
     * Synchronously checks whether the buffer is currently empty.
     *
     * @return {@code true} if the buffer contains no events, {@code false} otherwise.
     */
    public synchronized boolean isEmpty() {
        return this.buffer.isEmpty();
    }
}
