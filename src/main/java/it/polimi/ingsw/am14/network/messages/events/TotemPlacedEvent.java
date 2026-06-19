package it.polimi.ingsw.am14.network.messages.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.clientModelController.ClientModel;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.view.viewEvents.PositionUpdateViewEvent;

import java.util.ArrayList;


public class TotemPlacedEvent extends UpdateEvent {

    // attributes = update values for the view
    private String playerNickname;
    private char trackCardName;
    // only used for bufferization, will not be sent to the client
    private int gameId;
    private String first;
    private int numFood;
    private ArrayList<String> firstTurnOrderCard = new ArrayList<>();


    // constructor
    public TotemPlacedEvent(String playerNickname, char trackCardName, int gameId, int numFood, String first) {
        super("");
        this.description = "Player " + playerNickname + " has placed their totem on track card " + trackCardName;
        this.playerNickname = playerNickname;
        this.trackCardName = trackCardName;
        this.gameId = gameId;
        this.numFood = numFood;
        this.first = first;
    }


    @JsonCreator
    public TotemPlacedEvent(
            @JsonProperty("description") String description,
            @JsonProperty("nickname") String playerNickname,
            @JsonProperty("trackCardName") char trackCardName,
            @JsonProperty("numFood") int numFood,
            @JsonProperty("first") String first)

    {
        super(description);
        this.playerNickname = playerNickname;
        this.trackCardName = trackCardName;
        this.numFood = numFood;
        this.first = first;
    }

    public void bufferize(NetworkLayer networkLayer) {
        networkLayer.broadcastUpdate(this, gameId);
    }


    @Override
    public void effect(ClientModel model) {
        model.setMessage(description);
        model.updatePlayerPosition(playerNickname, trackCardName);
        model.removePlayerFromTurnOrderCard(playerNickname);

        if( first != null){
            firstTurnOrderCard.add(first);
            model.updatePlayerPosition(first, null);
            model.setPlayerOrderInTurnOrderCard(firstTurnOrderCard);
        }
        model.updatePlayerFood(playerNickname, numFood);
        model.checkAndEventuallySetPickState();
        model.notifyObservers(new PositionUpdateViewEvent(description));

    }

}
