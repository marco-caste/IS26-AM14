package it.polimi.ingsw.am14;

import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;

public interface VirtualView {
    void update(Event update);
    void reportError(ExceptionEvent error);
    void addToEventBuffer(Event event);
}
