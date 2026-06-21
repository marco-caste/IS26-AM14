package it.polimi.ingsw.am14.network.rmi.server;

import it.polimi.ingsw.am14.network.VirtualClient;
import it.polimi.ingsw.am14.network.messages.events.Event;
import it.polimi.ingsw.am14.network.messages.events.ExceptionEvent;

import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * Remote interface representing the client exposed to the RMI server.
 * It extends {@link VirtualClient} to include core client updates and {@link Remote} to allow remote invocations.
 */
public interface VirtualClientRmi extends VirtualClient, Remote {
    /**
     * Sends an update event from the server to the client.
     *
     * @param update the {@link Event} object containing the state update.
     * @throws RemoteException if a communication-related error occurs.
     */
    @Override
    void update(Event update) throws RemoteException;
    /**
     * Sends an error event from the server to the client.
     *
     * @param error the {@link ExceptionEvent} containing the error details.
     * @throws RemoteException if a communication-related error occurs.
     */
    @Override
    void reportError(ExceptionEvent error) throws RemoteException;
    /**
     * Receives a ping from the server to verify that the client is still connected and responsive.
     *
     * @throws RemoteException if a communication-related error occurs.
     */
    @Override
    void pingClient() throws RemoteException;
}
