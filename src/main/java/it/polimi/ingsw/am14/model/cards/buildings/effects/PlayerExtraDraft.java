package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.gameLogic.*;
/**
 * Implements the {@link EffectStrategy} to represent a specific building effect.
 * When applied, this effect grants the player an extra draft pick.
 */
public class PlayerExtraDraft implements EffectStrategy {
    /**
     * Applies the extra draft effect to the specified player.
     * Increases the player's top pick allowance by 1.
     *
     * @param player the player who benefits from the effect
     * @param game   the current game instance
     */
    @Override
    public void applyEffect(Player player, Game game) {
        player.setAddTopPick(1);
    }
}
