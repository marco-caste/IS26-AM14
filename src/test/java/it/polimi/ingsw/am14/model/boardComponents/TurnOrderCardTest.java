package it.polimi.ingsw.am14.model.boardComponents;

import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TurnOrderCardTest {

    private TurnOrderCard turnOrderCard;
    private static final int NUM_PLAYERS = 3;

    @BeforeEach
    public void setUp() {
        turnOrderCard = new TurnOrderCard(NUM_PLAYERS);
    }

    // CONSTRUCTOR TESTS

    @Test
    public void testTurnOrderCardConstructor() {
        // Verify TurnOrderCard is created with correct initial values
        assertNotNull(turnOrderCard);
        assertEquals(NUM_PLAYERS, turnOrderCard.getNumPlayers());
    }

    // GETTERS AND ADDERS TESTS

    @Test
    public void testGetNumPlayers() {
        assertEquals(NUM_PLAYERS, turnOrderCard.getNumPlayers());
    }

    @Test
    public void testGetAndAddPlayer() {
        Player player1 = new Player("Player1", TotemColor.WHITE);
        Player player2 = new Player("Player2", TotemColor.RED);
        Player player3 = new Player("Player3", TotemColor.BLUE);

        turnOrderCard.addPlayer(player1);
        turnOrderCard.addPlayer(player2);
        turnOrderCard.addPlayer(player3);

        assertEquals(player1, turnOrderCard.getPlayer(0));
        assertEquals(player2, turnOrderCard.getPlayer(1));
        assertEquals(player3, turnOrderCard.getPlayer(2));
    }

    // CLEAR TESTS

    @Test
    public void testClearPlayerOrder() {
        Player player1 = new Player("P1", TotemColor.WHITE);
        Player player2 = new Player("P2", TotemColor.RED);

        turnOrderCard.addPlayer(player1);
        turnOrderCard.addPlayer(player2);

        turnOrderCard.clearPlayerOrder();

        // After clearing, getPlayer should throw exception
        assertThrows(IndexOutOfBoundsException.class, () -> turnOrderCard.getPlayer(0));
    }

    @Test
    public void testClearPlayerOrderMultipleTimes() {
        Player player = new Player("P1", TotemColor.WHITE);
        turnOrderCard.addPlayer(player);

        turnOrderCard.clearPlayerOrder();
        assertThrows(IndexOutOfBoundsException.class, () -> turnOrderCard.getPlayer(0));

        // Can add player again after clearing
        Player player2 = new Player("P2", TotemColor.RED);
        turnOrderCard.addPlayer(player2);
        assertEquals(player2, turnOrderCard.getPlayer(0));
    }

    // SETUP PHASE TEN TESTS

    @Test
    public void testSetupPhaseTenInitialFood() {
        Player player1 = new Player("P1", TotemColor.WHITE);
        Player player2 = new Player("P2", TotemColor.RED);
        Player player3 = new Player("P3", TotemColor.BLUE);

        turnOrderCard.addPlayer(player1);
        turnOrderCard.addPlayer(player2);
        turnOrderCard.addPlayer(player3);

        turnOrderCard.setupPhaseTen();

        Player newPlayer1 = turnOrderCard.getPlayer(0);
        Player newPlayer2 = turnOrderCard.getPlayer(1);
        Player newPlayer3 = turnOrderCard.getPlayer(2);

        // Verify initial food for 3 players: [2, 3, 3]
        assertEquals(2, newPlayer1.getNumFood());
        assertEquals(3, newPlayer2.getNumFood());
        assertEquals(3, newPlayer3.getNumFood());
    }

    @Test
    public void testSetupPhaseTeenShufflesPlayers() {
        Player player1 = new Player("P1", TotemColor.WHITE);
        Player player2 = new Player("P2", TotemColor.RED);
        Player player3 = new Player("P3", TotemColor.BLUE);

        turnOrderCard.addPlayer(player1);
        turnOrderCard.addPlayer(player2);
        turnOrderCard.addPlayer(player3);

        turnOrderCard.setupPhaseTen();

        // Players should still be present but may be in different order
        boolean foundP1 = false, foundP2 = false, foundP3 = false;
        for (int i = 0; i < NUM_PLAYERS; i++) {
            Player p = turnOrderCard.getPlayer(i);
            if (p == player1) foundP1 = true;
            if (p == player2) foundP2 = true;
            if (p == player3) foundP3 = true;
        }

        assertTrue(foundP1);
        assertTrue(foundP2);
        assertTrue(foundP3);
    }

    // RESOLVE BONUS OR MALUS TESTS

    @Test
    public void testResolveBonusOrMalusSimple() {
        Player player1 = new Player("P1", TotemColor.WHITE);
        Player player2 = new Player("P2", TotemColor.RED);
        Player player3 = new Player("P3", TotemColor.BLUE);

        turnOrderCard.addPlayer(player1);
        turnOrderCard.addPlayer(player2);
        turnOrderCard.addPlayer(player3);

        turnOrderCard.setupPhaseTen();

        // For 3 players: foodTable is [2, 0, -1]
        // Player 1 gets +2 food bonus
        int initialFood1 = turnOrderCard.getPlayer(0).getNumFood();
        int initialFood2 = turnOrderCard.getPlayer(1).getNumFood();
        int initialFood3 = turnOrderCard.getPlayer(2).getNumFood();

        turnOrderCard.resolveBonusOrMalus();

        // Player 1 should get 2 more food
        assertEquals(initialFood1 + 2, turnOrderCard.getPlayer(0).getNumFood());
        // Player 2 should get 0 more food
        assertEquals(initialFood2, turnOrderCard.getPlayer(1).getNumFood());
        // Player 3 is last player
        assertEquals(initialFood3 - 1, turnOrderCard.getPlayer(2).getNumFood());
    }

    @Test
    public void testResolveBonusOrMalusLastPlayerNoFood() {
        Player player1 = new Player("P1", TotemColor.WHITE);
        Player player2 = new Player("P2", TotemColor.RED);
        Player player3 = new Player("P3", TotemColor.BLUE);

        turnOrderCard.addPlayer(player1);
        turnOrderCard.addPlayer(player2);
        turnOrderCard.addPlayer(player3);

        // Set player3 to have 0 food
        player3.setNumFood(0);

        int player3PrestigeInitial = player3.getNumPrestigePoints();

        turnOrderCard.resolveBonusOrMalus();

        // Last player with 0 food gets -2 prestige penalty
        assertEquals(player3PrestigeInitial - 2, player3.getNumPrestigePoints());
    }

    @Test
    public void testResolveBonusOrMalusLastPlayerOneFood() {
        Player player1 = new Player("P1", TotemColor.WHITE);
        Player player2 = new Player("P2", TotemColor.RED);
        Player player3 = new Player("P3", TotemColor.BLUE);

        turnOrderCard.addPlayer(player1);
        turnOrderCard.addPlayer(player2);
        turnOrderCard.addPlayer(player3);

        // Set player3 to have 1 food and 0 PP
        player3.setNumFood(1);

        int player3PrestigeInitial = player3.getNumPrestigePoints();

        turnOrderCard.resolveBonusOrMalus();

        // Last player with 0 food gets -0 prestige penalty
        assertEquals(0, player3.getNumFood());
        assertEquals(player3PrestigeInitial, player3.getNumPrestigePoints());
    }

    @Test
    public void testResolveBonusOrMalusWithAddFoodStartOfRound() {
        Player player1 = new Player("P1", TotemColor.WHITE);
        Player player2 = new Player("P2", TotemColor.RED);
        Player player3 = new Player("P3", TotemColor.BLUE);

        turnOrderCard.addPlayer(player1);
        turnOrderCard.addPlayer(player2);
        turnOrderCard.addPlayer(player3);

        // Set addFoodStartOfRound for player1
        player1.setAddFoodStartOfRound(1);
        int player1InitialFood = player1.getNumFood();

        turnOrderCard.resolveBonusOrMalus();

        // Player 1 should get 2 (bonus) + 1 (addFoodStartOfRound) = 3 more food
        // For 3 players: foodTable is [2, 0, -1]
        assertEquals(player1InitialFood + 2 + 1, player1.getNumFood());
    }

    @Test
    public void testResolveBonusOrMalusLastPlayerWithAddFoodStartOfRound() {
        Player player1 = new Player("P1", TotemColor.WHITE);
        Player player2 = new Player("P2", TotemColor.RED);
        Player player3 = new Player("P3", TotemColor.BLUE);

        turnOrderCard.addPlayer(player1);
        turnOrderCard.addPlayer(player2);
        turnOrderCard.addPlayer(player3);

        // Set addFoodStartOfRound for player3 (last player)
        player3.setAddFoodStartOfRound(1);
        int player3InitialFood = player3.getNumFood();

        turnOrderCard.resolveBonusOrMalus();

        // Last player should get -1 (malus) + 1 (addFoodStartOfRound) = 0 more food
        assertEquals(player3InitialFood, player3.getNumFood());
    }
}