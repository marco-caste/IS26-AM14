package it.polimi.ingsw.am14.model.cards.deckCards;


import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.boardComponents.Board;
/**
 * Represents an abstract Character card within the game deck.
 * <p>
 * This class serves as the base layer for all specific character card types (such as Artists or Builders).
 * It defines common game rules for characters, such as their default availability to be picked
 * and how they interact with the game {@link Board} during the setup and discarding phases.
 * </p>
 */
public abstract class CharacterCard extends DeckCard {
    /**
     * Constructs a new {@code CharacterCard} with the specified age and unique identifier.
     * This constructor is designed to be invoked by concrete subclasses during JSON deserialization.
     *
     * @param age the historical age/era this card belongs to (passed to super)
     * @param id  the unique identifier of the card (passed to super)
     */
    public CharacterCard(
            @JsonProperty("age") int age,
            @JsonProperty("id") int id
    ) {
        super(age, id);
    }
    /**
     * Determines whether this character card can be picked by a player.
     * <p>
     * By default, character cards do not have resource prerequisites or upfront food costs,
     * meaning they are always available to be picked.
     * </p>
     *
     * @param player the {@link Player} attempting to pick the card
     * @return always {@code true} as character cards have no picking restrictions
     */
    @Override
    public boolean isPickable(Player player) {
        return true;
    }
    /**
     * Handles the discarding process of this character card from the board.
     * <p>
     * Removes the card from the bottom deckable row of the specified game board
     * using its grid or list index.
     * </p>
     *
     * @param board     the {@link Board} instance from which the card is discarded
     * @param cardIndex the index position of the card within the row
     */
    @Override
    public void discard(Board board, int cardIndex) {
        board.removeFromBottomDeckableRow(cardIndex);
    }
    /**
     * Executes the specific setup actions required for character cards during "Phase Four"
     * of the game initialization.
     * <p>
     * Places this card instance into the bottom deckable row of the board and returns
     * the count of cards added.
     * </p>
     *
     * @param board the {@link Board} being initialized
     * @return the number of cards successfully added to the row (always {@code 1})
     */
    @Override
    public int setupPhaseFour(Board board) {
        board.addToBottomDeckableRow(this);
        return 1;
    }
}