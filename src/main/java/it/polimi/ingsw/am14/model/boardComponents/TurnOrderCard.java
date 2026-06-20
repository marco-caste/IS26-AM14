package it.polimi.ingsw.am14.model.boardComponents;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.gameLogic.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.am14.utils.Constants.foodTable;
import static it.polimi.ingsw.am14.utils.Constants.initialFood;

/**
 * <p>
 *     represents the turn order card, which represents the order of players in the game and the bonuses/maluses they receive at the end of each round
 * </p>
 */
public class TurnOrderCard {
    /**
     * <p>
     *     the number of players in the game, between 2 and 5
     * </p>
     */
    private final int numPlayers;
    /**
     * <p>
     *     the order of players in the game, represented as an ArrayList of Player objects
     * </p>
     */
    private final ArrayList<Player> playerOrder;


    /**
     * <p>
     *     builds a TurnOrderCard object with the given number of players and an empty player order
     * </p>
     * @param numPlayers between 2 and 5
     */
    public TurnOrderCard(int numPlayers) {
        this.numPlayers = numPlayers;
        this.playerOrder = new ArrayList<>();
    }

    /**
     * <p>
     *     constructor used by the Codec to deserialize the TurnOrderCard object from data file
     * </p>
     * @param numPlayers the number of players in the game
     * @param playerOrder an ordered list of players in the game
     */
    @JsonCreator
    public TurnOrderCard(
            @JsonProperty("numPlayers") int numPlayers,
            @JsonProperty("playerOrder") ArrayList<Player> playerOrder) {
        this.numPlayers = numPlayers;
        this.playerOrder = playerOrder;
    }

    /**
     * <p>
     *     resolves the bonus/malus for each player at the end of the round, according to the rules of the game and to which position they are in the player order. The last player in the order must pay either food or prestige points, if they do not have any food left. The other players receive a bonus according to their position in the order.
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
    public void clearPlayerOrder() {
        this.playerOrder.clear();
    }

    // ADDS PLAYER
    public void addPlayer(Player p) {
        this.playerOrder.add(p);
    }

    // shuffles the playerOrder and sets the initial food

    /**
     * <p>
     *     carries out phase 10 of the setup operations, as in the rule book:
     *     <li>shuffle the player order</li>
     *     <li>set the initial food for each player according to the number of players</li>
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
     *     returns the player at position index in the player order.
     * </p>
     * @param index the index of the player to remove, must be >= 0
     * @return the player at position index in the player order
     * @throws NullPointerException if the player at position index has already been removed from the player order
     */
    public Player getPlayer(int index) throws NullPointerException{
        if (this.playerOrder.get(index) == null)
            throw new NullPointerException("Player at index " + index + " has already been removed from the TurnOrderCard");
        return this.playerOrder.get(index);
    }

    public ArrayList<Player> getPlayerOrder() {
        return new ArrayList<>(playerOrder);
    }

    /**
     * <p>
     *     removes the player at position {@code playerIndex} from the player order by setting their reference to null.
     * </p>
     * @param playerIndex the index of the player to remove, must be >= 0
     */
    public void removePlayer(int playerIndex) {
        this.playerOrder.set(playerIndex, null);
    }

    /**
     * <p>
     *     sets the reference of the player at position {@code playerIndex} in the player order to the given {@code player} object.
     * </p>
     * @param playerIndex the index of the player to set, must be >= 0
     * @param player the player object to set at position {@code playerIndex}
     */
    public void setPlayerReference(int playerIndex, Player player) {
        this.playerOrder.set(playerIndex, player);
    }
}
