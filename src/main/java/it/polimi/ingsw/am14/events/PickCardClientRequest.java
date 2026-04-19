package it.polimi.ingsw.am14.events;

import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.exceptions.*;

public class PickCardClientRequest extends ClientRequest {

    private final String nickname;
    private final int cardId;
    private final int row;

    public PickCardClientRequest(String nickname, int cardId, int row) {
        this.nickname = nickname;
        this.cardId = cardId;
        this.row = row;
    }

    public void dispatchRequest(LobbyController controller) throws IllegalNicknameException, WrongStateException, WrongTurnException, UnknownPositionException, IllegalPickException, ForcedPickException, IllegalPlacementException, UnknownNicknameException{
        controller.pickCard(this.nickname, this.cardId, this.row);
    }
}
