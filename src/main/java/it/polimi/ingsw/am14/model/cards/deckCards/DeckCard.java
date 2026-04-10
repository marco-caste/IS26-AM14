package it.polimi.ingsw.am14.model.cards.deckCards;

import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.Card;

public abstract class DeckCard extends Card {

    public DeckCard(int age, int id) {
        super(age, id);
    }

    public abstract void discard(Board board, int CardIndex);
    // the card will set its reference to null and if it's an event card it will also add
    // itself to the "toResolveEventRow"

    public abstract int setupPhaseFour(Board board);
    // is used in setup when we use the method "addDeckableBottomRow" and if the card is:
    // event: the card will add itself to the TopRow
    // character: the card will add itself to the BottomRow

}

