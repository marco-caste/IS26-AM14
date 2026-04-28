package it.polimi.ingsw.am14.rmi.server;

import it.polimi.ingsw.am14.VirtualView;
import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;

import java.rmi.Remote;

public interface VirtualClientRmi extends VirtualView, Remote {
    @Override
    void update(Event update);
    @Override
    void reportError(ExceptionEvent error);
    @Override
    void addToEventBuffer(Event event);
}
