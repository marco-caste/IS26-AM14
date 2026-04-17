package it.polimi.ingsw.am14.events;

import it.polimi.ingsw.am14.model.cards.Card;

public class CardPickedEvent extends UpdateEvent{

    String playerNickname;
    int cardID;
    int row;

    public CardPickedEvent(String playerNickname, Card card, int row) {
        int ID = card == null ? -1 : card.getId();

        if (ID < 0) {
            this.description = "Player " + playerNickname + " has skipped the turn";
            this.playerNickname = playerNickname;
            this.cardID = -1;
            this.row = -1;
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
        this.description = "Player " + playerNickname + " has picked card with ID " + cardID + " from " + rowName;
    }
}
