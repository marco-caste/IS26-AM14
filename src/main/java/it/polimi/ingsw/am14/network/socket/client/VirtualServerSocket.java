package it.polimi.ingsw.am14.network.socket.client;

import it.polimi.ingsw.am14.network.VirtualServer;
/**
 * Interface representing the virtual server for socket-based communication on the client side.
 * Extends {@link VirtualServer} to define specific methods for sending requests to the server.
 */
public interface VirtualServerSocket extends VirtualServer {
    /**
     * Sends a request to connect a user to the server with the specified nickname.
     *
     * @param nickname the nickname of the user wanting to connect.
     */
    void connectUser(String nickname);
    /**
     * Sends a request to create a new game.
     *
     * @param nickname   the nickname of the player creating the game.
     * @param numPlayers the total number of players selected for the game.
     * @param totemColor the initial totem color chosen by the creator.
     */
    @Override
    void createGame(String nickname, int numPlayers, String totemColor);
    /**
     * Sends a request to join an existing game.
     *
     * @param nickname   the nickname of the player wanting to join.
     * @param gameId     the unique identifier of the game to join.
     * @param totemColor the totem color requested by the player.
     */
    @Override
    void joinGame(String nickname, int gameId, String totemColor);
    /**
     * Sends a request to place the player's initial totem card.
     *
     * @param nickname the nickname of the player performing the action.
     * @param position a character indicating the position (e.g., side) to place the totem.
     */
    @Override
    void placeTotem(String nickname, char position);
    /**
     * Sends a request to pick a card from the shared pool.
     *
     * @param nickname the nickname of the player picking the card.
     * @param cardId   the unique identifier of the chosen card.
     * @param row      the index of the row or area corresponding to the card's position.
     */
    @Override
    void pickCard(String nickname, int cardId, int row);
    /**
     * Initializes and starts a dedicated thread for sending periodic ping signals to the server,
     * ensuring the connection is kept alive over time.
     *
     * @param nickname the nickname of the user for whom the ping is sent.
     * @return the background {@link Thread} handling the ping.
     */
    @Override
    Thread pingServer(String nickname);
}
