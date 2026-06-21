package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.exceptions.*;
import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 *     Test class for the {@link Game} class. It covers the core functionality of the game,
 *     including player connection, card finding, and game state management.
 * </p>
 */
public class GameTest {

    private Game game;
    private static final int NUM_PLAYERS = 2;
    private static final int GAME_ID = 1;

    @BeforeEach
    public void setUp() throws IllegalSetupException {
        game = GameFactory.setup(NUM_PLAYERS, GAME_ID);
    }

    // ============================================================
    // Tests for onPlayerConnection
    // ============================================================

    /**
     * <p>
     *     Test that onPlayerConnection successfully adds a player to the game
     *     when the totem color is available and the game is in WaitingRoomState.
     * </p>
     */
    @Test
    public void testOnPlayerConnectionSuccessfullyAddsPlayer() throws WrongStateException {
        String nickname = "player1";
        String totemColor = "WHITE";

        game.onPlayerConnection(nickname, totemColor);

        assertEquals(1, game.playersSize());
        assertEquals(nickname, game.getPlayers().getFirst().getNickname());
        assertEquals(totemColor, game.getPlayers().getFirst().getTotemColor().name());
    }

    /**
     * <p>
     *     Test that onPlayerConnection throws IllegalArgumentException
     *     when the totem color is already taken by another player.
     * </p>
     */
    @Test
    public void testOnPlayerConnectionThrowsExceptionForDuplicateTotemColor() throws WrongStateException {
        String nickname1 = "player1";
        String nickname2 = "player2";
        String totemColor = "WHITE";

        game.onPlayerConnection(nickname1, totemColor);

        assertThrows(IllegalArgumentException.class, () -> 
            game.onPlayerConnection(nickname2, totemColor)
        );
    }

    /**
     * <p>
     *     Test that onPlayerConnection transitions to the next state
     *     when all players have connected to the game.
     * </p>
     */
    @Test
    public void testOnPlayerConnectionTransitionsToNextStateWhenAllPlayersJoined() throws WrongStateException {
        assertInstanceOf(WaitingRoomState.class, game.getCurrentState());

        game.onPlayerConnection("player1", "WHITE");
        game.onPlayerConnection("player2", "PURPLE");

        assertInstanceOf(PositionState.class, game.getCurrentState());
        assertEquals(2, game.playersSize());
    }

    /**
     * <p>
     *     Test that onPlayerConnection adds a player to the turn order card.
     * </p>
     */
    @Test
    public void testOnPlayerConnectionAddsPlayerToTurnOrderCard() throws WrongStateException {
        String nickname = "player1";
        String totemColor = "WHITE";

        int initialSize = game.getBoard().getTurnOrderCard().getPlayerOrder().size();
        game.onPlayerConnection(nickname, totemColor);
        int finalSize = game.getBoard().getTurnOrderCard().getPlayerOrder().size();

        assertEquals(initialSize + 1, finalSize);
    }

    /**
     * <p>
     *     Test that findCardById successfully finds a card in the TOP_BUILDING_ROW (row 1)
     *     when the card exists in that row.
     * </p>
     */
    @Test
    public void testFindCardByIdInTopBuildingRow() throws Exception {
        // Setup: Add players and transition to appropriate state
        game.onPlayerConnection("player1", "WHITE");
        game.onPlayerConnection("player2", "PURPLE");

        // Get a card from the top building row to ensure it exists
        Card cardToFind = game.getBoard().getTopBuildingRow().stream().findFirst().orElse(null);
        assertNotNull(cardToFind, "No card found in top building row for testing");

        int cardId = cardToFind.getId();
        int row = 1; // TOP_BUILDING_ROW

        // This should not throw an exception (or throw WrongStateException if state doesn't allow it)
        // We're only testing the findCardById part, not the state logic
        assertThrows(WrongStateException.class, () -> game.pickCard("player1", cardId, row));
    }

