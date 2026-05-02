package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    // TODO: da fare effect
}
