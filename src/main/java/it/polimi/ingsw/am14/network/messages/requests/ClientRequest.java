package it.polimi.ingsw.am14.network.messages.requests;

import it.polimi.ingsw.am14.controller.LobbyController;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am14.network.messages.events.Event;
/**
 * Abstract superclass representing a request sent by a client to the server.
 * Inherits from {@link Event}.
 * It uses Jackson annotations to support polymorphic deserialization of various client request types.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConnectClientRequest.class, name = "ConnectClientRequest"),
        @JsonSubTypes.Type(value = CreateGameClientRequest.class, name = "CreateGameClientRequest"),
        @JsonSubTypes.Type(value = JoinGameClientRequest.class, name = "JoinGameClientRequest"),
        @JsonSubTypes.Type(value = PickCardClientRequest.class, name = "PickCardClientRequest"),
        @JsonSubTypes.Type(value = PlaceTotemClientRequest.class, name = "PlaceTotemClientRequest"),
        @JsonSubTypes.Type(value = PingClientRequest.class, name = "PingClientRequest")
})

public abstract class ClientRequest extends Event {

    String nickname;

    /**
     * Dispatches the client's request to the server's lobby controller for processing.
     *
     * @param controller the {@link LobbyController} responsible for handling the logic.
     * @throws Exception if an error occurs during the processing of the request.
     */

    public abstract void dispatchRequest(LobbyController controller) throws Exception;

    /**
     * Constructs a {@code ClientRequest} with the specified description and client nickname.
     *
     * @param description a brief explanation of the request.
     * @param nickname    the nickname of the client sending the request.
     */

    public ClientRequest(String description, String nickname) {
        super(description);
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
