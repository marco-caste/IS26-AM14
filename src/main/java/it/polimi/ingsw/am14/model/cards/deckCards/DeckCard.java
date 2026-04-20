package it.polimi.ingsw.am14.model.cards.deckCards;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.Card;

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

    public DeckCard(
            @JsonProperty("age") int age,
            @JsonProperty("id") int id
    ) {
        super(age, id);
    }

    public abstract void discard(Board board, int CardIndex);
    // the card will set its reference to null and if it's an event card it will also add
    // itself to the "toResolveEventRow"

    public abstract int setupPhaseFour(Board board);
    // is used in setup when we use the method "addDeckableBottomRow" and if the card is:
    // event: the card will add itself to the TopRow
    // character: the card will add itself to the BottomRow

    @Override
    public String toString() {
        return "DeckCard{" +
                "age=" + this.getAge() +
                ", id=" + this.getId() +
                "type: " + this.getClass().getSimpleName() +
                '}';
    }
}

