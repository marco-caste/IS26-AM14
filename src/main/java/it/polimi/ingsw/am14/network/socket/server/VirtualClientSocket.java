package it.polimi.ingsw.am14.network.socket.server;

import it.polimi.ingsw.am14.network.VirtualClient;
/**
 * Interface representing the virtual client for socket-based communication on the server side.
 * Extends {@link VirtualClient} to specify the contract for sending event updates and errors
 * back to a connected socket client.
 */
public interface VirtualClientSocket extends VirtualClient {
}
