package it.polimi.ingsw.am14.network.socket.client;

import it.polimi.ingsw.am14.network.VirtualServer;

public interface VirtualServerSocket extends VirtualServer {
    void connectUser(String nickname);
    @Override
    void createGame(String nickname, int numPlayers, String totemColor);
    @Override
    void joinGame(String nickname, int gameId, String totemColor);
    @Override
    void placeTotem(String nickname, char position);
    @Override
    void pickCard(String nickname, int cardId, int row);
    @Override
    Thread pingServer(String nickname);
}
