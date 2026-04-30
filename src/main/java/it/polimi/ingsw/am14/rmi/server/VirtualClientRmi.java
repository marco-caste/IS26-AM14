package it.polimi.ingsw.am14.rmi.server;

import it.polimi.ingsw.am14.EventBuffer;
import it.polimi.ingsw.am14.VirtualClient;
import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualClientRmi extends VirtualClient, Remote {
    @Override
    void update(Event update) throws RemoteException;

    @Override
    void reportError(ExceptionEvent error) throws RemoteException;
}
