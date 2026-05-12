package it.polimi.ingsw.am14.network.socket.client;

import it.polimi.ingsw.am14.network.VirtualServer;

public interface VirtualServerSocket extends VirtualServer {
    void connectUser(String nickname);

    @Override
    void createGame(String nickname, int numPlayers, String totemColor) throws Exception;
    @Override
    void joinGame(String nickname, int gameId, String totemColor) throws Exception;
    @Override
    void placeTotem(String nickname, char position) throws Exception;
    @Override
    void pickCard(String nickname, int cardId, int row) throws Exception;
    @Override
    void pingServer(String nickname) throws Exception;
}
