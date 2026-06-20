package it.polimi.ingsw.am14.network.rmi.client;

import it.polimi.ingsw.am14.network.VirtualServer;
import it.polimi.ingsw.am14.network.rmi.server.VirtualClientRmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface representing the server exposed to RMI clients.
 * It extends {@link VirtualServer} to include core game actions and {@link Remote} to allow remote invocations.
 */

public interface VirtualServerRmi extends VirtualServer, Remote {

    /**
     * Connects an RMI client to the server.
     *
     * @param client   the remote reference to the connecting client.
     * @param nickname the chosen nickname of the player connecting.
     * @throws RemoteException if a communication-related error occurs.
     */
    void connect(VirtualClientRmi client, String nickname) throws RemoteException;
    /**
     * Requests the creation of a new game.
     *
     * @param nickname   the nickname of the player starting the game.
     * @param numPlayers the number of players participating in the game.
     * @param totemColor the color of the totem chosen by the creator.
     * @throws RemoteException if a communication-related error occurs.
     */
    @Override
    void createGame(String nickname, int numPlayers, String totemColor) throws RemoteException;
    /**
     * Requests to join an existing game.
     *
     * @param nickname   the nickname of the joining player.
     * @param gameId     the unique identifier of the game to join.
     * @param totemColor the color of the totem chosen by the player.
     * @throws RemoteException if a communication-related error occurs.
     */
    @Override
    void joinGame(String nickname, int gameId, String totemColor) throws RemoteException;
    /**
     * Requests to place a totem on the starting card.
     *
     * @param nickname the nickname of the player.
     * @param position the character representing the chosen face of the starting card.
     * @throws RemoteException if a communication-related error occurs.
     */
    @Override
    void placeTotem(String nickname, char position) throws RemoteException;
    /**
     * Requests to pick a card from the board.
     *
     * @param nickname the nickname of the player.
     * @param cardId   the unique identifier of the card to pick.
     * @param row      the index of the row where the card is positioned.
     * @throws RemoteException if a communication-related error occurs.
     */
    @Override
    void pickCard(String nickname, int cardId, int row) throws RemoteException;
    /**
     * Sends a ping to the server to verify the connection and keep it alive.
     *
     * @param nickname the nickname of the client sending the ping.
     * @return the {@link Thread} handling the server-side ping routine task, if applicable.
     * @throws RemoteException if a communication-related error occurs.
     */
    @Override
    Thread pingServer(String nickname) throws RemoteException;
}
