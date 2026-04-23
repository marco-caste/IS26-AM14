package it.polimi.ingsw.am14;

import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;

public interface VirtualView {
    public void update(Event update);
    public void reportError(ExceptionEvent error);
    void addToEventBuffer(Event event);
}
