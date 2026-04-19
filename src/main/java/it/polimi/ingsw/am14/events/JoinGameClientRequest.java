package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JoinGameClientRequest extends ClientRequest {

    private static final String METHOD_NAME = "onPlayerConnection";

    private final String nickname;
    private final String totemColor;
    private final int gameId;

    public JoinGameClientRequest(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("totemColor") String totemColor,
            @JsonProperty("gameID") int gameId) {
        this.nickname = nickname;
        this.totemColor = totemColor;
        this.gameId = gameId;
    }
}
