package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PositionTotemClientRequest extends ClientRequest {

    private static final String METHOD_NAME = "placeTotem";

    private final String nickname;
    private final char position;

    public PositionTotemClientRequest(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("position") char position) {
        this.nickname = nickname;
        this.position = position;
    }
}
