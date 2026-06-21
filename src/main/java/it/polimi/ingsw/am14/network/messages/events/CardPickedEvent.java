package it.polimi.ingsw.am14.network.messages.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.view.viewEvents.PickUpdateViewEvent;
import it.polimi.ingsw.am14.clientModelController.ClientModel;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Event representing a card being picked by a player during the game.
 * Inherits from {@link UpdateEvent}.
 * It contains the necessary information to update the client-side model,
 * including new player stats, board state, and turn order if the pick phase ends.
 */

public class CardPickedEvent extends UpdateEvent {

    private final String playerNickname;
    private final int cardID;
    private final int row;
    private final int playerFood;
    private final int playerPrestigePoints;
    private final int playerNumStars;
    private final boolean wasLastPick;
    private final int playerBuildingDiscount;
    private final int playerGatheringDiscount;
    private ArrayList<String> updatedTurnOrderCard;
    private int gameId; // only used for bufferization, will not be sent to the client

    // called by the model
    /**
     * Constructs a {@code CardPickedEvent} triggered by the server model.
     *
     * @param playerNickname          the nickname of the player who picked the card.
     * @param card                    the {@link Card} that was picked, or null if starting the pick phase.
     * @param row                     the row index from which the card was picked.
     * @param playerFood              the updated food amount of the player.
     * @param playerPrestigePoints    the updated prestige points of the player.
     * @param updatedTurnOrderCard    an {@link Optional} containing the updated turn order, if changed.
     * @param gameId                  the ID of the game this event belongs to.
     * @param playerBuildingDiscount  the updated building discount of the player.
     * @param playerGatheringDiscount the updated gathering discount of the player.
     * @param playerNumStars          the updated number of stars for the player.
     * @param wasLastPick             true if this was the last pick for the current player.
     */

    public CardPickedEvent(String playerNickname, Card card, int row, int playerFood, int playerPrestigePoints, Optional<ArrayList<Player>> updatedTurnOrderCard, int gameId, int playerBuildingDiscount, int playerGatheringDiscount, int playerNumStars, boolean wasLastPick) {
        super("");
        int ID = card == null ? -1 : card.getId();


        String rowName;
        switch (row) {
            case 0, 1 -> rowName = "top row";
            case 2, 3 -> rowName = "bottom row";
            default -> throw new IllegalArgumentException("Row must be between 0 and 3");
        }

        this.playerNickname = playerNickname;
        this.cardID = ID;
        this.row = row;
        this.playerFood = playerFood;
        this.playerPrestigePoints = playerPrestigePoints;
        this.playerBuildingDiscount = playerBuildingDiscount;
        this.playerNumStars = playerNumStars;
        this.wasLastPick = wasLastPick;
        this.playerGatheringDiscount = playerGatheringDiscount;
        this.gameId = gameId;

        if (ID < 0)
            this.description += "Pick phase has started. ";
        else
            this.description += "Player " + playerNickname + " picked card " + ID + ". ";


        updatedTurnOrderCard.ifPresentOrElse(
                (turnOrder) -> {
                    this.updatedTurnOrderCard = turnOrder.stream().
                            map(Player::getNickname).
                            collect(Collectors.toCollection(ArrayList::new));
                    this.description += "Was " + playerNickname + "'s last pick, turnOrderCard has been updated.";
                },
                () -> this.updatedTurnOrderCard = null
        );
    }

    // called by the JSON parser
    /**
     * Constructs a {@code CardPickedEvent} from JSON data, used by Jackson upon receiving a message.
     *
     * @param playerNickname          the nickname of the player who picked the card.
     * @param cardID                  the ID of the picked card.
     * @param row                     the row index from which the card was picked.
     * @param playerFood              the updated food amount of the player.
     * @param playerPrestigePoints    the updated prestige points of the player.
     * @param updatedTurnOrderCard    the updated turn order list of player nicknames.
     * @param description             the description of the event.
     * @param playerBuildingDiscount  the updated building discount of the player.
     * @param playerGatheringDiscount the updated gathering discount of the player.
     * @param playerNumStars          the updated number of stars for the player.
     * @param wasLastPick             true if this was the last pick for the current player.
     */

    @JsonCreator
    public CardPickedEvent(
            @JsonProperty("nickname") String playerNickname,
            @JsonProperty("cardID") int cardID,
            @JsonProperty("row") int row,
            @JsonProperty("playerFood") int playerFood,
            @JsonProperty("playerPrestigePoints") int playerPrestigePoints,
            @JsonProperty("updatedTurnOrderCard") ArrayList<String> updatedTurnOrderCard,
            @JsonProperty("description") String description,
            @JsonProperty("playerBuildingDiscount") int playerBuildingDiscount,
            @JsonProperty("playerGatheringDiscount") int  playerGatheringDiscount,
            @JsonProperty("playerNumStars") int playerNumStars,
            @JsonProperty("wasLastPick") boolean wasLastPick)
    {
        super(description);
        this.playerNickname = playerNickname;
        this.cardID = cardID;
        this.row = row;
        this.playerFood = playerFood;
        this.playerPrestigePoints = playerPrestigePoints;
        this.updatedTurnOrderCard = updatedTurnOrderCard;
        this.playerGatheringDiscount = playerGatheringDiscount;
        this.playerNumStars = playerNumStars;
        this.wasLastPick = wasLastPick;
        this.playerBuildingDiscount = playerBuildingDiscount;
    }

    /**
     * Bufferizes the event into the network layer to be broadcasted to all connected clients.
     *
     * @param networkLayer the network layer used to broadcast the event.
     */

    @Override
    public void bufferize(NetworkLayer networkLayer) {
        networkLayer.broadcastUpdate(this, gameId);
    }


    // method effect to update the client model
    /**
     * Applies the effect of this event to the provided client model, updating the local state
     * and notifying the view observers.
     *
     * @param model the {@link ClientModel} representing the client-side state of the game.
     */

    @Override
    public void effect(ClientModel model) {
        model.setMessage(description);
        if (cardID != -1) {
            model.updatePlayerTribe(playerNickname, cardID);
            model.removePickedCardFromBoard(row, cardID);
            model.updatePickPlayerStats(playerNickname, playerFood, playerPrestigePoints,playerNumStars, playerBuildingDiscount, playerGatheringDiscount);
        }

        System.out.println(updatedTurnOrderCard);
        System.out.println(wasLastPick);
        if (wasLastPick) {
            model.updatePlayerPosition(playerNickname, null );
            model.setPlayerOrderInTurnOrderCard(updatedTurnOrderCard);
        }
        model.notifyObservers(new PickUpdateViewEvent(description));
    }
}
