package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.gameLogic.*;

public class PlayerExtraDraft implements EffectStrategy {
    @Override
    public void applyEffect(Player player, Game game) {
        player.setAddTopPick(1);
    }
}
