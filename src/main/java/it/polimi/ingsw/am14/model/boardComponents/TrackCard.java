package it.polimi.ingsw.am14.model.boardComponents;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.gameLogic.Player;

/**
 * <p>
 *     TrackCard is a class that represents a card on the track of the game.
 *     It has a name, a number of picks for the top and bottom rows, and a number of food.
 *     It also has a playerTotem that represents the player who has placed their totem on this card.
 * </p>
 */
public class TrackCard {
    /**
     * <p>
     *     playerTotem is the player who has placed their totem on this card.
     *     It can be null if no player has placed their totem on this card.
     * </p>
     */
    private Player playerTotem;
    /**
     * <p>
     *     name is the name of the card, between 'a' and 'g'.
     * </p>
     */
    private final char name;
    /**
     * <p>
     *     numPicksTop is the number of picks for the top row, between 0 and 2.
     * </p>
     */
    private final int numPicksTop;
    /**
     * <p>
     *     numPicksBottom is the number of picks for the bottom row, between 0 and 1.
     * </p>
     */
    private final int numPicksBottom;
    /**
     * <p>
     *     numFood is the number of food, either 0 or 3.
     * </p>
     */
    private final int numFood;

    /**
     * <p>
     *     Accepts specific combinations of the input parameters and sets the corresponding fields;
     *     `playerTotem` can be null.
     * </p>
     * <ul>
     *     <li>('a', 0, 0, 3)</li>
     *     <li>('b', 0, 1, 0)</li>
     *     <li>('c', 1, 0, 0)</li>
     *     <li>('d', 0, 2, 0)</li>
     *     <li>('e', 1, 1, 0)</li>
     *     <li>('f', 2, 0, 0)</li>
     *     <li>('g', 2, 1, 0)</li>
     * </ul>
     *
     * @param name the name of the trackCard, must be between 'a' and 'g'
     * @param numPicksTop the number of picks for the top row, between 0 and 2
     * @param numPicksBottom the number of picks for the bottom row, between 0 and 1
     * @param numFood the number of food on the card, either 0 or 3
     * @param playerTotem the player who placed a totem on this card; may be null
     */
    @JsonCreator
    public TrackCard(
            @JsonProperty("name") char name,
            @JsonProperty("numPickTop") int numPicksTop,
            @JsonProperty("numPickBottom") int numPicksBottom,
            @JsonProperty("numFood") int numFood,
            @JsonProperty("playerTotem") Player playerTotem
            ) {
        this.playerTotem = playerTotem;
        this.name = name;
        this.numPicksTop = numPicksTop;
        this.numPicksBottom = numPicksBottom;
        this.numFood = numFood;
    }

    public void setPlayerTotem(Player playerTotem) {
        this.playerTotem = playerTotem;
    }

    public Player getPlayerTotem() {
        return playerTotem;
    }

    @JsonIgnore
    public char getName() {
        return name;
    }

    @JsonIgnore
    public String getPlayerNickname() {
        return playerTotem == null ? null : playerTotem.getNickname();
    }

    /**
     * <p>
     *     applies the effect of the card to the player who placed their totem on it.
     *     It sets the number of picks for the top and bottom rows, and adds the number of food to the player's total.
     * </p>
     */
    public void effect(){
        playerTotem.setNumTopPicksLeft(numPicksTop);
        playerTotem.setNumBottomPicksLeft(numPicksBottom);
        playerTotem.addToNumFood(numFood);
    }

    @Override
    public String toString() {
        return "TrackCard " +
                ", name=" + name +
                ", numPicksTop=" + numPicksTop +
                ", numPicksBottom=" + numPicksBottom +
                ", numFood=" + numFood;
    }
}
