package it.polimi.ingsw.am14.model.cards.deckCards;

import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.boardComponents.Board;

public abstract class EventCard extends DeckCard{

    public EventCard(int age, int id) {
        super(age, id);
    }


    @Override
    public boolean isPickable(Player player) {
        return false; // events are not pickable
    }

    @Override
    public int setupPhaseFour(Board board) {
        board.addToTopDeckableRow(this);
        return 0;
    }

    @Override
    public void discard(Board board, int cardIndex) {
        board.addToResolvingOtherEventsRow(this);
        board.removeFromBottomDeckableRow(cardIndex);
    }

    public void removeEvent(Game game) {
        game.getBoard().removeLastOtherEvent();
    }
}
