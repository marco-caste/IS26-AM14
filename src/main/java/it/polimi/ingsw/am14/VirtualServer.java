package it.polimi.ingsw.am14;

public interface VirtualServer {
    public void createGame(String nickname, int numPlayers, String totemColor);
    public void joinGame(String nickname, int gameId, String totemColor);
    public void placeTotem(String nickname, char position);
    public void pickCard(String nickname, int cardId, int row);
}
