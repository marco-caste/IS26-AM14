package it.polimi.ingsw.am14.model.boardComponents;

import it.polimi.ingsw.am14.model.gameLogic.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TurnOrderCard {
    private final int numPlayers;
    private final ArrayList<Player> playerOrder;
    private static final Map<Integer, List<Integer>> foodTable = Map.of(
            2, List.of(1, -1),
            3, List.of(2, 0, -1),
            4, List.of(2, 1, 0, -1),
            5, List.of(3, 1, 0, 0, -1)
    );
    private static final int[] initialFood = {2, 3, 3, 4, 4};

    /**
     * <p>
     *     builds a TurnOrderCard object
     * </p>
     * @param numPlayers between 2 and 5
     */
    public TurnOrderCard(int numPlayers) {
        this.numPlayers = numPlayers;
        this.playerOrder = new ArrayList<>();
    }

    /**
     * <p>
     *     applies the effects of the cases to the corresponding player:
     *     - visits each case except the last and adds the corresponding bonus to player
     *     - removes food or prestigePoints to the last player
     * </p>
     */
    public void resolveBonusOrMalus(){
        List<Integer> BonusPLayer = foodTable.get(numPlayers);
        Player lastPlayer = playerOrder.get(numPlayers - 1);

        for (int i=0 ; i < numPlayers - 1 ; i++) {
            Player player = playerOrder.get(i);
            if (BonusPLayer.get(i)>0) {
                player.addToNumFood(BonusPLayer.get(i) + player.getAddFoodStartOfRound());
            }
        }
        if(lastPlayer.getNumFood() == 0)
            lastPlayer.addToPrestigePoints(-2);
        else
            lastPlayer.addToNumFood(BonusPLayer.get(numPlayers - 1));
    }

    // CLEAR PLAYERS

    /**
     * <p>
     *     calls this.playerOrder.clear()
     * </p>
     */
    public void clearPlayerOrder() {
        this.playerOrder.clear();
    }

    // ADDS PLAYER

    /**
     * <p>
     *     adds p to this.playerOrder
     * </p>
     * @param p not null
     */
    public void addPlayer(Player p) {
        this.playerOrder.add(p);
    }

    // shuffles the playerOrder and sets the initial food

    /**
     * <p>
     *     carries out phase 10 of the setup operations, as in the rule book:
     *     - shuffles the playerOrder
     *     - sets the initial players' food
     * </p>
     */
    public void setupPhaseTen() {
        Collections.shuffle(this.playerOrder);

        for (int i = 0; i < numPlayers; i++)
            playerOrder.get(i).setNumFood(initialFood[i]);
    }
    // GETS NUMBER OF PLAYERS (NECESSARY AT CREATION TIME)
    public int getNumPlayers() {
        return numPlayers;
    }

    /**
     * <p>
     *     returns the player at position index in this.PlayerOrder
     * </p>
     * @param index >= 0
     * @return this.playerOrder.get(index)
     */
    public Player getPlayer(int index) {
        return this.playerOrder.get(index);
    }
}
