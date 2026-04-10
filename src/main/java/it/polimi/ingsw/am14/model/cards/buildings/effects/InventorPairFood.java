package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.gameLogic.*;

public class InventorPairFood implements EffectStrategy {
    @Override
    public void applyEffect(Player player, Game game) {
        player.addToNumFood(3);
    }
}
