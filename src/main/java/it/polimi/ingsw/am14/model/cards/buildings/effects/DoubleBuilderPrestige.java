package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.gameLogic.*;
import it.polimi.ingsw.am14.model.cards.deckCards.*;

import java.util.ArrayList;
/**
 * Implements the {@link EffectStrategy} to represent a specific building effect.
 * When applied, this effect grants the player additional prestige points equal to the
 * prestige points already provided by the {@link Builder} cards present in their tribe,
 * effectively doubling their value.
 */
public class DoubleBuilderPrestige implements EffectStrategy {
    /**
     * Applies the double builder prestige effect to the specified player.
     * Iterates through the player's tribe to find all builder cards and adds their
     * prestige points to the player's total score.
     *
     * @param player the player who benefits from the effect
     * @param game   the current game instance
     */
    @Override
    public void applyEffect(Player player, Game game) {
        ArrayList<Builder> builders = new ArrayList<>(player.getTribe().getBuilders());
        for (Builder builder : builders) {
            player.addToPrestigePoints(builder.getPrestigePoints());
        }
    }
}
