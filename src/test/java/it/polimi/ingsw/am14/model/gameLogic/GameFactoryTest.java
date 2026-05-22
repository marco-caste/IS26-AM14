package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.exceptions.IllegalSetupException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GameFactory Tests")
class GameFactoryTest {

    private static final int VALID_GAME_ID = 1;

    // ============================================
    // Test: Valid Number of Players (2-5)
    // ============================================

    @Test
    @DisplayName("Setup with 2 players (minimum valid)")
    void setupWith2Players() throws IllegalSetupException {
        Game game = GameFactory.setup(2, VALID_GAME_ID);
        
        assertNotNull(game, "Game should not be null");
        assertEquals(2, game.getNumPlayers(), "Game should have 2 players");
        assertNotNull(game.getBoard(), "Game should have a board");
    }

    @Test
    @DisplayName("Setup with 3 players")
    void setupWith3Players() throws IllegalSetupException {
        Game game = GameFactory.setup(3, VALID_GAME_ID);
        
        assertNotNull(game, "Game should not be null");
        assertEquals(3, game.getNumPlayers(), "Game should have 3 players");
        assertNotNull(game.getBoard(), "Game should have a board");
    }

    @Test
    @DisplayName("Setup with 4 players")
    void setupWith4Players() throws IllegalSetupException {
        Game game = GameFactory.setup(4, VALID_GAME_ID);
        
        assertNotNull(game, "Game should not be null");
        assertEquals(4, game.getNumPlayers(), "Game should have 4 players");
        assertNotNull(game.getBoard(), "Game should have a board");
    }

    @Test
    @DisplayName("Setup with 5 players (maximum valid)")
    void setupWith5Players() throws IllegalSetupException {
        Game game = GameFactory.setup(5, VALID_GAME_ID);
        
        assertNotNull(game, "Game should not be null");
        assertEquals(5, game.getNumPlayers(), "Game should have 5 players");
        assertNotNull(game.getBoard(), "Game should have a board");
    }

    // ============================================
    // Test: Invalid Number of Players
    // ============================================

    @Test
    @DisplayName("Setup with 0 players (below minimum)")
    void setupWith0PlayersThrows() {
        assertThrows(IllegalSetupException.class, () -> {
            GameFactory.setup(0, VALID_GAME_ID);
        }, "Should throw IllegalSetupException for 0 players");
    }

    @Test
    @DisplayName("Setup with 1 player (below minimum)")
    void setupWith1PlayerThrows() {
        assertThrows(IllegalSetupException.class, () -> {
            GameFactory.setup(1, VALID_GAME_ID);
        }, "Should throw IllegalSetupException for 1 player");
    }

    @Test
    @DisplayName("Setup with 6 players (above maximum)")
    void setupWith6PlayersThrows() {
        assertThrows(IllegalSetupException.class, () -> {
            GameFactory.setup(6, VALID_GAME_ID);
        }, "Should throw IllegalSetupException for 6 players");
    }

    @Test
    @DisplayName("Setup with 10 players (well above maximum)")
    void setupWith10PlayersThrows() {
        assertThrows(IllegalSetupException.class, () -> {
            GameFactory.setup(10, VALID_GAME_ID);
        }, "Should throw IllegalSetupException for 10 players");
    }

    @Test
    @DisplayName("Setup with negative players")
    void setupWithNegativePlayersThrows() {
        assertThrows(IllegalSetupException.class, () -> {
            GameFactory.setup(-5, VALID_GAME_ID);
        }, "Should throw IllegalSetupException for negative players");
    }

    // ============================================
    // Test: Game Initialization & Board Setup
    // ============================================

    @Test
    @DisplayName("Game board is initialized properly")
    void gameBoardInitialized() throws IllegalSetupException {
        Game game = GameFactory.setup(3, VALID_GAME_ID);
        
        assertNotNull(game.getBoard(), "Board should be initialized");
        assertNotNull(game.getBoard().getTribeDeck(), "Tribe deck should be initialized");
        assertNotNull(game.getBoard().getBuildingDeck(), "Building deck should be initialized");
    }

    @Test
    @DisplayName("Deckable row is populated after setup")
    void deckableRowPopulated() throws IllegalSetupException {
        Game game = GameFactory.setup(3, VALID_GAME_ID);
        
        int deckableRowSize = game.getBoard().topDeckableRowSize();
        assertTrue(deckableRowSize > 0, "Deckable row should have cards after setup");
    }

    @Test
    @DisplayName("Different gameIdCounter values can be used")
    void multipleGamesWithDifferentIds() throws IllegalSetupException {
        Game game1 = GameFactory.setup(2, 1);
        Game game2 = GameFactory.setup(2, 100);
        
        assertNotNull(game1, "First game should be created");
        assertNotNull(game2, "Second game should be created");
        // Games are independent
        assertNotSame(game1, game2, "Games should be different objects");
    }

}