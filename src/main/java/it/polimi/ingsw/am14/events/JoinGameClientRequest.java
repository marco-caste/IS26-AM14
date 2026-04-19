package it.polimi.ingsw.am14.events;

import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.exceptions.IllegalGameIdException;
import it.polimi.ingsw.am14.exceptions.WrongStateException;

public class JoinGameClientRequest extends ClientRequest {

    private final String nickname;
    private final int gameId;
    private final String totemColor;

    public JoinGameClientRequest(String nickname, int gameId,String totemColor) {
        this.nickname = nickname;
        this.gameId = gameId;
        this.totemColor = totemColor;
    }

    public void dispatchRequest(LobbyController controller) throws WrongStateException, IllegalGameIdException {
        controller.joinGame(this.nickname, this.gameId, this.totemColor);
    }
}
