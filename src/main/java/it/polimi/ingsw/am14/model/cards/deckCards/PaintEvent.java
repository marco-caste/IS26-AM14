package it.polimi.ingsw.am14.model.cards.deckCards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.buildings.visitors.*;
import it.polimi.ingsw.am14.model.gameLogic.*;

import java.util.ArrayList;

public class PaintEvent extends EventCard {

    @JsonCreator
    public PaintEvent(
            @JsonProperty("age") int age,
            @JsonProperty("id") int id
    ) {
        super(age, id);
    }

    private void checkBuilding(Player player, Game context) {
        PaintEventVisitor visitor = new PaintEventVisitor(context, player);
        ArrayList<Building> buildings = player.getTribe().getBuildings();
        for (Building building : buildings) {
            building.accept(visitor);
        }
    }

    @Override
    public void applyEffect(Player player, Game context) {

        int lossRange = this.getAge() - 1 ; // it's a formula which gives us the range of artists that cause a loss of prestige points
        int numArtists;
        int prestigeLoss;  // gives the number of prestige loss if the player has a number of artists <= lossRange
        int prestigeWin;    // gives the number of prestige win if the player has a number of artists > lossRange

        ArrayList<Player> players = new ArrayList<>(context.getPlayers());

        for (Player p : players) {

            numArtists = p.getTribe().getArtistsSize();
            prestigeWin = numArtists * this.getAge() ;
            prestigeLoss = numArtists * 2;

            if (numArtists <= lossRange)
                p.addToPrestigePoints(-prestigeLoss);
            else
                p.addToPrestigePoints(prestigeWin);

            checkBuilding(player, context);

        }
        removeEvent(context);
    }


}
