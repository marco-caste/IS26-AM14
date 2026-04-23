package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.NetworkLayer;


public class TotemPlacedEvent extends UpdateEvent {

    // attributes = update values for the view
    private String playerNickname;
    private char trackCardName;
    private int removedTotemIndex;
    // only used for bufferization, will not be sent to the client
    private int gameId;

    // constructor
    public TotemPlacedEvent(String playerNickname, char trackCardName, int removedTotemPosition, int gameId) {
        super("");
        this.description = "Player " + playerNickname + " has placed their totem on track card " + trackCardName;
        this.playerNickname = playerNickname;
        this.trackCardName = trackCardName;
        this.removedTotemIndex = removedTotemPosition;
        this.gameId = gameId;
    }

    @JsonCreator
    public TotemPlacedEvent(
            @JsonProperty("description") String description,
            @JsonProperty("nickname") String playerNickname,
            @JsonProperty("trackCardName") char trackCardName,
            @JsonProperty("removedTotemIdx") int removedTotemIndex) {
        super(description);
        this.playerNickname = playerNickname;
        this.trackCardName = trackCardName;
        this.removedTotemIndex = removedTotemIndex;
    }

    public void bufferize(NetworkLayer networkLayer) {
        networkLayer.broadcastUpdate(this, gameId);
    }


    @Override
    public void effect(it.polimi.ingsw.am14.view.ClientModel model) {
        model.setMessage(description);
        model.updatePlayerPosition(playerNickname, trackCardName);
        model.removePlayerFromTurnOrderCard(removedTotemIndex);
        model.notifyObservers();
    }

}
