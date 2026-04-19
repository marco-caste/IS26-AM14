package it.polimi.ingsw.am14.events;

import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.exceptions.IllegalGameIdException;
import it.polimi.ingsw.am14.exceptions.IllegalNicknameException;
import it.polimi.ingsw.am14.exceptions.WrongStateException;

public class ConnectClientRequest extends ClientRequest{

    private final String nickname;

    public ConnectClientRequest(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void dispatchRequest(LobbyController controller) throws IllegalNicknameException, IllegalGameIdException, WrongStateException {
        controller.connectUser(this.nickname);
    }
}