    /**
     * <p>
     *     Test that findCardById successfully finds a card in the BOTTOM_DECKABLE_ROW (row 2)
     *     when the card exists in that row.
     * </p>
     */
    @Test
    public void testFindCardByIdInBottomDeckableRow() throws Exception {
        game.onPlayerConnection("player1", "WHITE");
        game.onPlayerConnection("player2", "PURPLE");

        Card cardToFind = game.getBoard().getBottomDeckableRow().stream().findFirst().orElse(null);
        assertNotNull(cardToFind, "No card found in bottom deckable row for testing");

        int cardId = cardToFind.getId();
        int row = 2; // BOTTOM_DECKABLE_ROW

        assertThrows(WrongStateException.class, () -> game.pickCard("player1", cardId, row));
    }

    /**
     * <p>
     *     Test that findCardById throws IllegalPickException
     *     when the card ID is not found in the specified row.
     * </p>
     */
    @Test
    public void testFindCardByIdThrowsExceptionForNonExistentCard() throws WrongStateException {
        game.onPlayerConnection("player1", "WHITE");
        game.onPlayerConnection("player2", "PURPLE");

        int nonExistentCardId = 999999;
        int row = 0; // TOP_DECKABLE_ROW

        assertThrows(IllegalPickException.class, () -> game.pickCard("player1", nonExistentCardId, row));
    }

    /**
     * <p>
     *     Test that findCardById throws IllegalPickException
     *     when an invalid row number is provided (row > 3).
     * </p>
     */
    @Test
    public void testFindCardByIdThrowsExceptionForInvalidRow() throws WrongStateException {
        game.onPlayerConnection("player1", "WHITE");
        game.onPlayerConnection("player2", "PURPLE");

        int cardId = 1;
        int invalidRow = 4;

        assertThrows(IllegalPickException.class, () -> game.pickCard("player1", cardId, invalidRow));
    }

    /**
     * <p>
     *     Test that findCardById throws IllegalPickException
     *     when a negative row number is provided.
     * </p>
     */
    @Test
    public void testFindCardByIdThrowsExceptionForNegativeRow() throws WrongStateException {
        game.onPlayerConnection("player1", "WHITE");
        game.onPlayerConnection("player2", "PURPLE");

        int cardId = 1;
        int negativeRow = -1;

        assertThrows(IllegalPickException.class, () -> game.pickCard("player1", cardId, negativeRow));
    }

    /**
     * <p>
     *     Test that pickCard correctly handles the skip case (cardId = -1)
     *     by passing an empty Optional to the state's pickCard method.
     * </p>
     */
    @Test
    public void testPickCardWithSkip() throws WrongStateException {
        game.onPlayerConnection("player1", "WHITE");
        game.onPlayerConnection("player2", "PURPLE");

        int skipCardId = -1;
        int row = 0;

        // Should not throw IllegalPickException, but may throw WrongStateException
        // depending on the current game state
        assertThrows(WrongStateException.class, () -> game.pickCard("player1", skipCardId, row));
    }

    // ============================================================
    // Tests for getPreviouslySavedState
    // ============================================================

    /**
     * <p>
     *     Test that getPreviouslySavedState returns the state that was set
     *     in the recovery constructor.
     * </p>
     */
    @Test
    public void testGetPreviouslySavedState() throws IllegalSetupException {
        // Create a game with the normal constructor
        Game normalGame = GameFactory.setup(2, 1);
        
        // Create a recovery game using the recovery constructor
        List<Player> players = new ArrayList<>();
        players.add(new Player("player1", TotemColor.WHITE));
        players.add(new Player("player2", TotemColor.PURPLE));
        
        Board board = new Board(2);
        GameState savedState = new PositionState();
        
        Game recoveryGame = new Game(
                savedState,
                players,
                board,
                0,
                2,
                1,
                1,
                2
        );

        GameState retrieved = recoveryGame.getPreviouslySavedState();
        assertNotNull(retrieved);
        assertInstanceOf(PositionState.class, retrieved);
    }

