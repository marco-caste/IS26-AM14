package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.gameLogic.*;
/**
 * Implements the {@link EffectStrategy} to represent a specific building effect.
 * When applied, this effect grants the player an amount of food equal to the
 * number of artists currently in their tribe.
 */
public class SustenanceDiscountArtists implements EffectStrategy {
    /**
     * Applies the effect to the specified player.
     * Retrieves the number of artists in the player's tribe and adds that amount
     * to the player's total food count.
     *
     * @param player the player who benefits from the effect
     * @param game   the current game instance
     */
    @Override
    public void applyEffect(Player player, Game game) {
        player.addToNumFood(player.getTribe().getArtistsSize());
    }
}
