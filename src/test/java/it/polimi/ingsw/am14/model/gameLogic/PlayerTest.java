package it.polimi.ingsw.am14.model.gameLogic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    private Player player;
    private TotemColor totemColor;
    private final String nickname = "TestPlayer";

    @BeforeEach
    public void setUp() {
        totemColor = TotemColor.WHITE;
        player = new Player(nickname, totemColor);
    }

    // CONSTRUCTOR TESTS

    @Test
    public void testPlayerConstructor() {
        // Verify player is created with correct initial values for all attributes
        assertNotNull(player);
        assertEquals(nickname, player.getNickname());
        assertEquals(totemColor, player.getTotemColor());
        assertNotNull(player.getTribe());
    }

    @Test
    public void testPlayerInitialNumFood() {
        // Test that initial food is 0
        assertEquals(0, player.getNumFood());
    }

    @Test
    public void testPlayerInitialPrestigePoints() {
        // Test that initial prestige points is 0
        assertEquals(0, player.getNumPrestigePoints());
    }

    @Test
    public void testPlayerInitialStars() {
        // Test that initial stars is 0
        assertEquals(0, player.getNumStars());
    }

    @Test
    public void testPlayerInitialBuildingDiscount() {
        // Test that initial building discount is 0
        assertEquals(0, player.getTotalBuildingDiscount());
    }

    @Test
    public void testPlayerInitialGatheringDiscount() {
        // Test that initial gathering discount is 0
        assertEquals(0, player.getTotalGatheringDiscount());
    }

    @Test
    public void testPlayerInitialAddTopPick() {
        // Test that initial add top pick is 0
        assertEquals(0, player.getAddTopPick());
    }

    @Test
    public void testPlayerInitialAddFoodStartOfRound() {
        // Test that initial add food start of round is 0
        assertEquals(0, player.getAddFoodStartOfRound());
    }

    @Test
    public void testPlayerInitialNumSetCompleted() {
        // Test that initial set completed is 0
        assertEquals(0, player.getNumSetCompleted());
    }

    @Test
    public void testPlayerInitialNumTopPicksLeft() {
        // Test that initial top picks left is 0
        assertEquals(0, player.getNumTopPicksLeft());
    }

    @Test
    public void testPlayerInitialNumBottomPicksLeft() {
        // Test that initial bottom picks left is 0
        assertEquals(0, player.getNumBottomPicksLeft());
    }

    @Test
    public void testPlayerTotemColorInitialization() {
        // Test different totem colors
        Player playerRed = new Player("RedPlayer", TotemColor.RED);
        assertEquals(TotemColor.RED, playerRed.getTotemColor());

        Player playerBlue = new Player("BluePlayer", TotemColor.BLUE);
        assertEquals(TotemColor.BLUE, playerBlue.getTotemColor());
    }

    // GETTERS TESTS

    @Test
    public void testGetNickname() {
        assertEquals(nickname, player.getNickname());
    }

    @Test
    public void testGetTotemColor() {
        assertEquals(totemColor, player.getTotemColor());
    }

    @Test
    public void testGetNumFood() {
        assertEquals(0, player.getNumFood());
    }

    @Test
    public void testGetNumPrestigePoints() {
        assertEquals(0, player.getNumPrestigePoints());
    }

    @Test
    public void testGetNumStars() {
        assertEquals(0, player.getNumStars());
    }

    // SETTERS TESTS

    @Test
    public void testSetNumFood() {
        player.setNumFood(10);
        assertEquals(10, player.getNumFood());
    }

    @Test
    public void testSetNumFoodToZero() {
        player.setNumFood(5);
        player.setNumFood(0);
        assertEquals(0, player.getNumFood());
    }

    @Test
    public void testSetNumFoodToNegative() {
        player.setNumFood(-5);
        assertEquals(-5, player.getNumFood());
    }

    @Test
    public void testSetNumFoodLargeValue() {
        player.setNumFood(100);
        assertEquals(100, player.getNumFood());
    }

    @Test
    public void testSetAddTopPick() {
        player.setAddTopPick(5);
        assertEquals(5, player.getAddTopPick());
    }

    @Test
    public void testSetAddFoodStartOfRound() {
        player.setAddFoodStartOfRound(3);
        assertEquals(3, player.getAddFoodStartOfRound());
    }

    @Test
    public void testSetNumTopPicksLeft() {
        player.setNumTopPicksLeft(7);
        assertEquals(7, player.getNumTopPicksLeft());
    }

    @Test
    public void testSetNumBottomPicksLeft() {
        player.setNumBottomPicksLeft(6);
        assertEquals(6, player.getNumBottomPicksLeft());
    }

    @Test
    public void testSettersOverwrite() {
        player.setNumFood(10);
        player.setNumFood(20);
        assertEquals(20, player.getNumFood());
    }

    // ADDERS TESTS

    @Test
    public void testAddToNumFoodPositive() {
        player.addToNumFood(5);
        assertEquals(5, player.getNumFood());
    }

    @Test
    public void testAddToNumFoodMultipleTimes() {
        player.addToNumFood(3);
        player.addToNumFood(4);
        assertEquals(7, player.getNumFood());
    }

    @Test
    public void testAddToNumFoodNegative() {
        player.setNumFood(10);
        player.addToNumFood(-3);
        assertEquals(7, player.getNumFood());
    }

    @Test
    public void testAddToNumFoodResultingInNegative() {
        player.setNumFood(5);
        player.addToNumFood(-10);
        assertEquals(0, player.getNumFood());
    }

    @Test
    public void testAddToNumFoodZeroVariation() {
        player.setNumFood(5);
        player.addToNumFood(0);
        assertEquals(5, player.getNumFood());
    }

    @Test
    public void testAddToPrestigePoints() {
        player.addToPrestigePoints(10);
        assertEquals(10, player.getNumPrestigePoints());
    }

    @Test
    public void testAddToPrestigePointsMultipleTimes() {
        player.addToPrestigePoints(5);
        player.addToPrestigePoints(3);
        assertEquals(8, player.getNumPrestigePoints());
    }

    @Test
    public void testAddToPrestigePointsNegative() {
        player.addToPrestigePoints(10);
        player.addToPrestigePoints(-3);
        assertEquals(7, player.getNumPrestigePoints());
    }

    @Test
    public void testAddToStars() {
        player.addToStars(5);
        assertEquals(5, player.getNumStars());
    }

    @Test
    public void testAddToStarsMultipleTimes() {
        player.addToStars(2);
        player.addToStars(3);
        assertEquals(5, player.getNumStars());
    }

    @Test
    public void testAddToBuildingDiscount() {
        player.addToBuildingDiscount(3);
        assertEquals(3, player.getTotalBuildingDiscount());
    }

    @Test
    public void testAddToBuildingDiscountMultipleTimes() {
        player.addToBuildingDiscount(2);
        player.addToBuildingDiscount(3);
        assertEquals(5, player.getTotalBuildingDiscount());
    }

    @Test
    public void testAddToGatheringDiscount() {
        player.addToGatheringDiscount(2);
        assertEquals(2, player.getTotalGatheringDiscount());
    }

    @Test
    public void testAddToGatheringDiscountMultipleTimes() {
        player.addToGatheringDiscount(1);
        player.addToGatheringDiscount(2);
        assertEquals(3, player.getTotalGatheringDiscount());
    }

    @Test
    public void testAddToSetCompleted() {
        player.addToSetCompleted(1);
        assertEquals(1, player.getNumSetCompleted());
    }

    @Test
    public void testAddToSetCompletedMultipleTimes() {
        player.addToSetCompleted(1);
        player.addToSetCompleted(1);
        player.addToSetCompleted(2);
        assertEquals(4, player.getNumSetCompleted());
    }

    @Test
    public void testRemoveSingleTopPick() {
        player.setNumTopPicksLeft(5);
        player.removeSingleTopPick();
        assertEquals(4, player.getNumTopPicksLeft());
    }

    @Test
    public void testRemoveSingleTopPickMultipleTimes() {
        player.setNumTopPicksLeft(5);
        player.removeSingleTopPick();
        player.removeSingleTopPick();
        assertEquals(3, player.getNumTopPicksLeft());
    }

    // TODO: numXPicksLeft non può essere sotto 0
    @Test
    public void testRemoveSingleTopPickToNegative() {
        player.setNumTopPicksLeft(0);
        player.removeSingleTopPick();
        assertEquals(-1, player.getNumTopPicksLeft());
    }

    @Test
    public void testRemoveSingleBottomPick() {
        player.setNumBottomPicksLeft(5);
        player.removeSingleBottomPick();
        assertEquals(4, player.getNumBottomPicksLeft());
    }

    @Test
    public void testRemoveSingleBottomPickMultipleTimes() {
        player.setNumBottomPicksLeft(5);
        player.removeSingleBottomPick();
        player.removeSingleBottomPick();
        assertEquals(3, player.getNumBottomPicksLeft());
    }

    // TODO: numXPicksLeft non può essere sotto 0
    @Test
    public void testRemoveSingleBottomPickToNegative() {
        player.setNumBottomPicksLeft(0);
        player.removeSingleBottomPick();
        assertEquals(-1, player.getNumBottomPicksLeft());
    }

    // COMBINATION TESTS

    @Test
    public void testMultipleOperationsSequence() {
        // Test a sequence of operations
        player.setNumFood(10);
        player.addToNumFood(5);
        assertEquals(15, player.getNumFood());

        player.addToPrestigePoints(20);
        player.addToPrestigePoints(10);
        assertEquals(30, player.getNumPrestigePoints());

        player.setNumTopPicksLeft(3);
        player.removeSingleTopPick();
        player.removeSingleTopPick();
        assertEquals(1, player.getNumTopPicksLeft());
    }

    @Test
    public void testIndependentAttributes() {
        // Verify that modifying one attribute doesn't affect others
        player.setNumFood(10);
        player.addToPrestigePoints(5);
        player.addToStars(3);

        assertEquals(10, player.getNumFood());
        assertEquals(5, player.getNumPrestigePoints());
        assertEquals(3, player.getNumStars());
    }

    @Test
    public void testAllAttributesAfterMultipleOperations() {
        // Verify all attributes after multiple operations
        player.setNumFood(15);
        player.addToPrestigePoints(20);
        player.addToStars(8);
        player.addToBuildingDiscount(2);
        player.addToGatheringDiscount(3);
        player.setAddTopPick(1);
        player.setAddFoodStartOfRound(2);
        player.addToSetCompleted(2);
        player.setNumTopPicksLeft(4);
        player.setNumBottomPicksLeft(5);

        // Verify all attributes are correct
        assertEquals(nickname, player.getNickname());
        assertEquals(15, player.getNumFood());
        assertEquals(totemColor, player.getTotemColor());
        assertEquals(20, player.getNumPrestigePoints());
        assertEquals(8, player.getNumStars());
        assertEquals(2, player.getTotalBuildingDiscount());
        assertEquals(3, player.getTotalGatheringDiscount());
        assertEquals(1, player.getAddTopPick());
        assertEquals(2, player.getAddFoodStartOfRound());
        assertEquals(2, player.getNumSetCompleted());
        assertEquals(4, player.getNumTopPicksLeft());
        assertEquals(5, player.getNumBottomPicksLeft());
        assertNotNull(player.getTribe());
    }

}