package it.polimi.ingsw.am14.network.messages.events;

import it.polimi.ingsw.am14.clientModelController.ClientController;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.clientModelController.ClientModel;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CardPickedEvent.class, name = "CardPickedEvent"),
        @JsonSubTypes.Type(value = EndGameEvent.class, name = "EndGameEvent"),
        @JsonSubTypes.Type(value = EndRoundEvent.class, name = "EndRoundEvent"),
        @JsonSubTypes.Type(value = GameStartedEvent.class, name = "GameStartedEvent"),
        @JsonSubTypes.Type(value = GameRestartedEvent.class, name = "GameRestartedEvent"),
        @JsonSubTypes.Type(value = JoinedGameEvent.class, name = "JoinedGameEvent"),
        @JsonSubTypes.Type(value = LoginSuccesfulEvent.class, name = "LoginSuccesfulEvent"),
        @JsonSubTypes.Type(value = PlayerReconnectedEvent.class, name = "PlayerReconnectedEvent"),
        @JsonSubTypes.Type(value = TotemPlacedEvent.class, name = "TotemPlacedEvent"),
        @JsonSubTypes.Type(value = ExceptionEvent.class, name = "ExceptionEvent"),
        @JsonSubTypes.Type(value = PlayerDisconnectedEvent.class, name = "PlayerDisconnectedEvent"),
})

public abstract class Event implements Serializable {
    // a brief description of the event, for logging purposes
    protected String description;

    public void effect(ClientModel model){}
    // TODO: SOTTOCLASSI

    public Event(String description) {
        this.description = description;
    }

    public void bufferize(NetworkLayer network) {}

    public String getDescription() {
        return description;
    }

    /**
     * Hook method called after event processing.
     * Subclasses can override to perform custom controller logic.
     * Default implementation does nothing.
     */
    public void onEventProcessed(ClientController controller, ClientModel model) {
        // Default: do nothing. Override in specific events, visitor pattern
    }
}
