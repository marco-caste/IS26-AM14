package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.gameLogic.*;

@FunctionalInterface //In order to have only 1 method
public interface EffectStrategy {
    void applyEffect(Player player, Game game);
}



