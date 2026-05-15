package it.polimi.ingsw.am14.network.messages.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.controller.LobbyController;

public class PingClientRequest extends ClientRequest {

    // why does it need a nickname? because if the server receives a pingServer request, it needs to know which client
    // sent it, so that it can send a pingServer response back to client.

    @JsonCreator
    public PingClientRequest(
            @JsonProperty("nickname") String nickname) {
        super("pingServer", nickname);
    }

    @Override
    public void dispatchRequest(LobbyController controller) throws Exception {
        controller.handlePingRequest(getNickname());
    }
}
