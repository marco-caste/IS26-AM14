package it.polimi.ingsw.am14.model.gameLogic;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
     *     creates a Player instance given a nickname and a totemColor,
     *     while every other attribute is set to default
     * </p>
     * @param nickname the nickname of the player, must not be null
     * @param totemColor the totem color of the player, must not be null
     *
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

    /**
     * <p>
     *     creates a Player instance given all the attributes. Used in case of a game state reconstruction from a JSON file.
     * </p>
     * @param nickname the nickname of the player, must not be null
     * @param totemColor the totem color of the player, must not be null
     * @param tribe the tribe of the player, must not be null
     * @param numBottomPicksLeft number of bottom picks left for this player, must be >= 0
     * @param numTopPicksLeft number of top picks left for this player, must be >= 0
     * @param numSetCompleted number of sets completed by this player, must be >= 0
     * @param addFoodStartOfRound amount of food added at the start of each round for this player, must be >= 0
     * @param addTopPick amount of top picks added for this player, must be >= 0
     * @param totalGatheringDiscount total gathering discount for this player, must be >= 0
     * @param totalBuildingDiscount total building discount for this player, must be >= 0
     * @param numStars number of stars for this player, must be >= 0
     * @param numPrestigePoints number of prestige points for this player, must be >= 0
     * @param numFood number of food for this player, must be >= 0
     */
    @JsonCreator
    public Player(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("totemColor") TotemColor totemColor,
            @JsonProperty("tribe") Tribe tribe,
            @JsonProperty("numBottomPicksLeft") int numBottomPicksLeft,
            @JsonProperty("numTopPicksLeft") int numTopPicksLeft,
            @JsonProperty("numSetCompleted") int numSetCompleted,
            @JsonProperty("addFoodStartOfRound") int addFoodStartOfRound,
            @JsonProperty("addTopPick") int addTopPick,
            @JsonProperty("totalGatheringDiscount") int totalGatheringDiscount,
            @JsonProperty("totalBuildingDiscount") int totalBuildingDiscount,
            @JsonProperty("numStars") int numStars,
            @JsonProperty("numPrestigePoints") int numPrestigePoints,
            @JsonProperty("numFood") int numFood) {
        this.nickname = nickname;
        this.totemColor = totemColor;
        this.tribe = tribe;
        this.numBottomPicksLeft = numBottomPicksLeft;
        this.numTopPicksLeft = numTopPicksLeft;
        this.numSetCompleted = numSetCompleted;
        this.addFoodStartOfRound = addFoodStartOfRound;
        this.addTopPick = addTopPick;
        this.totalGatheringDiscount = totalGatheringDiscount;
        this.totalBuildingDiscount = totalBuildingDiscount;
        this.numStars = numStars;
        this.numPrestigePoints = numPrestigePoints;
        this.numFood = numFood;
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
     *     adds variation to the food of this player and ensures it remains >= 0
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
     *     adds variation to the number of prestige points of this player
     * </p>
     * @param variation any integer
     */
    public void addToPrestigePoints(int variation) {
        this.numPrestigePoints += variation;
    }

    /**
     * <p>
     *     adds variation to the total number of stars of this player
     * </p>
     * @param variation >= 0
     */
    public void addToStars(int variation) {
        if (variation < 0) throw new IllegalArgumentException("variation must be >= 0");
        this.numStars += variation;
    }
    /**
     * <p>
     *     adds variation to the total building discount of this player
     * </p>
     * @param variation >= 0
     */
    public void addToBuildingDiscount(int variation) {
        this.totalBuildingDiscount += variation;
    }
    /**
     * <p>
     *     adds variation to the total gathering discount of this player
     * </p>
     * @param variation >= 0
     */
    public void addToGatheringDiscount(int variation) {
        this.totalGatheringDiscount += variation;
    }

    /**
     * <p>
     *     adds value to the number of sets completed by this player
     * </p>
     * @param value >= 1
     */
    public void addToSetCompleted(int value) {
        this.numSetCompleted += value;
    }

    /**
     * <p>
     *     removes one pick from the number of top picks left for this player
     * </p>
     */
    public void removeSingleTopPick() {
        this.numTopPicksLeft--;
    }

    /**
     * <p>
     *     removes one pick from the number of bottom picks left for this player
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
