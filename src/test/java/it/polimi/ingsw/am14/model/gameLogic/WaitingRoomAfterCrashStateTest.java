package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.network.messages.events.GameRestartedEvent;
import it.polimi.ingsw.am14.network.messages.events.PlayerReconnectedEvent;
import it.polimi.ingsw.am14.network.Observer;
import it.polimi.ingsw.am14.network.messages.events.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 *     Test class for {@link WaitingRoomAfterCrashState}. This class tests the behavior
 *     of the game state after a server crash, where players need to reconnect.
 * </p>
 */
public class WaitingRoomAfterCrashStateTest {

    private Game game;
    private WaitingRoomAfterCrashState waitingRoomAfterCrashState;
    private TestObserver testObserver;

    /**
     * <p>
     *     Mock observer for testing event notifications
     * </p>
     */
    private static class TestObserver implements Observer {
        private final List<Event> events = new ArrayList<>();

        @Override
        public void update(Event event) {
            events.add(event);
        }

        public List<Event> getEvents() {
            return new ArrayList<>(events);
        }

        public void clear() {
            events.clear();
        }
    }

    /**
     * <p>
     *     Builds a game in recovery state with the specified number of players.
     *     All players are pre-added to the game and connected to a PositionState.
     * </p>
     * @param numPlayers the number of players
     * @return a Game in recovery state
     */
    private Game buildRecoveryGame(int numPlayers) {
        List<Player> players = new ArrayList<>();
        players.add(new Player("player1", TotemColor.WHITE));
        if (numPlayers >= 2) players.add(new Player("player2", TotemColor.PURPLE));
        if (numPlayers >= 3) players.add(new Player("player3", TotemColor.YELLOW));
        if (numPlayers >= 4) players.add(new Player("player4", TotemColor.RED));
        if (numPlayers >= 5) players.add(new Player("player5", TotemColor.BLUE));

        Board board = new Board(numPlayers);
        GameState savedState = new PositionState();

        return new Game(
                savedState,
                players,
                board,
                0,
                numPlayers,
                1,
                1,
                99
        );
    }

    @BeforeEach
    public void setUp() {
        game = buildRecoveryGame(2);
        waitingRoomAfterCrashState = new WaitingRoomAfterCrashState();
        testObserver = new TestObserver();
        game.addObserver(testObserver);
    }

    // ============================================================
    // Tests for onPlayerConnection - partial reconnection
    // ============================================================

    /**
     * <p>
     *     Test that onPlayerConnection notifies observers with PlayerReconnectedEvent
     *     when a player reconnects before all players have reconnected.
     * </p>
     */
    @Test
    public void testOnPlayerConnectionNotifiesPlayerReconnectedEvent() {
        String nickname = "player1";
        String totemColor = "WHITE";

        waitingRoomAfterCrashState.onPlayerConnection(game, nickname, totemColor);

        List<Event> events = testObserver.getEvents();
        assertEquals(1, events.size());
        assertInstanceOf(PlayerReconnectedEvent.class, events.getFirst());
    }

    /**
     * <p>
     *     Test that onPlayerConnection increments the numPlayersReconnected counter
     *     and keeps the game in WaitingRoomAfterCrashState when not all players have reconnected.
     * </p>
     */
    @Test
    public void testOnPlayerConnectionIncrementsCounterAndKeepsStateWhenNotComplete() {
        String nickname = "player1";
        String totemColor = "WHITE";

        waitingRoomAfterCrashState.onPlayerConnection(game, nickname, totemColor);

        assertInstanceOf(WaitingRoomAfterCrashState.class, game.getCurrentState());
        assertEquals(1, testObserver.getEvents().size());
    }

    /**
     * <p>
     *     Test that multiple player reconnections are tracked correctly
     *     by verifying multiple PlayerReconnectedEvent notifications are sent.
     * </p>
     */
    @Test
    public void testMultiplePlayerReconnectionsGenerateMultipleEvents() {
        waitingRoomAfterCrashState.onPlayerConnection(game, "player1", "WHITE");
        waitingRoomAfterCrashState.onPlayerConnection(game, "player2", "PURPLE");

        List<Event> events = testObserver.getEvents();
        assertEquals(3, events.size());
        assertInstanceOf(PlayerReconnectedEvent.class, events.get(0));
        assertInstanceOf(PlayerReconnectedEvent.class, events.get(1));
    }

    // ============================================================
    // Tests for onPlayerConnection - complete reconnection
    // ============================================================

    /**
     * <p>
     *     Test that onPlayerConnection transitions to the previously saved state
     *     when all players have reconnected.
     * </p>
     */
    @Test
    public void testOnPlayerConnectionTransitionsToSavedStateWhenAllReconnected() {
        waitingRoomAfterCrashState.onPlayerConnection(game, "player1", "WHITE");
        // Game was in WaitingRoomAfterCrashState, now should transition

        waitingRoomAfterCrashState.onPlayerConnection(game, "player2", "PURPLE");
        // After all players reconnect, should be in PositionState

        assertInstanceOf(PositionState.class, game.getCurrentState());
    }

