package it.polimi.ingsw.am14;

import it.polimi.ingsw.am14.events.Event;

import java.util.List;

public class EventBuffer {
    private final List<Event> buffer;

    public EventBuffer() {
        this.buffer = new java.util.ArrayList<>();
    }

    public synchronized void addEvent(Event event) {
        this.buffer.addLast(event);
    }

    public synchronized List<Event> flushBuffer() {
        List<Event> eventsToReturn = new java.util.ArrayList<>(this.buffer);
        this.buffer.clear();
        return eventsToReturn;
    }

    public synchronized boolean isEmpty() {
        return this.buffer.isEmpty();
    }

    public synchronized Event flushSingleEvent() {
        if (this.buffer.isEmpty()) {
            throw new IllegalStateException("Buffer is empty, no event to flush");
        }
        return this.buffer.removeFirst();
    }
}
