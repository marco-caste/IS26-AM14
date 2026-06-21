package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.gameLogic.*;
/**
 * Implements the {@link EffectStrategy} to represent a specific building effect.
 * When applied, this effect grants the player 3 additional shamanic stars.
 */
public class ExtraShamanicStars implements EffectStrategy {
    /**
     * Applies the extra shamanic stars effect to the specified player.
     * Adds 3 stars to the player's total count.
     *
     * @param player the player who benefits from the effect
     * @param game   the current game instance
     */
    @Override
    public void applyEffect(Player player, Game game) {
        player.addToStars(3);
    }
}
