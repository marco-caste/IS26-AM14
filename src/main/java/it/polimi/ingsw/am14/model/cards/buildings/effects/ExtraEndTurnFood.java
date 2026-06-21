package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.gameLogic.*;
/**
 * Implements the {@link EffectStrategy} to represent a specific building effect.
 * When applied, this effect grants the player 1 additional food automatically
 * at the start of each round.
 */
public class ExtraEndTurnFood implements EffectStrategy {
/**
 * Applies the extra food effect to the specified player.
 * Updates the player's attributes to automatically receive 1 extra food
 * at the start of the round.
 *
 * @param player the player who benefits from the effect
 * @param game   the current game instance
 */
    @Override
    public void applyEffect(Player player, Game game) {
        player.setAddFoodStartOfRound(1);
    }
}
