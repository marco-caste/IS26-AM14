package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateGameClientRequest extends ClientRequest {

    private static final String METHOD_NAME = "setup";
    private final String nickname;
    private final int numPlayers;
    private final String totemColor;

    public CreateGameClientRequest(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("numPlayers") int numPlayers,
            @JsonProperty("totemColor") String totemColor) {
        this.nickname = nickname;
        this.numPlayers = numPlayers;
        this.totemColor = totemColor;
    }
}
