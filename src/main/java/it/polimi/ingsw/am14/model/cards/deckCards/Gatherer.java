package it.polimi.ingsw.am14.model.cards.deckCards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;
/**
 * Represents a Gatherer character card within the game.
 * <p>
 * This class extends {@link CharacterCard} and introduces a specialized discount mechanic
 * for gathering actions. When its effect is applied, the gatherer joins the player's
 * tribe and provides a permanent bonus to the player's gathering capabilities.
 * </p>
 */
public class Gatherer extends CharacterCard{

    private final int  discount;
    // it's a positive number that represents
    // the discount on the gathering event
    /**
     * Constructs a new {@code Gatherer} card with the specified age, identifier, and gathering discount.
     * This constructor is annotated with {@link JsonCreator} for Jackson JSON deserialization.
     *
     * @param age      the historical age/era this card belongs to (passed to super)
     * @param id       the unique identifier of the card (passed to super)
     * @param discount the positive value representing the reduction or bonus applied during gathering events
     */
    @JsonCreator
    public Gatherer (
            @JsonProperty("age") int age,
            @JsonProperty("id") int id,
            @JsonProperty("discount") int discount
    ) {
        super(age, id);
        this.discount = discount;
    }
    /**
     * Applies the gatherer's specific effect.
     * <p>
     * Welcomes this card instance into the player's tribe roster and permanently
     * updates the player's cumulative gathering discount or efficiency modifier
     * by the value specified in {@code discount}.
     * </p>
     *
     * @param player  the {@link Player} who is playing or activating the card
     * @param context the current {@link Game} context (unused in this specific implementation)
     */
    @Override
    public void applyEffect(Player player, Game context) {
        player.getTribe().addGatherer(this);
        player.addToGatheringDiscount(this.discount);
        // updates the GatheringDiscount of tha player
    }

}
