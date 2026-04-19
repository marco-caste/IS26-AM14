package it.polimi.ingsw.am14.events;

public class PositionTotemClientRequest extends ClientRequest {

    private static final String METHOD_NAME = "placeTotem";

    private final String nickname;
    private final char position;

    public PositionTotemClientRequest(String nickname, char position) {
        this.nickname = nickname;
        this.position = position;
    }
}
