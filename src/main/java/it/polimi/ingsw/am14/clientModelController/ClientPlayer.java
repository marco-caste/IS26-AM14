package it.polimi.ingsw.am14.clientModelController;

import java.util.ArrayList;
import java.util.List;
/**
 * A localized model representation tracking a specific participant's identity, assets, and modifiers.
 * <p>
 * This class serves as a lightweight, synchronized mirror of a player's server-side attributes within
 * the client-side architecture. It manages credentials (nickname, cosmetic configuration tokens), volatile
 * resource balances (food reserves, absolute score counters), and persistent passive effects unlocked
 * via building interactions (such as developmental star pools or cost-reduction multipliers).
 * These metrics are read directly by rendering view screens to update user-specific UI dashboards.
 * </p>
 */
public class ClientPlayer {
    private String nickname;
    private String totemColor;
    private int numFood;
    private int numPrestigePoints;
    private int numStars;
    private int totalBuildingDiscount;
    private int totalGatheringDiscount;
    private List<Integer> playerTribe = new ArrayList<>();
    /**
     * Constructs a baseline {@code ClientPlayer} instance with an unassigned status configuration,
     * bound to a unique username token.
     *
     * @param nickname the descriptive text username selected by the participant
     */
    public ClientPlayer(String nickname) {
        this.nickname = nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setTotemColor(String totemColor) {
        this.totemColor = totemColor;
    }

    public void setNumFood(int numFood) {
        this.numFood = numFood;
    }

    public void setNumPrestigePoints(int numPrestigePoints) {
        this.numPrestigePoints = numPrestigePoints;
    }

    public void addToPlayerTribe(Integer tribeElement) {
        this.playerTribe.add(tribeElement);
    }

    public String getNickname() {
        return nickname;
    }

    public String getTotemColor() {
        return totemColor.toLowerCase();
    }

    public int getNumFood() {
        return numFood;
    }

    public int getNumPrestigePoints() {
        return numPrestigePoints;
    }

    public List<Integer> getPlayerTribe() {
        return playerTribe;
    }

    public int getNumStars() {return numStars;}

    public int getTotalBuildingDiscount() {return totalBuildingDiscount;}

    public int getTotalGatheringDiscount() {return totalGatheringDiscount;}
    /**
     * Generates an aligned text representation highlighting core tracking attributes for console diagnostics.
     *
     * @return formatted diagnostic text string summarizing primary stats
     */
    @Override
    public String toString() {
        return  "nickname='" + nickname + "'\n" +
                "totemColor='" + totemColor.toLowerCase() + "'\n" +
                "numFood=" + numFood + "\n" +
                "numPrestigePoints=" + numPrestigePoints;
    }

    public void setNumStars(int numStars) {
        this.numStars = numStars;
    }

    public void setTotalBuildingDiscount(int buildingDiscount) {
        this.totalBuildingDiscount = buildingDiscount;

    }

    public void setTotalGatheringDiscount(int gatheringDiscount) {
        this.totalGatheringDiscount = gatheringDiscount;
    }
}
