package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.gameLogic.*;

public class HuntExtraFoodAndPrestige implements EffectStrategy {
    @Override
    public void applyEffect(Player player, Game game) {
        player.addToNumFood(player.getTribe().getHuntersSize());
        player.addToPrestigePoints(player.getTribe().getHuntersSize());
    }
}
