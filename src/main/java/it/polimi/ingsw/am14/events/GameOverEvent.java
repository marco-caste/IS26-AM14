package it.polimi.ingsw.am14.events;

public class GameOverEvent extends ServerEvent {

    public GameOverEvent() {
        this.description = "The game was forced to end due to a disconnection.";
    }
}
