package it.polimi.ingsw.am14.events;

public class JoinGameClientRequest extends ClientRequest {

    private static final String METHOD_NAME = "onPlayerConnection";

    private final String nickname;
    private final String totemColor;
    private final String gameId;

    public JoinGameClientRequest(String nickname, String totemColor, String gameId) {
        this.nickname = nickname;
        this.totemColor = totemColor;
        this.gameId = gameId;
    }
}
