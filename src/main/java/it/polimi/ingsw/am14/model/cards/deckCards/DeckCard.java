package it.polimi.ingsw.am14.model.cards.deckCards;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.Card;
/**
 * Represents an abstract Deck Card within the game.
 * <p>
 * This class extends {@link Card} and serves as the foundation for all deck-bound cards,
 * which broadly categorize into Character cards (e.g., Artists, Builders, Gatherers)
 * and Event cards (e.g., Hunt, Paint, Shaman, or Sustenance events).
 * </p>
 * <p>
 * It enforces structural behaviors for managing how these cards are discarded from the play area
 * and how they position themselves onto the {@link Board} during the initial game setup.
 * Polymorphic serialization and deserialization are handled via Jackson annotations based on the "type" property.
 * </p>
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Artist.class, name = "artist"),
        @JsonSubTypes.Type(value = Builder.class, name = "builder"),
        @JsonSubTypes.Type(value = Gatherer.class, name = "gatherer"),
        @JsonSubTypes.Type(value = Hunter.class, name = "hunter"),
        @JsonSubTypes.Type(value = Inventor.class, name = "inventor"),
        @JsonSubTypes.Type(value = Shaman.class, name = "shaman"),
        @JsonSubTypes.Type(value = HuntEvent.class, name = "huntEvent"),
        @JsonSubTypes.Type(value = PaintEvent.class, name = "paintEvent"),
        @JsonSubTypes.Type(value = ShamanEvent.class, name = "shamanEvent"),
        @JsonSubTypes.Type(value = SustenanceEvent.class, name = "sustenanceEvent")
})

public abstract class DeckCard extends Card {
    /**
     * Constructs a new {@code DeckCard} with the specified historical age and unique identifier.
     * This constructor is designed to be invoked by concrete subclasses during JSON deserialization.
     *
     * @param age the historical age/era this card belongs to (passed to super)
     * @param id  the unique identifier of the card (passed to super)
     */
    public DeckCard(
            @JsonProperty("age") int age,
            @JsonProperty("id") int id
    ) {
        super(age, id);
    }
    /**
     * Handles the discarding routine for this card from the board components.
     * <p>
     * Subclasses implementing this method will clear their respective slot on the board.
     * If the implementing card is an Event card, this routine will also handle shifting
     * the card into the board's designated row for pending events ("toResolveEventRow").
     * </p>
     *
     * @param board     the {@link Board} instance where the discard action takes place
     * @param cardIndex the index position of the card within its current row
     */
    public abstract void discard(Board board, int cardIndex);
    // the card will set its reference to null and if it's an event card it will also add
    // itself to the "toResolveEventRow"
    /**
     * Coordinates the card's placement during Phase Four of the game setup.
     * <p>
     * This method dictates layout positioning when initializing the board rows.
     * Depending on the concrete subtype:
     * <ul>
     *   <li><b>Event cards:</b> place themselves into the board's Top Row.</li>
     *   <li><b>Character cards:</b> place themselves into the board's Bottom Row.</li>
     * </ul>
     * </p>
     *
     * @param board the {@link Board} being populated during initialization
     * @return the number of cards successfully positioned onto the board row
     */
    public abstract int setupPhaseFour(Board board);
    // is used in setup when we use the method "addDeckableBottomRow" and if the card is:
    // event: the card will add itself to the TopRow
    // character: the card will add itself to the BottomRow
    /**
     * Returns a string representation of the deck card, detailing its core identifiers
     * and specific subclass type name.
     *
     * @return a string summarizing the deck card's basic attributes
     */
    @Override
    public String toString() {
        return "DeckCard{" +
                "age=" + this.getAge() +
                ", id=" + this.getId() +
                "type: " + this.getClass().getSimpleName() +
                '}';
    }
}

