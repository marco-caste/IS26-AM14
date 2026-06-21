package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.gameLogic.*;
/**
 * Implements the {@link EffectStrategy} to represent a specific building effect.
 * When applied, this effect grants the player additional prestige points calculated
 * as 5 times the age of the last event present on the game board, effectively
 * doubling the standard shamanic reward.
 */
public class DoubleShamanicReward implements EffectStrategy {
    /**
     * Applies the double shamanic reward effect to the specified player.
     * Retrieves the age of the last event from the game board and adds the
     * corresponding calculated prestige points to the player's total score.
     *
     * @param player the player who benefits from the effect
     * @param game   the current game instance
     */
    @Override
    public void applyEffect(Player player, Game game) {
        player.addToPrestigePoints(5*game.getBoard().getLastOtherEvent().getAge());
    }
}
