package it.polimi.ingsw.am14.network.messages.events;

import it.polimi.ingsw.am14.clientModelController.ClientController;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.clientModelController.ClientModel;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
/**
 * Abstract base class representing a generic network event in the application.
 * It acts as the superclass for all network events.
 * It provides the foundation for polymorphic serialization and deserialization using Jackson,
 * and defines the lifecycle methods for buffering, affecting the client model, and post-processing.
 */
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
    /**
     * Applies the effect of this event to the provided client model, updating the local state.
     *
     * @param model the {@link ClientModel} representing the client-side state of the game.
     */
    public void effect(ClientModel model){}

    /**
     * Constructs an {@code Event} with the specified description.
     *
     * @param description a brief text explaining the event details.
     */
    public Event(String description) {
        this.description = description;
    }

    /**
     * Bufferizes the event into the network layer to be sent or broadcasted.
     *
     * @param network the {@link NetworkLayer} used to handle the outgoing event.
     */
    public void bufferize(NetworkLayer network) {}


    public String getDescription() {
        return description;
    }

    /**
     * This method is called by the ClientController after the event has been processed and the model has been updated.
     * It can be used to trigger any additional actions that need to be performed after the event has been processed,
     * such as updating the view or sending additional requests to the server.
     *
     * @param controller the ClientController that processed the event
     */
    public void onEventProcessed(ClientController controller) {
    }
}
