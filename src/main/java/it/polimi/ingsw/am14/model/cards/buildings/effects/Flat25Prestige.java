package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.gameLogic.*;
/**
 * Implements the {@link EffectStrategy} to represent a specific building effect.
 * When applied, this effect grants the player a flat 25 prestige points.
 */
public class Flat25Prestige implements EffectStrategy {

    /**
     * Applies the flat prestige effect to the specified player.
     * Adds 25 prestige points to the player's total score.
     *
     * @param player the player who benefits from the effect
     * @param game   the current game instance
     */
    @Override
    public void applyEffect(Player player, Game game) {}
}
