package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.gameLogic.*;

public class ShamanicPenaltyShield implements EffectStrategy {
    @Override
    public void applyEffect(Player player, Game game) {
        player.addToPrestigePoints(+ 1 + 2*game.getBoard().getLastOtherEvent().getAge());
    }
}
