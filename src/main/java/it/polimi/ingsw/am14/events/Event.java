package it.polimi.ingsw.am14.events;

import it.polimi.ingsw.am14.view.ClientModel;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CardPickedEvent.class, name = "CardPickedEvent"),
        @JsonSubTypes.Type(value = EndGameEvent.class, name = "EndGameEvent"),
        @JsonSubTypes.Type(value = EndRoundEvent.class, name = "EndRoundEvent"),
        @JsonSubTypes.Type(value = GameOverEvent.class, name = "GameOverEvent"),
        @JsonSubTypes.Type(value = GameStartedEvent.class, name = "GameStartedEvent"),
        @JsonSubTypes.Type(value = JoinedGameEvent.class, name = "JoinedGameEvent"),
        @JsonSubTypes.Type(value = LoginSuccesfulEvent.class, name = "LoginSuccesfulEvent"),
        @JsonSubTypes.Type(value = PlayerReconnectedEvent.class, name = "PlayerReconnectedEvent"),
        @JsonSubTypes.Type(value = TotemPlacedEvent.class, name = "TotemPlacedEvent"),
        @JsonSubTypes.Type(value = ExceptionEvent.class, name = "ExceptionEvent")
})

public abstract class Event {
    // a brief description of the event, for logging purposes
    protected String description;

    public void effect(ClientModel model){}

    public Event(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
