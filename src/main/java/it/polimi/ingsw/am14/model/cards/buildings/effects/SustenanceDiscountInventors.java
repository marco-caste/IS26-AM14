package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.gameLogic.*;

public class SustenanceDiscountInventors implements EffectStrategy {
    @Override
    public void applyEffect(Player player, Game game) {
        player.addToNumFood(player.getTribe().getInventorsSize());
    }
}