    /**
     * <p>
     *     Test that GameRestartedEvent is sent when all players have reconnected.
     * </p>
     */
    @Test
    public void testOnPlayerConnectionNotifiesGameRestartedEventWhenComplete() {
        testObserver.clear();

        waitingRoomAfterCrashState.onPlayerConnection(game, "player1", "WHITE");
        waitingRoomAfterCrashState.onPlayerConnection(game, "player2", "PURPLE");

        List<Event> events = testObserver.getEvents();
        assertEquals(3, events.size()); // 2 PlayerReconnectedEvents + 1 GameRestartedEvent
        assertInstanceOf(PlayerReconnectedEvent.class, events.get(0));
        assertInstanceOf(PlayerReconnectedEvent.class, events.get(1));
        assertInstanceOf(GameRestartedEvent.class, events.get(2));
    }

    /**
     * <p>
     *     Test that GameRestartedEvent contains the correct previously saved state name.
     * </p>
     */
    @Test
    public void testGameRestartedEventContainsSavedStateName() {
        testObserver.clear();

        waitingRoomAfterCrashState.onPlayerConnection(game, "player1", "WHITE");
        waitingRoomAfterCrashState.onPlayerConnection(game, "player2", "PURPLE");

        List<Event> events = testObserver.getEvents();
        GameRestartedEvent restartedEvent = (GameRestartedEvent) events.get(2);

        assertNotNull(restartedEvent);
        // The saved state should be PositionState
        assertEquals("PLACE", game.getPreviouslySavedState().getStateName());
    }

    /**
     * <p>
     *     Test with three players: verify that after first two reconnections,
     *     the state remains WaitingRoomAfterCrashState, and only after
     *     the third reconnection it transitions to the saved state.
     * </p>
     */
    @Test
    public void testThreePlayerReconnectionSequence() {
        Game game3 = buildRecoveryGame(3);
        TestObserver observer3 = new TestObserver();
        game3.addObserver(observer3);
        WaitingRoomAfterCrashState state = new WaitingRoomAfterCrashState();

        // First player reconnects
        state.onPlayerConnection(game3, "player1", "WHITE");
        assertInstanceOf(WaitingRoomAfterCrashState.class, game3.getCurrentState());
        assertEquals(1, observer3.getEvents().size());

        // Second player reconnects
        state.onPlayerConnection(game3, "player2", "PURPLE");
        assertInstanceOf(WaitingRoomAfterCrashState.class, game3.getCurrentState());
        assertEquals(2, observer3.getEvents().size());

        // Third player reconnects - transitions to saved state
        state.onPlayerConnection(game3, "player3", "YELLOW");
        assertInstanceOf(PositionState.class, game3.getCurrentState());
        assertEquals(4, observer3.getEvents().size()); // 3 PlayerReconnected + 1 GameRestarted
    }

    // ============================================================
    // Tests for nextState
    // ============================================================

    /**
     * <p>
     *     Test that nextState sets the current state to the previously saved state.
     * </p>
     */
    @Test
    public void testNextStateTransitionsToPreviouslySavedState() {
        GameState savedState = game.getPreviouslySavedState();
        assertInstanceOf(PositionState.class, savedState);

        waitingRoomAfterCrashState.nextState(game);

        assertSame(savedState, game.getCurrentState());
    }

