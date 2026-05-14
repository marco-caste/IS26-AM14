package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.clientModel.ClientModel;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class CardPickedEvent extends UpdateEvent {

    private String playerNickname;
    private int cardID;
    private int row;
    private int playerFood;
    private int playerPrestigePoints;
    private int playerNumStars;
    private int playerBuildingDiscount;
    private int playerGatheringDiscount;
    private ArrayList<String> updatedTurnOrderCard;
    private int gameId; // only used for bufferization, will not be sent to the client

    // called by the model
    public CardPickedEvent(String playerNickname, Card card, int row, int playerFood, int playerPrestigePoints, Optional<ArrayList<Player>> updatedTurnOrderCard, int gameId, int playerBuildingDiscount, int playerGatheringDiscount, int playerNumStars) {
        super("");
        int ID = card == null ? -1 : card.getId();

        if (ID < 0) {
            this.description += "Player " + playerNickname + " has skipped the turn";
            this.playerNickname = playerNickname;
            this.cardID = -1;
            this.row = -1;
            this.playerFood = playerFood;
            this.playerPrestigePoints = playerPrestigePoints;
            this.playerBuildingDiscount = playerBuildingDiscount;
            this.playerNumStars = playerNumStars;
            this.playerGatheringDiscount = playerGatheringDiscount;
            return;
        }

        String rowName;
        switch (row) {
            case 0 -> rowName = "TOP_DECKABLE_ROW";
            case 1 -> rowName = "TOP_BUILDING_ROW";
            case 2 -> rowName = "BOTTOM_DECKABLE_ROW";
            case 3 -> rowName = "BOTTOM_BUILDING_ROW";
            default -> throw new IllegalArgumentException("Row must be between 0 and 3");
        }
        this.playerNickname = playerNickname;
        this.cardID = ID;
        this.row = row;
        this.playerFood = playerFood;
        this.playerPrestigePoints = playerPrestigePoints;
        this.gameId = gameId;
        this.description += "Player " + playerNickname + " has picked card with ID " + cardID + " from " + rowName;

        updatedTurnOrderCard.ifPresentOrElse(
                (turnOrder) -> {
                    this.updatedTurnOrderCard = turnOrder.stream().
                            map(Player::getNickname).
                            collect(Collectors.toCollection(ArrayList::new));
                    this.description += "Was " + playerNickname + "'s last pick, turnOrderCard has been updated\n";
                },
                () -> this.updatedTurnOrderCard = null
        );
    }

    // called by the JSON parser
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
            @JsonProperty("playerNumStars") int playerNumStars)
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
        this.playerBuildingDiscount = playerBuildingDiscount;
    }

    public void bufferize(NetworkLayer networkLayer) {
        networkLayer.broadcastUpdate(this, gameId);
    }


    // method effect to update the client model

    @Override
    public void effect(ClientModel model) {
        model.setMessage(description);
        if (cardID != -1) {
            model.updatePlayerTribe(playerNickname, cardID);
            model.removePickedCardFromBoard(row, cardID);
            model.updatePickPlayerStats(playerNickname, playerFood, playerPrestigePoints, playerBuildingDiscount, playerGatheringDiscount, playerNumStars);
        }
        model.setPlayerOrderInTurnOrderCard(updatedTurnOrderCard);
        model.notifyObservers("pickUpdate");
    }
}
