package it.polimi.ingsw.am14.model.cards.deckCards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;
/**
 * Represents a Shaman character card within the game.
 * <p>
 * This class extends {@link CharacterCard} and introduces a mechanic involving stars.
 * When its effect is applied, the shaman joins the player's tribe and immediately grants
 * a specific number of stars, which serve as a resource or modifier during future shaman-related events.
 * </p>
 */
public class Shaman extends CharacterCard {

    private final int numStars;
    /**
     * Constructs a new {@code Shaman} card with the specified age, unique identifier,
     * and number of stars.
     * This constructor is annotated with {@link JsonCreator} for Jackson JSON deserialization.
     *
     * @param age      the historical age/era this card belongs to (passed to super)
     * @param id       the unique identifier of the card (passed to super)
     * @param numStars the number of stars awarded to the player upon acquiring this card
     */
    @JsonCreator
    public Shaman(
            @JsonProperty("age") int age,
            @JsonProperty("id") int id,
            @JsonProperty("numStars") int numStars
    ) {
        super(age, id);
        this.numStars = numStars;
    }
    /**
     * Applies the shaman's specific effect.
     * <p>
     * Registers this shaman instance within the player's tribe roster and increments
     * the player's total star count by the value specified in {@code numStars}.
     * </p>
     *
     * @param player  the {@link Player} who is playing or activating the card
     * @param context the current {@link Game} context (unused in this specific implementation)
     */
    @Override
    public void applyEffect(Player player, Game context) { //
        player.getTribe().addShaman(this);
        player.addToStars(this.numStars);
        //it gives the player a number of stars that will be useful for the shaman events.
    }


}



