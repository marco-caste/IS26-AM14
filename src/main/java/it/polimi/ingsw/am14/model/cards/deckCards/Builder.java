package it.polimi.ingsw.am14.model.cards.deckCards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;
/**
 * Represents a Builder character card within the game.
 * <p>
 * This class extends {@link CharacterCard} and introduces attributes for reducing building purchase costs
 * and awarding end-game prestige points. When its effect is applied, the builder joins the player's
 * tribe and grants a permanent discount on future building purchases.
 * </p>
 */
public class Builder extends CharacterCard{

    private final int buildDiscount ; // positive number
    private final int prestigePoints; // prestige that the player will get at the end of the game from this builder
    /**
     * Constructs a new {@code Builder} card with the specified attributes, discounts, and point rewards.
     * This constructor is annotated with {@link JsonCreator} for Jackson JSON deserialization.
     *
     * @param age            the historical age/era this card belongs to (passed to super)
     * @param id             the unique identifier of the card (passed to super)
     * @param buildDiscount  the cost reduction value applied to building purchases (must be a positive number)
     * @param prestigePoints the amount of prestige points this builder awards at the end of the game
     */
    @JsonCreator
    public Builder(
            @JsonProperty("age") int age,
            @JsonProperty("id") int id,
            @JsonProperty("buildDiscount") int buildDiscount,
            @JsonProperty("prestigePoints") int prestigePoints
    ) {
        super(age, id);
        this.buildDiscount = buildDiscount;
        this.prestigePoints = prestigePoints;
    }

    public int getPrestigePoints(){
        return this.prestigePoints;
    }
    /**
     * Applies the builder's specific effect.
     * <p>
     * Adds this card instance to the player's tribe roster and permanently increases
     * the player's cumulative building discount by the value specified in {@code buildDiscount}.
     * </p>
     *
     * @param player  the {@link Player} who is playing or activating the card
     * @param context the current {@link Game} context (unused in this specific implementation)
     */
    @Override
    public void applyEffect(Player player, Game context) {
        player.getTribe().addBuilder(this);
        player.addToBuildingDiscount(this.buildDiscount);
    }
}