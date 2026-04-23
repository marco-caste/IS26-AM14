package it.polimi.ingsw.am14.socket.server;

import it.polimi.ingsw.am14.events.Event;

import java.util.ArrayList;
import java.util.List;

public class EventBuffer {

    private List<Event> eventList;

    public EventBuffer(List<Event> eventList) {
        this.eventList = eventList;
    }

    public void addEvent(Event event){
        this.eventList.add(event);
    }

    public List<Event> flushBuffer(){
        List<Event> flushedEvents = this.eventList;
        this.eventList= new ArrayList<>();
        return flushedEvents;
    }

}
