package it.polimi.ingsw.am14.model.cards.deckCards;

import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.buildings.visitors.HuntEventVisitor;
import it.polimi.ingsw.am14.model.gameLogic.*;

import java.util.ArrayList;

public class HuntEvent extends EventCard {

    public HuntEvent(int age, int id) {
        super(age, id);
    }

    private void checkBuilding(Player player, Game context) {
        HuntEventVisitor visitor = new HuntEventVisitor(context, player);
        ArrayList<Building> buildings = player.getTribe().getBuildings();
        for (Building building : buildings) {
            building.accept(visitor);
        }
    }

    @Override
    public void applyEffect(Player player, Game context) {
        int numHunters;
        int prestigeWin;

        ArrayList<Player> players = new ArrayList<>(context.getPlayers());

        for(Player p : players){
            numHunters = p.getTribe().getHuntersSize();
            prestigeWin = numHunters * this.getAge();
            // the prestigeWin depends on the number of hunters and the age of the card

            p.addToNumFood(numHunters);
            p.addToPrestigePoints(prestigeWin);
            checkBuilding(p, context);
        }
        // we add one food and prestige Points for each hunter the player has in his tribe

        removeEvent(context);
    }
}
