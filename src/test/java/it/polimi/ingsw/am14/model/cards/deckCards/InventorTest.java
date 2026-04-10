package it.polimi.ingsw.am14.model.cards.deckCards;

import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.buildings.effects.InventorPairFood;
import it.polimi.ingsw.am14.model.cards.buildings.types.InventorsBuilding;
import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventorTest {

    private Inventor inventor;
    private Player player;
    private Game game;
    private Building inventorsBuilding;

    @BeforeEach
    void setUp() {
        inventor = new Inventor(2, 50, InventionType.LEATHER);
        player = new Player("TestPlayer", TotemColor.RED);
        game = new Game(2);
        InventorPairFood inventorBuilding = new InventorPairFood();
        inventorsBuilding = new InventorsBuilding(10, 5, 2, 2, inventorBuilding);
    }

    @Test
    void constructorInitializesAllAttributes() {
        Inventor inv = new Inventor(1, 100, InventionType.BOAT);
        assertEquals(1, inv.getAge());
        assertEquals(100, inv.getId());
        assertEquals(InventionType.BOAT, inv.getInvention());
    }

    @Test
    void isPickableAlwaysReturnsTrue() {
        // CharacterCard always returns true for isPickable
        assertTrue(inventor.isPickable(player));
    }

    @Test
    void setupPhaseFourReturnsOne() {
        Board board = new Board(2);
        int result = inventor.setupPhaseFour(board);
        assertEquals(1, result);
        assertEquals(1, board.getBottomDeckableRow().size());
    }

    @Test
    void setupPhaseFourMultipleInventors() {
        Board board = new Board(2);
        Inventor inv1 = new Inventor(1, 1, InventionType.BOAT);
        Inventor inv2 = new Inventor(2, 2, InventionType.ARROW);
        Inventor inv3 = new Inventor(3, 3, InventionType.ROPE);

        assertEquals(1, inv1.setupPhaseFour(board));
        assertEquals(1, inv2.setupPhaseFour(board));
        assertEquals(1, inv3.setupPhaseFour(board));
        assertEquals(3, board.getBottomDeckableRow().size());
    }

    @Test
    void applyEffectAddsInventorToTribe() {
        int initialSize = player.getTribe().getInventorsSize();
        inventor.applyEffect(player, game);

        assertEquals(initialSize + 1, player.getTribe().getInventorsSize());
        assertTrue(player.getTribe().getInventors().contains(inventor));
    }

    @Test
    void applyEffectWithNoMatchingInventorInTribe() {
        // No matching inventor in tribe, so checkInventorPairBuilding returns early
        // but inventor is still added to tribe
        inventor.applyEffect(player, game);
        assertEquals(1, player.getTribe().getInventorsSize());
    }

    @Test
    void applyEffectWithMatchingInventorAndBuilding() {
        // Add a matching inventor to the tribe
        Inventor leatherInventor = new Inventor(2, 1, InventionType.LEATHER);
        player.getTribe().addInventor(leatherInventor);

        // Add an InventorsBuilding
        player.getTribe().addBuilding(inventorsBuilding);

        // Apply effect with a matching inventor (checkInventorPairBuilding is called)
        Inventor secondLeatherInventor = new Inventor(2, 3, InventionType.LEATHER);
        secondLeatherInventor.applyEffect(player, game);

        // Inventor should be added to tribe
        assertEquals(2, player.getTribe().getInventorsSize());
        assertTrue(player.getTribe().getInventors().contains(secondLeatherInventor));

        // Check effect applied
        assertEquals(3, player.getNumFood());
    }

    @Test
    void applyEffectMultipleInventors() {
        Inventor inv1 = new Inventor(2, 1, InventionType.LEATHER);
        Inventor inv2 = new Inventor(2, 2, InventionType.BOAT);
        Inventor inv3 = new Inventor(2, 3, InventionType.ARROW);

        inv1.applyEffect(player, game);
        inv2.applyEffect(player, game);
        inv3.applyEffect(player, game);

        assertEquals(3, player.getTribe().getInventorsSize());
        assertTrue(player.getTribe().getInventors().contains(inv1));
        assertTrue(player.getTribe().getInventors().contains(inv2));
        assertTrue(player.getTribe().getInventors().contains(inv3));
    }

    @Test
    void applyEffectWithMultipleMatchingInventorsAndBuildings() {
        // Add multiple inventors of the same type
        Inventor inv1 = new Inventor(2, 1, InventionType.LEATHER);
        Inventor inv2 = new Inventor(2, 2, InventionType.BOAT);
        player.getTribe().addInventor(inv1);
        player.getTribe().addInventor(inv2);
        player.getTribe().addBuilding(inventorsBuilding);

        // Apply effect with a matching inventor (triggers checkInventorPairBuilding)
        Inventor inv3 = new Inventor(2, 5, InventionType.LEATHER);

        inv3.applyEffect(player, game);

        // Inventor should be added to tribe
        assertEquals(3, player.getTribe().getInventorsSize());
    }

    @Test
    void applyEffectWithoutMatchingInventorButWithBuildings() {
        // Add buildings but no matching inventor
        player.getTribe().addBuilding(inventorsBuilding);

        // Apply effect with an inventor that doesn't match any existing one
        Inventor leatherInventor = new Inventor(2, 2, InventionType.LEATHER);
        leatherInventor.applyEffect(player, game);

        // Inventor should be added anyway
        assertEquals(1, player.getTribe().getInventorsSize());
        assertTrue(player.getTribe().getInventors().contains(leatherInventor));
    }

    @Test
    void completeFlowAllInheritedMethods() {
        Board board = new Board(2);

        // Test Card methods (getAge, getId)
        assertEquals(2, inventor.getAge());
        assertEquals(50, inventor.getId());
        
        // Test CharacterCard method (isPickable)
        assertTrue(inventor.isPickable(player));

        // Test DeckCard methods (setupPhaseFour, discard)
        int result = inventor.setupPhaseFour(board);
        assertEquals(1, result);

        // Test own applyEffect (which internally calls checkInventorPairBuilding)
        inventor.applyEffect(player, game);
        assertEquals(1, player.getTribe().getInventorsSize());
    }

    @Test
    void multipleInventorsWithDifferentPlayers() {
        Player player1 = new Player("Player1", TotemColor.RED);
        Player player2 = new Player("Player2", TotemColor.BLUE);

        Inventor inv1 = new Inventor(2, 1, InventionType.LEATHER);
        Inventor inv2 = new Inventor(2, 2, InventionType.BOAT);

        inv1.applyEffect(player1, game);
        inv2.applyEffect(player2, game);

        assertEquals(1, player1.getTribe().getInventorsSize());
        assertEquals(1, player2.getTribe().getInventorsSize());
        assertEquals(InventionType.LEATHER, player1.getTribe().getInventors().getFirst().getInvention());
        assertEquals(InventionType.BOAT, player2.getTribe().getInventors().getFirst().getInvention());
    }

    @Test
    void allInventionTypesSupported() {
        for (InventionType type : InventionType.values()) {
            Inventor inv = new Inventor(2, 1, type);
            assertEquals(type, inv.getInvention());
            assertTrue(inv.isPickable(player));
            inv.applyEffect(player, game);
        }
        assertEquals(InventionType.values().length, player.getTribe().getInventorsSize());
    }

    @Test
    void allInheritedMethodsWorkTogether() {
        Board board = new Board(2);

        // Step 1: Create inventor and check inherited getters
        assertEquals(2, inventor.getAge());
        assertEquals(50, inventor.getId());

        // Step 2: Check inherited isPickable before applying effect
        assertTrue(inventor.isPickable(player));

        // Step 3: Add to board using inherited setupPhaseFour
        assertEquals(1, inventor.setupPhaseFour(board));

        // Step 4: Apply effect (internal checkInventorPairBuilding)
        inventor.applyEffect(player, game);
        assertEquals(1, player.getTribe().getInventorsSize());
        assertEquals(1, board.getBottomDeckableRow().size()); // pickstate removes not inventor

        // Step 5: Discard from board using inherited discard
        inventor.discard(board, 0);
        assertEquals(0, board.getBottomDeckableRow().size());

        // Step 6: Create another inventor and verify independence
        Inventor inv2 = new Inventor(3, 100, InventionType.BOAT);
        assertEquals(3, inv2.getAge());
        assertEquals(100, inv2.getId());
        assertTrue(inv2.isPickable(player));
    }
}
