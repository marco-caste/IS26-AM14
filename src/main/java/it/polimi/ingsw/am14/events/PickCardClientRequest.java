package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.exceptions.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.SQLException;

public class PickCardClientRequest extends ClientRequest {

    private final int cardId;
    private final int row;

    @JsonCreator
    public PickCardClientRequest(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("cardID") int cardId,
            @JsonProperty("row") int row) {
        super("pick card client request", nickname);
        this.cardId = cardId;
        this.row = row;
    }

    public void dispatchRequest(LobbyController controller) throws IllegalNicknameException, WrongStateException, WrongTurnException, UnknownPositionException, IllegalPickException, ForcedPickException, IllegalPlacementException, UnknownNicknameException, SQLException {
        controller.pickCard(this.nickname, this.cardId, this.row);
    }
}