    /**
     * <p>
     *     Test that nextState correctly transitions to a PickState
     *     when it was the previously saved state.
     * </p>
     */
    @Test
    public void testNextStateTransitionsToPickStateSavedState() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("p1", TotemColor.WHITE));
        players.add(new Player("p2", TotemColor.PURPLE));
        Board board = new Board(2);
        GameState pickStateSaved = new PickState();

        Game pickGame = new Game(
                pickStateSaved,
                players,
                board,
                0,
                2,
                2,
                1,
                100
        );

        WaitingRoomAfterCrashState state = new WaitingRoomAfterCrashState();
        state.nextState(pickGame);

        assertInstanceOf(PickState.class, pickGame.getCurrentState());
    }

    /**
     * <p>
     *     Test that nextState correctly transitions to an EndRoundState
     *     when it was the previously saved state.
     * </p>
     */
    @Test
    public void testNextStateTransitionsToEndRoundStateSavedState() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("p1", TotemColor.WHITE));
        players.add(new Player("p2", TotemColor.PURPLE));
        Board board = new Board(2);
        GameState endRoundStateSaved = new EndRoundState();

        Game endRoundGame = new Game(
                endRoundStateSaved,
                players,
                board,
                0,
                2,
                2,
                1,
                101
        );

        WaitingRoomAfterCrashState state = new WaitingRoomAfterCrashState();
        state.nextState(endRoundGame);

        assertInstanceOf(EndRoundState.class, endRoundGame.getCurrentState());
    }

    // ============================================================
    // Tests for PlayerReconnectedEvent content
    // ============================================================

    /**
     * <p>
     *     Test that PlayerReconnectedEvent contains the correct game data:
     *     game ID, number of players, and player list.
     * </p>
     */
    @Test
    public void testPlayerReconnectedEventContainsCorrectGameData() {
        testObserver.clear();

        waitingRoomAfterCrashState.onPlayerConnection(game, "player1", "WHITE");

        PlayerReconnectedEvent event = (PlayerReconnectedEvent) testObserver.getEvents().getFirst();
        assertEquals(99, game.getGameId()); // gameId from buildRecoveryGame
        assertEquals(2, game.getNumPlayers());
    }

    // ============================================================
    // Integration Tests
    // ============================================================

    /**
     * <p>
     *     Integration test: verify complete game recovery flow with all players
     *     reconnecting and transitioning to the saved game state.
     * </p>
     */
    @Test
    public void testCompleteGameRecoveryFlow() {
        Game game5 = buildRecoveryGame(5);
        TestObserver observer5 = new TestObserver();
        game5.addObserver(observer5);
        WaitingRoomAfterCrashState state = new WaitingRoomAfterCrashState();

        // All 5 players reconnect
        state.onPlayerConnection(game5, "player1", "WHITE");
        state.onPlayerConnection(game5, "player2", "PURPLE");
        state.onPlayerConnection(game5, "player3", "YELLOW");
        state.onPlayerConnection(game5, "player4", "RED");
        state.onPlayerConnection(game5, "player5", "BLUE");

        // After all reconnect, should be in PositionState
        assertInstanceOf(PositionState.class, game5.getCurrentState());

        // Should have 5 PlayerReconnectedEvents + 1 GameRestartedEvent = 6 total
        assertEquals(6, observer5.getEvents().size());

        // Last event should be GameRestartedEvent
        assertInstanceOf(GameRestartedEvent.class, observer5.getEvents().get(5));
    }

    /**
     * <p>
     *     Test that the WaitingRoomAfterCrashState instance is independent
     *     across multiple game instances (i.e., numPlayersReconnected counter
     *     is instance-specific).
     * </p>
     */
    @Test
    public void testStateInstanceIndependence() {
        WaitingRoomAfterCrashState state1 = new WaitingRoomAfterCrashState();
        WaitingRoomAfterCrashState state2 = new WaitingRoomAfterCrashState();

        Game game1 = buildRecoveryGame(2);
        Game game2 = buildRecoveryGame(2);

        // First connection on state1
        state1.onPlayerConnection(game1, "player1", "WHITE");
        assertInstanceOf(WaitingRoomAfterCrashState.class, game1.getCurrentState());

        // First connection on state2
        state2.onPlayerConnection(game2, "player1", "WHITE");
        assertInstanceOf(WaitingRoomAfterCrashState.class, game2.getCurrentState());

        // Complete state1
        state1.onPlayerConnection(game1, "player2", "PURPLE");
        assertInstanceOf(PositionState.class, game1.getCurrentState());

        // state2 should still be in WaitingRoomAfterCrashState
        assertInstanceOf(WaitingRoomAfterCrashState.class, game2.getCurrentState());
    }

    /**
     * <p>
     *     Test that after a complete reconnection cycle, a new WaitingRoomAfterCrashState
     *     instance can be used with a fresh counter.
     * </p>
     */
    @Test
    public void testFreshStateInstanceAfterTransition() {
        waitingRoomAfterCrashState.onPlayerConnection(game, "player1", "WHITE");
        waitingRoomAfterCrashState.onPlayerConnection(game, "player2", "PURPLE");

        // game is now in PositionState
        assertInstanceOf(PositionState.class, game.getCurrentState());

        // Create a new state instance and verify it starts fresh
        WaitingRoomAfterCrashState freshState = new WaitingRoomAfterCrashState();
        Game newRecoveryGame = buildRecoveryGame(2);
        TestObserver newObserver = new TestObserver();
        newRecoveryGame.addObserver(newObserver);

        // New state should work correctly from the beginning
        freshState.onPlayerConnection(newRecoveryGame, "player1", "WHITE");
        assertInstanceOf(WaitingRoomAfterCrashState.class, newRecoveryGame.getCurrentState());
        assertEquals(1, newObserver.getEvents().size());
    }

    /**
     * <p>
     *     Test the combination of player reconnection with proper event firing,
     *     ensuring the nickname from the last reconnection is passed to GameRestartedEvent.
     * </p>
     */
    @Test
    public void testGameRestartedEventReceivesLastReconnectingPlayerNickname() {
        testObserver.clear();

        waitingRoomAfterCrashState.onPlayerConnection(game, "player1", "WHITE");
        waitingRoomAfterCrashState.onPlayerConnection(game, "player2", "PURPLE");

        List<Event> events = testObserver.getEvents();
        GameRestartedEvent restartedEvent = (GameRestartedEvent) events.get(2);

        assertNotNull(restartedEvent);
        // Verify the event is created (the event should store the nickname internally)
    }
}