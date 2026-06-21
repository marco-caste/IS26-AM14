package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.gameLogic.*;
/**
 * Implements the {@link EffectStrategy} to represent a specific building effect.
 * When applied, this effect grants the player 2 prestige points for each inventor
 * currently in their tribe.
 */
public class ScoringInventors implements EffectStrategy {
    /**
     * Applies the scoring effect to the specified player.
     * Retrieves the number of inventors in the player's tribe, multiplies it by 2,
     * and adds the result to the player's total prestige points.
     *
     * @param player the player who benefits from the effect
     * @param game   the current game instance
     */
    @Override
    public void applyEffect(Player player, Game game) {
        player.addToPrestigePoints(2*player.getTribe().getInventorsSize());
    }
}
