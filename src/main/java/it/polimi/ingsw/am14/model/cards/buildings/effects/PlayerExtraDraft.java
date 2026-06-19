package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.gameLogic.*;

public class PlayerExtraDraft implements EffectStrategy {
    @Override
    public void applyEffect(Player player, Game game) {
        System.out.println("[DEBUG PlayerExtraDraft] applyEffect ENTER player=" + player.getNickname()
                + " addTopPickBefore=" + player.getAddTopPick()
                + " topLeft=" + player.getNumTopPicksLeft()
                + " bottomLeft=" + player.getNumBottomPicksLeft()
                + " food=" + player.getNumFood()
                + " gameId=" + game.getGameId());
        player.setAddTopPick(1);
        System.out.println("[DEBUG PlayerExtraDraft] applyEffect EXIT player=" + player.getNickname()
                + " addTopPickAfter=" + player.getAddTopPick());
    }
}
