package it.polimi.ingsw.am14.model.cards.deckCards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.boardComponents.Board;
/**
 * Represents an abstract Event card within the game deck.
 * <p>
 * This class extends {@link DeckCard} and acts as the base layer for all event-type cards
 * (such as Hunt, Paint, Shaman, or Sustenance events). Unlike character cards, event cards
 * alter the state of the game globally when triggered and follow a distinct cycle of
 * initialization, resolution, and cleanup on the {@link Board}.
 * </p>
 */
public abstract class EventCard extends DeckCard{
    /**
     * Constructs a new {@code EventCard} with the specified age and unique identifier.
     * This constructor is designed to be invoked by concrete subclasses during JSON deserialization.
     *
     * @param age the historical age/era this card belongs to (passed to super)
     * @param id  the unique identifier of the card (passed to super)
     */
    public EventCard(
            @JsonProperty("age") int age,
            @JsonProperty("id") int id
    ) {
        super(age, id);
    }

    /**
     * Determines whether this event card can be directly picked by a player.
     * <p>
     * By definition, event cards cannot be purchased or kept in a player's hand.
     * They are triggered through board mechanics instead.
     * </p>
     *
     * @param player the {@link Player} checking availability
     * @return always {@code false} as event cards are never directly pickable
     */
    @Override
    public boolean isPickable(Player player) {
        return false; // events are not pickable
    }
    /**
     * Executes the specific setup actions required for event cards during "Phase Four"
     * of the game initialization.
     * <p>
     * Places this event instance into the top deckable row of the board.
     * </p>
     *
     * @param board the {@link Board} being initialized
     * @return always {@code 0}, as event cards do not increment the character row counts
     */
    @Override
    public int setupPhaseFour(Board board) {
        board.addToTopDeckableRow(this);
        return 0;
    }
    /**
     * Handles the discarding routine when an event card is triggered or replaced on the board.
     * <p>
     * Moves this event into the board's designated row for events currently being resolved
     * ("resolvingOtherEventsRow") and clears its original slot in the deckable row.
     * </p>
     *
     * @param board     the {@link Board} instance where the card operations occur
     * @param cardIndex the index position of the card within its original row
     */
    @Override
    public void discard(Board board, int cardIndex) {
        board.addToResolvingOtherEventsRow(this);
        board.removeFromBottomDeckableRow(cardIndex);
    }
    /**
     * Removes this event from the active evaluation context, typically called
     * once the event's effects have finished resolving.
     * <p>
     * Tells the board to clean up and pop the last recorded active event.
     * </p>
     *
     * @param game the current {@link Game} instance containing the board to be updated
     */
    public void removeEvent(Game game) {
        game.getBoard().removeLastOtherEvent();
    }
    /**
     * Returns a string representation of the event card, detailing its identifiers
     * and specific concrete event type name.
     *
     * @return a string summarizing the event card's state
     */
    @Override
    public String toString() {
        return "Event{" +
                "age=" + this.getAge() +
                ", id=" + this.getId() +
                ", type=" + this.getClass().getSimpleName() +
                '}';
    }
}
