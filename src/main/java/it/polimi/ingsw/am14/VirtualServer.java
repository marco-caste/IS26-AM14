package it.polimi.ingsw.am14;

public interface VirtualServer {
    void connectUser(String nickname);
    void createGame(String nickname, int numPlayers, String totemColor);
    void joinGame(String nickname, int gameId, String totemColor);
    void placeTotem(String nickname, char position);
    void pickCard(String nickname, int cardId, int row);
}
