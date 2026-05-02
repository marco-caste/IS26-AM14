package it.polimi.ingsw.am14.network;

public interface VirtualServer {
    void createGame(String nickname, int numPlayers, String totemColor) throws Exception;
    void joinGame(String nickname, int gameId, String totemColor) throws Exception;
    void placeTotem(String nickname, char position) throws Exception;
    void pickCard(String nickname, int cardId, int row) throws Exception;
}
