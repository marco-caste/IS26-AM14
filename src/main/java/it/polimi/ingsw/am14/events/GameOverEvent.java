package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.view.ClientModel;

public class GameOverEvent extends ServerEvent {

    public GameOverEvent() {
        super("The game was forced to end due to a disconnection.");
    }

    @JsonCreator
    public GameOverEvent(
            @JsonProperty("description") String description) {
        super(description);
    }

    @Override
    public void effect(ClientModel model) {
        model.updateGameOverEvent(description);
    }
}
