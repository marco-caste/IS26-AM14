package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.gameLogic.*;
/**
 * Implements the {@link EffectStrategy} to represent a specific building effect.
 * When applied, this effect grants the player 5 units of food.
 */
public class SetCompletionFood implements EffectStrategy {
    /**
     * Applies the effect to the specified player.
     * Adds 5 units of food to the player's total food count.
     *
     * @param player the player who benefits from the effect
     * @param game   the current game instance
     */
    @Override
    public void applyEffect(Player player, Game game) {
        player.addToNumFood(5);
    }
}
