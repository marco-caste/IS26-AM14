package it.polimi.ingsw.am14.events;

import it.polimi.ingsw.am14.controller.LobbyController;

public abstract class ClientRequest extends Event {

    public abstract void dispatchRequest(LobbyController controller) throws Exception;
}
