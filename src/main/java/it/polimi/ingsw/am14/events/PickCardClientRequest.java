package it.polimi.ingsw.am14.events;

public class PickCardClientRequest extends ClientRequest {

    private static final String METHOD_NAME = "pickCard";

    private final String nickname;
    private final int cardId;
    private final int row;

    public PickCardClientRequest(String nickname, int cardId, int row) {
        this.nickname = nickname;
        this.cardId = cardId;
        this.row = row;
    }
}
