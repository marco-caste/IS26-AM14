package it.polimi.ingsw.am14.network.messages.requests;

import it.polimi.ingsw.am14.controller.LobbyController;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am14.network.messages.events.Event;

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

    public abstract void dispatchRequest(LobbyController controller) throws Exception;

    public ClientRequest(String description, String nickname) {
        super(description);
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