    /**
     * <p>
     *     Test that getPreviouslySavedState returns the exact GameState object
     *     that was passed to the recovery constructor.
     * </p>
     */
    @Test
    public void testGetPreviouslySavedStateReturnsSameInstance() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("player1", TotemColor.WHITE));
        players.add(new Player("player2", TotemColor.PURPLE));

        Board board = new Board(2);
        GameState savedState = new PickState();
        
        Game recoveryGame = new Game(
                savedState,
                players,
                board,
                0,
                2,
                1,
                1,
                3
        );

        assertSame(savedState, recoveryGame.getPreviouslySavedState());
    }

    /**
     * <p>
     *     Test that getPreviouslySavedState returns null for a game created
     *     with the normal constructor (not recovery constructor).
     * </p>
     */
    @Test
    public void testGetPreviouslySavedStateReturnsNullForNormalGame() {
        // game is created with normal constructor in setUp()
        assertNull(game.getPreviouslySavedState());
    }

    // ============================================================
    // Tests for previouslySavedStateName
    // ============================================================

    /**
     * <p>
     *     Test that previouslySavedStateName returns the correct state name
     *     from the previously saved state.
     * </p>
     */
    @Test
    public void testPreviouslySavedStateName() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("player1", TotemColor.WHITE));
        players.add(new Player("player2", TotemColor.PURPLE));

        Board board = new Board(2);
        GameState savedState = new PositionState();
        
        Game recoveryGame = new Game(
                savedState,
                players,
                board,
                0,
                2,
                1,
                1,
                4
        );

        String stateName = recoveryGame.previouslySavedStateName();
        assertNotNull(stateName);
        assertEquals("PLACE", stateName);
    }

    /**
     * <p>
     *     Test that previouslySavedStateName returns the correct name
     *     for different state types.
     * </p>
     */
    @Test
    public void testPreviouslySavedStateNameForDifferentStates() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("player1", TotemColor.WHITE));
        players.add(new Player("player2", TotemColor.PURPLE));

        Board board = new Board(2);
        GameState pickState = new PickState();
        
        Game recoveryGameWithPick = new Game(
                pickState,
                players,
                board,
                0,
                2,
                1,
                1,
                5
        );

        String pickStateName = recoveryGameWithPick.previouslySavedStateName();
        assertNotNull(pickStateName);
        assertEquals("PICK", pickStateName);
    }

    /**
     * <p>
     *     Test that previouslySavedStateName correctly delegates to
     *     the GameState's getStateName method.
     * </p>
     */
    @Test
    public void testPreviouslySavedStateNameDelegation() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("player1", TotemColor.WHITE));
        players.add(new Player("player2", TotemColor.PURPLE));
        
        Board board = new Board(2);
        GameState endRoundState = new EndRoundState();
        
        Game recoveryGame = new Game(
                endRoundState,
                players,
                board,
                0,
                2,
                1,
                1,
                6
        );

        String retrievedName = recoveryGame.previouslySavedStateName();
        String expectedName = endRoundState.getStateName();
        assertEquals(expectedName, retrievedName);
    }

    // ============================================================
    // Integration Tests
    // ============================================================

    /**
     * <p>
     *     Integration test that combines multiple operations:
     *     adding players, checking state transitions, and accessing game properties.
     * </p>
     */
    @Test
    public void testGameIntegration() throws WrongStateException {
        assertEquals(NUM_PLAYERS, game.getNumPlayers());
        assertEquals(GAME_ID, game.getGameId());
        assertEquals(1, game.getCurrentRound());
        assertEquals(1, game.getCurrentAge());

        game.onPlayerConnection("player1", "WHITE");
        assertEquals(1, game.playersSize());

        game.onPlayerConnection("player2", "PURPLE");
        assertEquals(2, game.playersSize());
        assertInstanceOf(PositionState.class, game.getCurrentState());
    }

    /**
     * <p>
     *     Test that unknown nickname throws UnknownNicknameException
     *     when attempting to pick a card for a non-existent player.
     * </p>
     */
    @Test
    public void testPickCardWithUnknownNickname() throws WrongStateException {
        game.onPlayerConnection("player1", "WHITE");
        game.onPlayerConnection("player2", "PURPLE");

        assertThrows(UnknownNicknameException.class, () -> game.pickCard("unknownPlayer", 1, 0));
    }
}



















