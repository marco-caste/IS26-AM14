package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.gameLogic.*;
/**
 * Implements the {@link EffectStrategy} to represent a specific building effect.
 * When applied, this effect grants the player additional food and prestige points
 * equal to the number of hunters currently in their tribe.
 */
public class HuntExtraFoodAndPrestige implements EffectStrategy {
    /**
     * Applies the extra food and prestige effect to the specified player.
     * Retrives the number of hunters in the player's tribe and adds that amount
     * to both the player's food total and prestige points.
     *
     * @param player the player who benefits from the effect
     * @param game   the current game instance
     */
    @Override
    public void applyEffect(Player player, Game game) {
        player.addToNumFood(player.getTribe().getHuntersSize());
        player.addToPrestigePoints(player.getTribe().getHuntersSize());
    }
}
