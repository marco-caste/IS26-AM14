package it.polimi.ingsw.am14.clientModelController;

import java.util.ArrayList;
import java.util.List;

public class ClientPlayer {
    private String nickname;
    private String totemColor;
    private int numFood;
    private int numPrestigePoints;
    private int numStars;
    private int totalBuildingDiscount;
    private int totalGatheringDiscount;
    private List<Integer> playerTribe = new ArrayList<>();

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
