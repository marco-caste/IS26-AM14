package it.polimi.ingsw.am14.model.cards.deckCards;


import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.boardComponents.Board;

public abstract class CharacterCard extends DeckCard {

    public CharacterCard(int age, int id) {
        super(age, id);
    }

    @Override
    public boolean isPickable(Player player) {
        return true;
    }

    @Override
    public void discard(Board board, int cardIndex) {
        board.removeFromBottomDeckableRow(cardIndex);
    }
    @Override
    public int setupPhaseFour(Board board) {
        board.addToBottomDeckableRow(this);
        return 1;
    }
}