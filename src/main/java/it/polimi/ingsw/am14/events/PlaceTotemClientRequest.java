package it.polimi.ingsw.am14.events;

import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.exceptions.*;

public class PlaceTotemClientRequest extends ClientRequest {

    private final String nickname;
    private final char position;

    public PlaceTotemClientRequest(String nickname, char position) {
        this.nickname = nickname;
        this.position = position;
    }

    public void dispatchRequest(LobbyController controller) throws IllegalNicknameException, WrongStateException, WrongTurnException, UnknownPositionException, IllegalPlacementException, UnknownNicknameException {
        controller.placeTotem(this.nickname, this.position);
    }
}
