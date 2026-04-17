package it.polimi.ingsw.am14.events;

import it.polimi.ingsw.am14.model.gameLogic.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TotemPlacedEvent extends UpdateEvent{

    // attributes = update values for the view
    private String playerNickname;
    private char trackCardName;
    private int removedTotemIndex;

    // constructor
    public TotemPlacedEvent(String playerNickname, char trackCardName, int removedTotemPosition) {
        this.description = "Player " + playerNickname + " has placed their totem on track card " + trackCardName;
        this.playerNickname = playerNickname;
        this.trackCardName = trackCardName;
        this.removedTotemIndex = removedTotemPosition;
    }
}
