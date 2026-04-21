package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.exceptions.*;

public class PlaceTotemClientRequest extends ClientRequest {

    private final String nickname;
    private final char position;

    @JsonCreator
    public PlaceTotemClientRequest(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("position") char position) {
        super("place totem client request");
        this.nickname = nickname;
        this.position = position;
    }

    public void dispatchRequest(LobbyController controller) throws IllegalNicknameException, WrongStateException, WrongTurnException, UnknownPositionException, IllegalPlacementException, UnknownNicknameException {
        controller.placeTotem(this.nickname, this.position);
    }
}
