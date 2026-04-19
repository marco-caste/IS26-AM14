package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginSuccesfulEvent extends ServerEvent {

    String playerNickname;

    public LoginSuccesfulEvent(String playerNickname) {
        super("");
        this.playerNickname = playerNickname;
        this.description = "Player \"" + playerNickname + "\" has logged in successfully.";
    }

    @JsonCreator
    public LoginSuccesfulEvent(
            @JsonProperty("description") String description,
            @JsonProperty("nickname") String playerNickname) {
        super(description);
        this.playerNickname = playerNickname;
    }
}
