package it.polimi.ingsw.am14.Events;

public class TotemPlacedEvent extends UpdateEvent{

    // attributes = update values for the view
    String playerNickname;
    char trackCardName;

    // constructor
    public TotemPlacedEvent(String playerNickname, char trackCardName) {
        this.description = "Player " + playerNickname + " has placed their totem on track card " + trackCardName;
        this.playerNickname = playerNickname;
        this.trackCardName = trackCardName;
    }
}
