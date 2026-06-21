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

    @Override
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
