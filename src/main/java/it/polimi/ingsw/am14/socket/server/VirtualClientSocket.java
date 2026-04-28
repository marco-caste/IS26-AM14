package it.polimi.ingsw.am14.socket.server;

import it.polimi.ingsw.am14.VirtualClient;
import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;

public interface VirtualClientSocket extends VirtualClient {
    public void update(Event update);
    public void reportError(ExceptionEvent error);
}
