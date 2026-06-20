package it.polimi.ingsw.am14.network;

import it.polimi.ingsw.am14.network.messages.events.Event;
import it.polimi.ingsw.am14.network.messages.events.ExceptionEvent;
/**
 * Interface representing a remote client connected to the server.
 * It defines the standard operations to interact with the client, such as sending
 * game updates, reporting errors, and verifying the connection status.
 */
public interface VirtualClient {
    /**
     * Sends a generic event update to the client.
     *
     * @param update the {@link Event} containing the update information.
     * @throws Exception if an error occurs during the transmission.
     */
    void update(Event update) throws Exception;
    /**
     * Sends an error event to the client, typically as a response to an invalid action.
     *
     * @param error the {@link ExceptionEvent} containing the error details.
     * @throws Exception if an error occurs during the transmission.
     */
    void reportError(ExceptionEvent error) throws Exception;
    /**
     * Sends a keep-alive ping to the client to verify that the connection is still active.
     *
     * @throws Exception if the connection is lost or an error occurs during the ping.
     */
    void pingClient() throws Exception;
}
