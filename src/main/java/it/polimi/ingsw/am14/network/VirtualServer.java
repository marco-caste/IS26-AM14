package it.polimi.ingsw.am14.network;

import java.rmi.RemoteException;
/**
 * Interface representing the server infrastructure as exposed to remote clients.
 * It outlines the actions a connected client can invoke on the server to manage
 * games and execute gameplay interactions.
 */
public interface VirtualServer {
    /**
     * Requests the creation of a new game session on the server.
     *
     * @param nickname   the requested username of the player hosting the game.
     * @param numPlayers the desired number of participants for the match.
     * @param totemColor the preferred totem color of the host.
     * @throws Exception if the network transaction fails or validation errors occur.
     */
    void createGame(String nickname, int numPlayers, String totemColor) throws Exception;
    /**
     * Requests a connection to a specific, existing game session.
     *
     * @param nickname   the requested username of the player joining the match.
     * @param gameId     the unique numerical identifier of the target game.
     * @param totemColor the preferred totem color of the joining player.
     * @throws Exception if the connection fails or if the game is full/unavailable.
     */
    void joinGame(String nickname, int gameId, String totemColor) throws Exception;
    /**
     * Submits a gameplay request to place a totem at a precise coordinate.
     *
     * @param nickname the username of the active player performing the action.
     * @param position the character representing the chosen board position.
     * @throws Exception if the network transaction fails or the move proves invalid.
     */
    void placeTotem(String nickname, char position) throws Exception;
    /**
     * Submits a gameplay request to pick a specific card from the available selections.
     *
     * @param nickname the username of the active player picking the card.
     * @param cardId   the unique identifier of the chosen card.
     * @param row      a value representing the row, deck, or context from which the card is drawn.
     * @throws Exception if the network transaction fails or the action is denied.
     */
    void pickCard(String nickname, int cardId, int row) throws Exception;
    /**
     * Reaches out to the server with a keep-alive ping to maintain the connection.
     *
     * @param nickname the username of the client communicating its heartbeat.
     * @return the {@link Thread} operating the connection/ping management, depending on the network protocol in use.
     * @throws RemoteException if an RMI-related network error manifests during the ping.
     */
    Thread pingServer(String nickname) throws RemoteException;
}
