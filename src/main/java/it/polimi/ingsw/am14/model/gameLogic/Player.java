package it.polimi.ingsw.am14.model.gameLogic;

import java.util.Objects;

public class Player {
    private final String nickname;
    private int numFood;
    private final TotemColor totemColor;
    private int numPrestigePoints;
    private int numStars;
    private int totalBuildingDiscount;
    private int totalGatheringDiscount;
    private int addTopPick;
    private int addFoodStartOfRound;
    private int numSetCompleted;
    private int numTopPicksLeft;
    private int numBottomPicksLeft;
    private final Tribe tribe;

    /**
     * <p>
     *     creates a Player instance with this.nickname = nickname and this.totemColor = totemColor,
     *     while every other attribute is set to default
     * </p>
     * @param nickname not null
     * @param totemColor not null
     *
     * @result call to Tribe constructor, returns a Player instance
     */
    public Player(String nickname, TotemColor totemColor) {
        this.nickname = nickname;
        this.numFood = 0;
        this.totemColor = totemColor;
        this.numPrestigePoints = 0;
        this.numStars = 0;
        this.totalBuildingDiscount = 0;
        this.totalGatheringDiscount = 0;
        this.addTopPick = 0;
        this.addFoodStartOfRound = 0;
        this.numSetCompleted = 0;
        this.numTopPicksLeft = 0;
        this.numBottomPicksLeft = 0;
        this.tribe = new Tribe();
    }

    // GETTERS:
    public String getNickname() {
        return nickname;
    }
    public int getNumFood() {
        return numFood;
    }
    public TotemColor getTotemColor() {
        return totemColor;
    }
    public int getNumPrestigePoints() {
        return numPrestigePoints;
    }
    public int getNumStars() {
        return numStars;
    }
    public int getTotalBuildingDiscount() {
        return totalBuildingDiscount;
    }
    public int getTotalGatheringDiscount() {
        return totalGatheringDiscount;
    }
    public int getAddTopPick() {
        return addTopPick;
    }
    public int getAddFoodStartOfRound() {
        return addFoodStartOfRound;
    }
    public int getNumSetCompleted() {
        return numSetCompleted;
    }
    public int getNumTopPicksLeft() {
        return numTopPicksLeft;
    }
    public int getNumBottomPicksLeft() {
        return numBottomPicksLeft;
    }
    public Tribe getTribe() {
        return tribe;
    }
    public int [] getAllIds() {
        return tribe.getAllIds();
    }

    // SETTERS:
    public void setNumFood(int numFood) {
        this.numFood = numFood;
    }
    public void setAddTopPick(int addTopPick) {
        this.addTopPick = addTopPick;
    }
    public void setAddFoodStartOfRound(int addFoodStartOfRound) {
        this.addFoodStartOfRound = addFoodStartOfRound;
    }
    public void setNumTopPicksLeft(int numTopPicksLeft) {
        this.numTopPicksLeft = numTopPicksLeft;
    }
    public void setNumBottomPicksLeft(int numBottomPicksLeft) {
        this.numBottomPicksLeft = numBottomPicksLeft;
    }

    // ADDERS

    /**
     * <p>
     *     adds variation to this.numFood and ensures that this.numFood >= 0
     * </p>
     * @param variation any integer
     */
    public void addToNumFood(int variation) {
        this.numFood += variation;
        if (this.numFood < 0)
            this.numFood = 0;
    }
    /**
     * <p>
     *     adds variation to this.numPrestigePoints
     * </p>
     * @param variation any integer
     */
    public void addToPrestigePoints(int variation) {
        this.numPrestigePoints += variation;
    }

    /**
     * <p>
     *     adds variation to this.numStars
     * </p>
     * @param variation >= 0
     */
    public void addToStars(int variation) {
        if (variation < 0) throw new IllegalArgumentException("variation must be >= 0");
        this.numStars += variation;
    }
    /**
     * <p>
     *     adds variation to this.totalBuildingDiscount
     * </p>
     * @param variation >= 0
     */
    public void addToBuildingDiscount(int variation) {
        this.totalBuildingDiscount += variation;
    }
    /**
     * <p>
     *     adds variation to this.totalGatheringDiscount
     * </p>
     * @param variation >= 0
     */
    public void addToGatheringDiscount(int variation) {
        this.totalGatheringDiscount += variation;
    }

    /**
     * <p>
     *     adds value to this.numSetCompleted
     * </p>
     * @param value >= 1
     */
    public void addToSetCompleted(int value) {
        this.numSetCompleted += value;
    }

    /**
     * <p>
     *     removes one pick from this.numTopPicksLeft
     * </p>
     */
    public void removeSingleTopPick() {
        this.numTopPicksLeft--;
    }

    /**
     * <p>
     *     removes one pick from this.numBottomPicksLeft
     * </p>
     */
    public void removeSingleBottomPick() {
        this.numBottomPicksLeft--;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return getNumFood() == player.getNumFood() &&
                getNumPrestigePoints() == player.getNumPrestigePoints() &&
                getNumStars() == player.getNumStars() &&
                getTotalBuildingDiscount() == player.getTotalBuildingDiscount() &&
                getTotalGatheringDiscount() == player.getTotalGatheringDiscount() &&
                getAddTopPick() == player.getAddTopPick() &&
                getAddFoodStartOfRound() == player.getAddFoodStartOfRound() &&
                getNumSetCompleted() == player.getNumSetCompleted() &&
                getNumTopPicksLeft() == player.getNumTopPicksLeft() &&
                getNumBottomPicksLeft() == player.getNumBottomPicksLeft() &&
                Objects.equals(getNickname(), player.getNickname()) &&
                getTotemColor() == player.getTotemColor() &&
                Objects.equals(getTribe(), player.getTribe());
    }
}
