package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.exceptions.IllegalGameIdException;
import it.polimi.ingsw.am14.exceptions.WrongStateException;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JoinGameClientRequest extends ClientRequest {

    private final String nickname;
    private final String totemColor;
    private final int gameId;

    @JsonCreator
    public JoinGameClientRequest(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("totemColor") String totemColor,
            @JsonProperty("gameID") int gameId) {
        super("join game client request");
        this.nickname = nickname;
        this.gameId = gameId;
        this.totemColor = totemColor;
    }

    public void dispatchRequest(LobbyController controller) throws WrongStateException, IllegalGameIdException {
        controller.joinGame(this.nickname, this.gameId, this.totemColor);
    }
}
