package it.polimi.ingsw.am14.rmi.client;

import it.polimi.ingsw.am14.EventBuffer;
import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;
import it.polimi.ingsw.am14.rmi.server.VirtualClientRmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RmiClient extends UnicastRemoteObject implements VirtualClientRmi {
    private final EventBuffer eventBuffer = new EventBuffer();

    protected RmiClient() throws RemoteException {
    }

    @Override
    public void update(Event update) {

    }

    @Override
    public void reportError(ExceptionEvent error) {

    }

    @Override
    public void addToEventBuffer(Event event) {

    }

    @Override
    public EventBuffer getEventBuffer() {
        return eventBuffer;
    }
}
