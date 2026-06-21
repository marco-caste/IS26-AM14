package it.polimi.ingsw.am14.network.messages.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.view.viewEvents.ExceptionViewEvent;
import it.polimi.ingsw.am14.clientModelController.ClientModel;

/**
 * Event representing an exception or error that occurred during the game.
 * Inherits from {@link Event}.
 * It contains the error description to be displayed to the user via the client model.
 */

public class ExceptionEvent extends Event {

    /**
     * Constructs an {@code ExceptionEvent} from JSON data, used by Jackson upon receiving an error message.
     *
     * @param error the description of the error or exception that occurred.
     */

    @JsonCreator
    public ExceptionEvent(
            @JsonProperty("description") String error) {
        super(error);
    }

    /**
     * Applies the effect of this event to the provided client model, setting the error message
     * and notifying the view observers.
     *
     * @param model the {@link ClientModel} representing the client-side state of the game.
     */

    public void effect(ClientModel model) {
        model.setMessage(description);
        model.notifyObservers(new ExceptionViewEvent(description));
    }
}
