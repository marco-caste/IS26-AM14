package it.polimi.ingsw.am14.network.messages.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.clientModelController.ClientModel;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.view.viewEvents.PositionUpdateViewEvent;

import java.util.ArrayList;
/**
 * Event representing a player placing their totem on a specific track card.
 * Inherits from {@link UpdateEvent}.
 * This event is generated when the turn order is being decided and the player chooses a position.
 */

public class TotemPlacedEvent extends UpdateEvent {

    // attributes = update values for the view
    private String playerNickname;
    private char trackCardName;
    // only used for bufferization, will not be sent to the client
    private int gameId;
    private String first;
    private int numFood;
    private ArrayList<String> firstTurnOrderCard = new ArrayList<>();


    /**
     * Constructs a {@code TotemPlacedEvent} triggered by the server when a player successfully places their totem.
     *
     * @param playerNickname the nickname of the player who placed the totem.
     * @param trackCardName  the character identifier of the track card where the totem was placed.
     * @param gameId         the unique ID of the game (used only for server-side bufferization).
     * @param numFood        the updated amount of food possessed by the player.
     * @param first          the nickname of the player who became first in the turn order (if applicable).
     */

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


    /**
     * Constructs a {@code TotemPlacedEvent} from JSON data, used by Jackson upon receiving a message.
     *
     * @param description    the description of the event.
     * @param playerNickname the nickname of the player who placed the totem.
     * @param trackCardName  the character identifier of the chosen track card.
     * @param numFood        the updated amount of food for the player.
     * @param first          the nickname of the first player in the new turn order (if any).
     */

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

    /**
     * Bufferizes the event into the network layer to broadcast the totem placement
     * to all connected clients in the specified game.
     *
     * @param networkLayer the network layer used to broadcast the event.
     */

    public void bufferize(NetworkLayer networkLayer) {
        networkLayer.broadcastUpdate(this, gameId);
    }


    /**
     * Applies the effect of this event to the provided client model, updating the player's
     * position, their resources, modifying the turn order card, and notifying the view observers.
     *
     * @param model the {@link ClientModel} on which to apply the state changes.
     */

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
