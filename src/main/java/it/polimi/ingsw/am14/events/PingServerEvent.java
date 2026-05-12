package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PingServerEvent extends ServerEvent {

    @JsonCreator
    public PingServerEvent(
            @JsonProperty("description") String description) {
        super(description);
    }
}
