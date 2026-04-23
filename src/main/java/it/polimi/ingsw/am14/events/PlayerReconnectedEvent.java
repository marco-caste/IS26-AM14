package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.view.ClientModel;

public class PlayerReconnectedEvent extends ServerEvent {

    private JoinedGameEvent reconnectedEvent;

    public PlayerReconnectedEvent(JoinedGameEvent reconnectedEvent, String playerNickname) {
        super("");
        this.reconnectedEvent = reconnectedEvent;
        this.description = "Player \"" + playerNickname + "\" has re-established their connection to the game.";
    }

    @JsonCreator
    public PlayerReconnectedEvent(
            @JsonProperty("description") String description,
            @JsonProperty("event") JoinedGameEvent reconnectedEvent) {
        super(description);
        this.reconnectedEvent = reconnectedEvent;
    }

    @Override
    public void effect(ClientModel model) {
        model.updatePlayerReconnectedEvent(description, reconnectedEvent);
    }
}
