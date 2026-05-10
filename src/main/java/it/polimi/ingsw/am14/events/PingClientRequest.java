package it.polimi.ingsw.am14.events;

import it.polimi.ingsw.am14.controller.LobbyController;

public class PingClientRequest extends ClientRequest {

    // why does it need a nickname? because if the server receives a pingServer request, it needs to know which client
    // sent it, so that it can send a pingServer response back to client.

    public PingClientRequest(String nickname) {
        super("pingServer", nickname);
    }

    @Override
    public void dispatchRequest(LobbyController controller) throws Exception {
        // send a pingServer response to this.nickname (look it up in controller)
    }
}
