package it.polimi.ingsw.am14.model.cards.deckCards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;
/**
 * Represents a Hunter character card within the game.
 * <p>
 * This class extends {@link CharacterCard} and introduces a food-gathering mechanic based on hunting.
 * When its effect is applied, the hunter joins the player's tribe and, if certain conditions
 * are met (controlled by the {@code hasHam} property), triggers an immediate food bonus scaling
 * with the size of the player's hunting party.
 * </p>
 */
public class Hunter extends CharacterCard{

    private final boolean hasHam;
    /**
     * Constructs a new {@code Hunter} card with the specified age, identifier, and food trait availability.
     * This constructor is annotated with {@link JsonCreator} for Jackson JSON deserialization.
     *
     * @param age    the historical age/era this card belongs to (passed to super)
     * @param id     the unique identifier of the card (passed to super)
     * @param food   the boolean flag mapped from the "hasHam" JSON property, indicating if this hunter provides immediate food scaling
     */
    @JsonCreator
    public Hunter(
            @JsonProperty("age") int age,
            @JsonProperty("id") int id,
            @JsonProperty("hasHam") boolean food
    ) {
        super(age, id);
        this.hasHam = food;
    }

    private void gainFood(Player player){
        if(hasHam){
            int numHunters = player.getTribe().getHuntersSize();
            player.addToNumFood(numHunters);
        }
    }// if the player has ham, he will gain 1 food for each hunter in his tribe (including this one)
    /**
     * Applies the hunter's specific effect.
     * <p>
     * Registers this hunter instance within the player's tribe roster and subsequently
     * processes the food distribution logic via {@link #gainFood(Player)}.
     * </p>
     *
     * @param player  the {@link Player} who is playing or activating the card
     * @param context the current {@link Game} context (unused in this specific implementation)
     */
    @Override
    public void applyEffect(Player player, Game context) {
        player.getTribe().addHunter(this);
        gainFood(player);
    }  // we add the player to the tribe and then we call the gainFood method to give him food if the hunter picked
}
