package it.polimi.ingsw.am14.model.cards.buildings;

import it.polimi.ingsw.am14.model.cards.buildings.effects.EffectStrategy;
import it.polimi.ingsw.am14.model.cards.buildings.types.ImmediateBuilding;
import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuildingTest {

    private Building testBuilding;
    private Player player;
    private Game game;
    private final EffectStrategy noOpEffect = (player, game) -> {};
    private final EffectStrategy addFoodEffect = (p, g) -> p.addToNumFood(5);

    @BeforeEach
    void setUp() {
        // Create a concrete Building instance for testing (ImmediateBuilding)
        testBuilding = new ImmediateBuilding(10, 5, 1, 1, noOpEffect);
        player = new Player("TestPlayer", TotemColor.RED);
    }

    @Test
    void constructorInitializesAllAttributes() {
        Building building = new ImmediateBuilding(15, 7, 2, 42, noOpEffect);
        
        assertEquals(15, building.getCost());
        assertEquals(7, building.getPrestigePoints());
    }

    // ==================== ISPICKABLE TESTS ====================

    @Test
    void isPickableReturnsTrueWhenPlayerHasEnoughFood() {
        player.addToNumFood(15);
        assertTrue(testBuilding.isPickable(player));
    }

    @Test
    void isPickableReturnsTrueWhenPlayerHasExactAmount() {
        player.addToNumFood(10);  // Building costs 10
        assertTrue(testBuilding.isPickable(player));
    }

    @Test
    void isPickableReturnsFalseWhenPlayerHasInsufficientFood() {
        player.addToNumFood(5);   // Building costs 10
        assertFalse(testBuilding.isPickable(player));
    }

    @Test
    void isPickableWithBuildingDiscount() {
        // Building costs 10, but player has discount of 5
        player.addToNumFood(6);           // 6 + 5 discount = 11 >= 10
        player.addToBuildingDiscount(5);
        assertTrue(testBuilding.isPickable(player));
    }

    @Test
    void isPickableWithBuildingDiscountButStillInsufficientFood() {
        // Building costs 10
        player.addToNumFood(2);           // 2 + 5 discount = 7 < 10
        player.addToBuildingDiscount(5);
        assertFalse(testBuilding.isPickable(player));
    }

    @Test
    void buyWithNoDiscount() {
        player.addToNumFood(15);
        testBuilding.buy(player);
        assertEquals(5, player.getNumFood());   // 15 - 10 cost = 5
    }

    @Test
    void buyWithBuildingDiscount() {
        player.addToNumFood(10);
        player.addToBuildingDiscount(5);
        // Payment = -10 + 5 = -5
        testBuilding.buy(player);
        assertEquals(5, player.getNumFood());   // 10 - 5 = 5
    }

    @Test
    void buyWithFullCostDiscount() {
        player.addToNumFood(10);
        player.addToBuildingDiscount(10);
        // Payment = -10 + 10 = 0
        testBuilding.buy(player);
        assertEquals(10, player.getNumFood());  // 10 - 0 = 10
    }

    @Test
    void buyWithDiscountGreaterThanCost() {
        player.addToNumFood(10);
        player.addToBuildingDiscount(15);
        // Payment = -10 + 15 = 5, but capped at 0
        testBuilding.buy(player);
        assertEquals(10, player.getNumFood());  // 10 - 0 = 10 (payment capped at 0)
    }

    @Test
    void buyMultipleBuildings() {
        player.addToNumFood(100);
        Building building1 = new ImmediateBuilding(10, 5, 1, 1, noOpEffect);
        Building building2 = new ImmediateBuilding(15, 7, 2, 2, noOpEffect);
        
        building1.buy(player);
        building2.buy(player);
        
        assertEquals(75, player.getNumFood());  // 100 - 10 - 15 = 75
        assertEquals(2, player.getTribe().getBuildings().size());
    }

    @Test
    void applyEffectExecutesEffectStrategy() {
        Building buildingWithEffect = new ImmediateBuilding(10, 5, 1, 1, addFoodEffect);
        player.setNumFood(0);
        
        buildingWithEffect.applyEffect(player, null);
        
        assertEquals(5, player.getNumFood());
    }

    @Test
    void isPickableAndBuyWorkTogether() {
        Building expensiveBuilding = new ImmediateBuilding(50, 20, 3, 1, noOpEffect);
        player.addToNumFood(45);
        
        // Should not be pickable initially
        assertFalse(expensiveBuilding.isPickable(player));
        
        // Add more food
        player.addToNumFood(10);
        
        // Now should be pickable
        assertTrue(expensiveBuilding.isPickable(player));
        
        // Buy it
        expensiveBuilding.buy(player);
        assertEquals(5, player.getNumFood());  // 55 - 50 = 5
    }

    @Test
    void buyAfterCheckingIsPickable() {
        player.addToNumFood(25);
        
        assertTrue(testBuilding.isPickable(player));
        testBuilding.buy(player);
        assertEquals(15, player.getNumFood());
        
        assertTrue(testBuilding.isPickable(player));
        testBuilding.buy(player);
        assertEquals(5, player.getNumFood());
        
        assertFalse(testBuilding.isPickable(player));
    }
}
