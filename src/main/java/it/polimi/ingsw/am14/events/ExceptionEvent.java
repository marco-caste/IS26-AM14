package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.view.ClientModel;

public class ExceptionEvent extends Event {

    @JsonCreator
    public ExceptionEvent(
            @JsonProperty("description") String error) {
        super(error);
    }

    public void effect(ClientModel model) {
        model.setMessage(description);
        model.notifyObservers();
    }
}
