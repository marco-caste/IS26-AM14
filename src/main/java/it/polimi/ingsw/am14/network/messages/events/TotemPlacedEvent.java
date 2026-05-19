package it.polimi.ingsw.am14.network.messages.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.view.viewEvents.PositionUpdateViewEvent;


public class TotemPlacedEvent extends UpdateEvent {

    // attributes = update values for the view
    private String playerNickname;
    private char trackCardName;
    // only used for bufferization, will not be sent to the client
    private int gameId;

    // constructor
    public TotemPlacedEvent(String playerNickname, char trackCardName, int gameId) {
        super("");
        this.description = "Player " + playerNickname + " has placed their totem on track card " + trackCardName;
        this.playerNickname = playerNickname;
        this.trackCardName = trackCardName;
        this.gameId = gameId;
    }

    @JsonCreator
    public TotemPlacedEvent(
            @JsonProperty("description") String description,
            @JsonProperty("nickname") String playerNickname,
            @JsonProperty("trackCardName") char trackCardName) {
        super(description);
        this.playerNickname = playerNickname;
        this.trackCardName = trackCardName;
    }

    public void bufferize(NetworkLayer networkLayer) {
        networkLayer.broadcastUpdate(this, gameId);
    }


    @Override
    public void effect(it.polimi.ingsw.am14.clientModelController.ClientModel model) {
//        model.setMessage(description);
        model.updatePlayerPosition(playerNickname, trackCardName);
        model.removePlayerFromTurnOrderCard(playerNickname);
        model.checkAndEventuallySetPickState();
        model.notifyObservers(new PositionUpdateViewEvent(description));
    }

}
