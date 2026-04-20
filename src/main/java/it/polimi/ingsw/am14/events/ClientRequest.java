package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConnectClientRequest.class, name = "ConnectClientRequest"),
        @JsonSubTypes.Type(value = CreateGameClientRequest.class, name = "CreateGameClientRequest"),
        @JsonSubTypes.Type(value = JoinGameClientRequest.class, name = "JoinGameClientRequest"),
        @JsonSubTypes.Type(value = PickCardClientRequest.class, name = "PickCardClientRequest"),
        @JsonSubTypes.Type(value = PlaceTotemClientRequest.class, name = "PlaceTotemClientRequest")
})

public abstract class ClientRequest extends Event {
    public ClientRequest(String description) {
        super(description);
    }
}
