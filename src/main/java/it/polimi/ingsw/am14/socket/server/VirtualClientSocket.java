package it.polimi.ingsw.am14.socket.server;

import it.polimi.ingsw.am14.VirtualView;
import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;

public interface VirtualClientSocket extends VirtualView {
    public void update(Event update);
    public void reportError(ExceptionEvent error);
}
