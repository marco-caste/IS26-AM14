package it.polimi.ingsw.am14.model.cards.deckCards;

import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.buildings.visitors.SustenanceEventVisitor;
import it.polimi.ingsw.am14.model.gameLogic.*;
import it.polimi.ingsw.am14.model.boardComponents.Board;

import java.util.ArrayList;

public class SustenanceEvent extends EventCard {

    public SustenanceEvent(int age, int id) {
        super(age, id);
    }

    @Override
    public void applyEffect(Player playerNotUsed, Game context) {
        int totalFoodNeeded;
        int missingFood;
        int prestigeLoss;

        ArrayList<Player> players = new ArrayList<>(context.getPlayers());

        for (Player player : players) {
            //Saving the old amount in order to apply a discount and not give food
            int oldFood = player.getNumFood();
            //Adding food based on building
            checkBuilding(player, context);

            //calculating the total food needed for the player to feed his tribe
            totalFoodNeeded =
                    player.getTribe().getHuntersSize() +
                            player.getTribe().getGatherersSize() +
                            player.getTribe().getShamansSize() +
                            player.getTribe().getBuildersSize() +
                            player.getTribe().getInventorsSize() +
                            player.getTribe().getArtistsSize() -
                            player.getTotalGatheringDiscount();

            missingFood = totalFoodNeeded - player.getNumFood(); // calculating the missing food for the player
            prestigeLoss = missingFood * this.getAge(); // calculating the prestige loss for the player

            player.addToNumFood(-totalFoodNeeded);
            if (missingFood > 0) { // if the player doesn't have enough food, he will lose all his food and some prestige points
                player.addToPrestigePoints(-prestigeLoss);
            }
            else {
                //check: if you have more food then before you come back to the old amount
                if (player.getNumFood() > oldFood)
                    player.setNumFood(oldFood);
            }
        }
    }

    private void checkBuilding(Player player, Game context) {
        SustenanceEventVisitor visitor = new SustenanceEventVisitor(context, player);
        ArrayList<Building> buildings = player.getTribe().getBuildings();
        for (Building building : buildings) {
            building.accept(visitor);
        }
    }

    @Override
    public void discard(Board board, int cardIndex) {
        board.addToResolvingSustenanceEventsRow(this);
        board.removeFromBottomDeckableRow(cardIndex);
    }


}
