package it.polimi.ingsw.am14.Events;

public class LoginSuccesfulEvent extends ServerEvent {

    String playerNickname;

    public LoginSuccesfulEvent(String playerNickname) {
        this.playerNickname = playerNickname;
        this.description = "Player \"" + playerNickname + "\" has logged in successfully.";
    }
}
