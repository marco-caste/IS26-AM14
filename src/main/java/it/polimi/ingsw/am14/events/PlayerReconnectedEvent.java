package it.polimi.ingsw.am14.events;

public class PlayerReconnectedEvent extends ServerEvent {

    private JoinedGameEvent reconnectedEvent;

    public PlayerReconnectedEvent(JoinedGameEvent reconnectedEvent, String playerNickname) {
        this.reconnectedEvent = reconnectedEvent;
        this.description = "Player \"" + playerNickname + "\" has re-established their connection to the game.";
    }
}
