package it.polimi.ingsw.am14.events;

public class CreateGameClientRequest extends ClientRequest {

    private static final String METHOD_NAME = "setup";
    private final String nickname;
    private final int numPlayers;
    private final String totemColor;

    public CreateGameClientRequest(String nickname, int numPlayers, String totemColor) {
        this.nickname = nickname;
        this.numPlayers = numPlayers;
        this.totemColor = totemColor;
    }
}
