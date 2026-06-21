package it.polimi.ingsw.am14.model.cards.deckCards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;
/**
 * Represents an Artist character card within the game.
 * <p>
 * This class extends {@link CharacterCard}. When its effect is applied,
 * the artist is officially welcomed and registered into the purchasing player's tribe.
 * </p>
 */
public class Artist extends CharacterCard {
    /**
     * Constructs a new {@code Artist} card with the specified age and unique identifier.
     * This constructor is annotated with {@link JsonCreator} for Jackson JSON deserialization.
     *
     * @param age the historical age/era this card belongs to (passed to super)
     * @param id  the unique identifier of the card (passed to super)
     */
    @JsonCreator
    public Artist(
            @JsonProperty("age") int age,
            @JsonProperty("id") int id
    ) {
        super(age, id);
    }
    /**
     * Applies the artist's specific effect, which adds this card instance
     * directly to the player's tribe roster.
     *
     * @param player  the {@link Player} who is playing or activating the card
     * @param context the current {@link Game} context (unused in this specific implementation)
     */
    @Override
    public void applyEffect(Player player, Game context) {
        player.getTribe().addArtist(this);
    }
}
