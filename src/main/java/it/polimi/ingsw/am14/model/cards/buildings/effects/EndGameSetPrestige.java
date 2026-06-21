package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.gameLogic.*;

/**
 * Implements the {@link EffectStrategy} to represent a specific building effect.
 * When applied, this effect grants the player 6 additional prestige points for
 * each completed set of cards they possess at the end of the game.
 */
public class EndGameSetPrestige implements EffectStrategy {
    /**
     * Applies the end-game set prestige effect to the specified player.
     * Retrieves the number of completed sets the player has and adds 6 prestige
     * points per completed set to their total score.
     *
     * @param player the player who benefits from the effect
     * @param game   the current game instance
     */
    @Override
    public void applyEffect(Player player, Game game) {
        player.addToPrestigePoints(6*player.getNumSetCompleted());
    }
}
