package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.gameLogic.*;
/**
 * Implements the {@link EffectStrategy} to represent a specific building effect.
 * When applied, this effect grants the player a variable amount of prestige points
 * based on the age of the last other event on the game board.
 */
public class ShamanicPenaltyShield implements EffectStrategy {

    /**
     * Applies the scoring effect to the specified player.
     * Calculates the prestige points to be added as 1 plus twice the age of the
     * last other event, and adds the result to the player's total prestige points.
     *
     * @param player the player who benefits from the effect
     * @param game   the current game instance
     */
    @Override
    public void applyEffect(Player player, Game game) {
        player.addToPrestigePoints(+ 1 + 2*game.getBoard().getLastOtherEvent().getAge());
    }
}
