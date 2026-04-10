package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.gameLogic.*;
import it.polimi.ingsw.am14.model.cards.deckCards.*;

import java.util.ArrayList;

public class DoubleBuilderPrestige implements EffectStrategy {
    @Override
    public void applyEffect(Player player, Game game) {
        ArrayList<Builder> builders = new ArrayList<>(player.getTribe().getBuilders());
        for (Builder builder : builders) {
            player.addToPrestigePoints(builder.getPrestigePoints());
        }
    }
}
