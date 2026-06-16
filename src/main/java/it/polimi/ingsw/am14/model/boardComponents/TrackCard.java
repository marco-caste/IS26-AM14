package it.polimi.ingsw.am14.model.boardComponents;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.gameLogic.Player;

// classe che ha lo scopo di memorizzare il player che ci è finito sopra e assegnare
// i pick o il cibo che deve dare al player che ci è finito sopra
public class TrackCard {
    private Player playerTotem;
    private final char name;
    private final int numPicksTop;
    private final int numPicksBottom;
    private final int numFood;

    /**
     * <p>
     *     accepts specific combinations of the input parameters:
     *     - ('a', 0, 0, 3)
     *     - ('b', 0, 1, 0)
     *     - ('c', 1, 0, 0)
     *     - ('d', 0, 2, 0)
     *     - ('e', 1, 1, 0)
     *     - ('f', 2, 0, 0)
     *     - ('g', 2, 1, 0)
     *     sets the correct values and playerTotem to null
     * </p>
     * @param name between 'a' and 'g'
     * @param numPicksTop between 0 and 2
     * @param numPicksBottom between 0 and 1
     * @param numFood = 0 or 3
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
     *     applies the effect of this TrackCard to playerTotem:
     *     - sets their numTopPicksLeft
     *     - sets their numBottomPicksLeft
     *     - adds eventual food
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
