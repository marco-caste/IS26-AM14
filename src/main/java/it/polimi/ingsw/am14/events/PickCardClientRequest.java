package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PickCardClientRequest extends ClientRequest {

    private static final String METHOD_NAME = "pickCard";

    private final String nickname;
    private final int cardId;
    private final int row;

    public PickCardClientRequest(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("cardID") int cardId,
            @JsonProperty("row") int row) {
        this.nickname = nickname;
        this.cardId = cardId;
        this.row = row;
    }
}
