package it.polimi.ingsw.am14.network;

import it.polimi.ingsw.am14.network.messages.events.Event;
import it.polimi.ingsw.am14.network.messages.events.ExceptionEvent;

public interface VirtualClient {
    void update(Event update) throws Exception;
    void reportError(ExceptionEvent error) throws Exception;
    void pingClient() throws Exception;
}
