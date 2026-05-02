package it.polimi.ingsw.am14.network;

import it.polimi.ingsw.am14.events.Event;

public interface Observer {
    void update(Event event);
}
