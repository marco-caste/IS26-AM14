package it.polimi.ingsw.am14.network.messages.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.controller.LobbyController;

/**
 * Request sent by a client to ping the server and keep the connection alive.
 * Inherits from {@link ClientRequest}.
 */

public class PingClientRequest extends ClientRequest {

    // why does it need a nickname? because if the server receives a pingServer request, it needs to know which client
    // sent it, so that it can send a pingServer response back to client.

    /**
     * Constructs a {@code PingClientRequest} from JSON data, used by Jackson upon receiving a message.
     * The nickname is required so that the server knows which client sent the ping,
     * allowing it to send a response back to the correct client.
     *
     * @param nickname the nickname of the client sending the ping request.
     */

    @JsonCreator
    public PingClientRequest(
            @JsonProperty("nickname") String nickname) {
        super("pingServer", nickname);
    }

    /**
     * Dispatches the ping request to the server's lobby controller.
     * Informs the controller that the client is still active and waiting for a response.
     *
     * @param controller the {@link LobbyController} responsible for handling the ping logic.
     */

    @Override
    public void dispatchRequest(LobbyController controller) {
        controller.handlePingRequest(getNickname());
    }
}
