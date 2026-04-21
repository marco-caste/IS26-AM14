package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.exceptions.IllegalGameIdException;
import it.polimi.ingsw.am14.exceptions.IllegalNicknameException;
import it.polimi.ingsw.am14.exceptions.WrongStateException;

public class ConnectClientRequest extends ClientRequest{

    // mferr
    @JsonCreator
    public ConnectClientRequest(
            @JsonProperty("nickname") String nickname) {
        super("connect client request", nickname);
    }

    // mferr
    @Override
    public void dispatchRequest(LobbyController controller) throws IllegalNicknameException, IllegalGameIdException, WrongStateException {
        controller.connectUser(this.nickname);
    }
}
