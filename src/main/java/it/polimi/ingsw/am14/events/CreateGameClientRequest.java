package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.exceptions.IllegalSetupException;
import it.polimi.ingsw.am14.exceptions.WrongStateException;

public class CreateGameClientRequest extends ClientRequest {

    private final String nickname;
    private final int numPlayers;
    private final String totemColor;

    @JsonCreator
    public CreateGameClientRequest(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("numPlayers") int numPlayers,
            @JsonProperty("totemColor") String totemColor) {
        super("create game client request");

        this.nickname = nickname;
        this.numPlayers = numPlayers;
        this.totemColor = totemColor;
    }

    public void dispatchRequest(LobbyController controller) throws IllegalSetupException, WrongStateException {
        controller.createGame(this.nickname, this.numPlayers, this.totemColor);
    }
}
