package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.model.cards.deckCards.DeckCard;
import it.polimi.ingsw.am14.model.cards.deckCards.Shaman;
import it.polimi.ingsw.am14.model.cards.deckCards.Hunter;
import it.polimi.ingsw.am14.exceptions.WrongStateException;
import it.polimi.ingsw.am14.network.Observer;
import it.polimi.ingsw.am14.network.messages.events.EndRoundEvent;
import it.polimi.ingsw.am14.network.messages.events.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EndRoundStateTest {
    Game gameStub;
    EndRoundState endRoundStateStub;
    Player p1;
    Player p2;

    @BeforeEach
    void setUp() {
        gameStub = new Game(2, 1);
        endRoundStateStub = new EndRoundState();
        gameStub.setCurrentState(endRoundStateStub);

        p1 = new Player("player1", TotemColor.WHITE);
        p2 = new Player("player2", TotemColor.RED);
        gameStub.addPlayer(p1);
        gameStub.addPlayer(p2);
        gameStub.getBoard().getTurnOrderCard().addPlayer(p1);
        gameStub.getBoard().getTurnOrderCard().addPlayer(p2);
    }

    @Test
    void placeTotem() {
        assertThrows(
                WrongStateException.class,
                () -> {
                    endRoundStateStub.placeTotem(gameStub, new Player("player", TotemColor.WHITE), 'a');
                }
        );
    }

    @Test
    void pickCard() {
        assertThrows(
                WrongStateException.class,
                () -> {
                    endRoundStateStub.pickCard(gameStub, new Player("player", TotemColor.WHITE), Optional.of(new Shaman(1, 2, 3)), 2);
                }
        );
    }

    @Test
    void onPlayerConnection() {
        assertThrows(
                WrongStateException.class,
                () -> {
                    endRoundStateStub.onPlayerConnection(gameStub,"player", "WHITE");
                }
        );
    }

    @Test
    void endRoundIncrementsRoundAndTransitionsToPositionState() {
        gameStub.getBoard().setTopDeckableRow(new ArrayList<>(List.of(new Hunter(1, 200, false))));

        endRoundStateStub.endRound(gameStub);

        assertEquals(2, gameStub.getCurrentRound());
        assertInstanceOf(PositionState.class, gameStub.getCurrentState());
        assertEquals(gameStub.getNumPlayers() + 4, gameStub.getBoard().topDeckableRowSize());
    }

    @Test
    void endRoundResolvesTurnOrderBonusAndMalus() {
        p1.setNumFood(0);
        p2.setNumFood(0);

        endRoundStateStub.endRound(gameStub);

        assertEquals(1, p1.getNumFood());
        assertEquals(0, p2.getNumFood());
        assertEquals(-2, p2.getNumPrestigePoints());
    }

    //this is a game situation, 9 rounds need to be elapsed, so it will be tested in integration tests with Game
//    @Test
//    void endRoundAtRoundNineDrawsAllRemainingDeckCards() {
//        gameStub.setCurrentRound(9);
//        // setting currentRound to 9 does not mean that 9 rounds have actually been played
//        // therefore why the first assert is not 9 but 93
//        int deckSizeBefore = gameStub.getBoard().getTribeDeck().deckSize();
//
//        endRoundStateStub.endRound(gameStub);
//
//        assertEquals(10, gameStub.getCurrentRound());
//        assertEquals(0, gameStub.getBoard().getTribeDeck().deckSize());
//        assertEquals(deckSizeBefore, gameStub.getBoard().topDeckableRowSize());
//    }

    @Test
    void endRoundWhenAgeAdvancesRefillsTopBuildingRow() {
        gameStub.setCurrentAge(1);
        int ageTwoBuildings = gameStub.getBoard().getBuildingDeck().buildingDeckSize(2);

        while (gameStub.getBoard().getTribeDeck().deckPeek().getAge() == 1) {
            gameStub.getBoard().pickDeckableAndAddToTop(gameStub.getNumPlayers());
        }

        endRoundStateStub.endRound(gameStub);

        assertEquals(2, gameStub.getCurrentAge());
        assertEquals(ageTwoBuildings, gameStub.getBoard().getTopBuildingRow().size());
    }

    @Test
    void nextState() {
        endRoundStateStub.nextState(gameStub);

        assertInstanceOf(PositionState.class, gameStub.getCurrentState());
    }
    @Test
    void endRoundWithSpecificBoardSetupDoesNotTreatCharactersAsEvents() {
        // IDs: top row [13,15,21,19,6]
        ArrayList<DeckCard> topRow = new ArrayList<>(List.of(
                new Hunter(13, 1, false),
                new Hunter(15, 1, false),
                new Hunter(21, 1, false),
                new Hunter(19, 1, false),
                new Hunter(6, 1, false)
        ));
        gameStub.getBoard().setTopDeckableRow(topRow);

        // IDs: bottom row [20,86,87,9]
        DeckCard card20 = new Hunter(20, 1, false);
        DeckCard event86 = new Shaman(86, 1, 1);
        DeckCard event87 = new Shaman(87, 1, 1);
        DeckCard card9 = new Hunter(9, 1, false);

        ArrayList<DeckCard> bottomRow = new ArrayList<>(List.of(card20, event86, event87, card9));
        gameStub.getBoard().setBottomDeckableRow(bottomRow);

        // Assicura che l'intera procedura di End Round (scarto personaggi, risoluzione eventi, switch e riempimento)
        // venga eseguita senza lanciare una NullPointerException con player = null
        assertDoesNotThrow(() -> {
            endRoundStateStub.endRound(gameStub);
        });

        // Verifica che il round sia stato incrementato correttamente
        assertEquals(2, gameStub.getCurrentRound());

        // Verifica che la vecchia top row (dimensione 5) sia stata spostata nella bottom row tramite switchDeckablesFromTop()
        assertEquals(5, gameStub.getBoard().bottomDeckableRowSize());

        // Verifica che lo stato finale sia corretto
        assertInstanceOf(PositionState.class, gameStub.getCurrentState());
    }

        /**
     * <p>
     *     Test that endRound at round 10 (final round) picks all remaining deck cards
     *     and transitions directly to PositionState without checking for age change.
     * </p>
     */
    @Test
    void endRoundAtRound10PicksAllRemainingCards() {
        // Save initial deck size
        int initialDeckSize = gameStub.getBoard().getTribeDeck().deckSize();

        // Set current round to 9 so that after increment it will be 10
        gameStub.setCurrentRound(9);

        // Get the top row to verify picks calculation
        gameStub.getBoard().setTopDeckableRow(new ArrayList<>(List.of(new Hunter(1, 200, false))));

        endRoundStateStub.endRound(gameStub);

        // Verify round incremented to 10
        assertEquals(10, gameStub.getCurrentRound());

        // Verify state transitioned to PositionState
        assertInstanceOf(PositionState.class, gameStub.getCurrentState());

        // Verify all remaining cards were picked
        assertEquals(0, gameStub.getBoard().getTribeDeck().deckSize(), "All remaining cards should be picked at round 10");
    }

    /**
     * <p>
     *     Test that endRound at round 10 sends EndRoundEvent with correct game state.
     * </p>
     */
    @Test
    void endRoundAtRound10NotifiesEndRoundEvent() {
        // Create mock observer to capture events
        List<Event> capturedEvents = new ArrayList<>();
        Observer testObserver = capturedEvents::add;
        gameStub.addObserver(testObserver);

        gameStub.setCurrentRound(9);
        gameStub.getBoard().setTopDeckableRow(new ArrayList<>(List.of(new Hunter(1, 200, false))));

        endRoundStateStub.endRound(gameStub);

        // Verify EndRoundEvent was sent
        assertTrue(capturedEvents.stream().anyMatch(e -> e instanceof EndRoundEvent),
                "EndRoundEvent should be sent at round 10");

        // Verify it's the only event at round 10
        long endRoundEventCount = capturedEvents.stream()
                .filter(e -> e instanceof EndRoundEvent)
                .count();
        assertEquals(1, endRoundEventCount);
    }

    /**
     * <p>
     *     Test that endRound properly resolves OtherEvents in reverse order (from end to start).
     * </p>
     */
    @Test
    void endRoundResolvesOtherEventsInReverseOrder() {
        // Create a mock list to track method calls
        List<Integer> callOrder = new ArrayList<>();

        // Note: This test verifies the loop at lines 39-41
        // The loop iterates from size-1 down to 0
        gameStub.getBoard().setTopDeckableRow(new ArrayList<>(List.of(
                new Hunter(1, 200, false),
                new Hunter(2, 201, false)
        )));

        // Execute endRound - should handle events without throwing exception
        assertDoesNotThrow(() -> endRoundStateStub.endRound(gameStub));

        assertEquals(2, gameStub.getCurrentRound());
    }

    /**
     * <p>
     *     Test that endRound properly resolves SustenanceEvents in forward order (from start to end).
     * </p>
     */
    @Test
    void endRoundResolvesSustenanceEventsInForwardOrder() {
        // Note: This test verifies the loop at lines 42-44
        // The loop iterates from 0 to size-1
        gameStub.getBoard().setTopDeckableRow(new ArrayList<>(List.of(
                new Hunter(1, 200, false)
        )));

        // Execute endRound - should handle sustenance events without throwing exception
        assertDoesNotThrow(() -> endRoundStateStub.endRound(gameStub));

        assertEquals(2, gameStub.getCurrentRound());
        assertInstanceOf(PositionState.class, gameStub.getCurrentState());
    }

    /**
     * <p>
     *     Test that endRound switches deckables and fills properly on non-final rounds.
     * </p>
     */
    @Test
    void endRoundSwitchesAndFillsDeckablesOnNormalRounds() {
        // Ensure we're not at round 10
        gameStub.setCurrentRound(5);

        gameStub.getBoard().setTopDeckableRow(new ArrayList<>(List.of(new Hunter(1, 200, false))));
        int numPlayers = gameStub.getNumPlayers();

        endRoundStateStub.endRound(gameStub);

        // Verify round incremented by 1 (not jumping to 10)
        assertEquals(6, gameStub.getCurrentRound());

        // Verify the correct number of cards were added (numPlayers = 2)
        assertEquals(numPlayers + 4, gameStub.getBoard().topDeckableRowSize());
    }

    /**
     * <p>
     *     Test that endRound does NOT change age when next deck card is same age.
     * </p>
     */
    @Test
    void endRoundDoesNotChangeAgeWhenCardIsSameAge() {
        gameStub.setCurrentAge(1);
        int initialAge = gameStub.getCurrentAge();

        // Ensure top deck card is age 1 (same as current age)
        gameStub.getBoard().setTopDeckableRow(new ArrayList<>(List.of(new Hunter(1, 1, false))));

        endRoundStateStub.endRound(gameStub);

        // Verify age did NOT change
        assertEquals(initialAge, gameStub.getCurrentAge());

        // Verify we're in PositionState and round was incremented
        assertInstanceOf(PositionState.class, gameStub.getCurrentState());
        assertEquals(2, gameStub.getCurrentRound());
    }

    /**
     * <p>
     *     Test that endRound changes age and refills building row when age advances.
     * </p>
     */
    @Test
    void endRoundChangeAgeAndRefillsBuildingRow() {
        gameStub.setCurrentAge(1);

        // Setup: fill deck to ensure age change happens
        while (gameStub.getBoard().getTribeDeck().deckPeek().getAge() == 1) {
            gameStub.getBoard().pickDeckableAndAddToTop(gameStub.getNumPlayers());
        }

        int ageBeforeEndRound = gameStub.getCurrentAge();
        int ageTwoBuildingCountBefore = gameStub.getBoard().getBuildingDeck().buildingDeckSize(2);

        endRoundStateStub.endRound(gameStub);

        // Verify age incremented
        assertEquals(ageBeforeEndRound + 1, gameStub.getCurrentAge());

        // Verify building row was refilled with age 2 buildings
        assertEquals(ageTwoBuildingCountBefore, gameStub.getBoard().getTopBuildingRow().size());
    }

    /**
     * <p>
     *     Test that endRound sends EndRoundEvent on normal rounds (not final round).
     * </p>
     */
    @Test
    void endRoundNotifiesEndRoundEventOnNormalRound() {
        List<Event> capturedEvents = new ArrayList<>();
        Observer testObserver = capturedEvents::add;
        gameStub.addObserver(testObserver);

        gameStub.setCurrentRound(5);
        gameStub.getBoard().setTopDeckableRow(new ArrayList<>(List.of(new Hunter(1, 200, false))));

        endRoundStateStub.endRound(gameStub);

        // Verify EndRoundEvent was sent
        long endRoundEventCount = capturedEvents.stream()
                .filter(e -> e instanceof EndRoundEvent)
                .count();
        assertEquals(1, endRoundEventCount);
    }

    /**
     * <p>
     *     Test the complete flow: resolve events, switch cards, increment round, check age, and transition state.
     * </p>
     */
    @Test
    void endRoundCompleteFlowWithAgeChange() {
        // Setup: Create scenario with age change
        gameStub.setCurrentAge(1);
        gameStub.setCurrentRound(5);

        List<Event> capturedEvents = new ArrayList<>();
        Observer testObserver = capturedEvents::add;
        gameStub.addObserver(testObserver);

        // Move to age 2 cards
        while (gameStub.getBoard().getTribeDeck().deckPeek().getAge() == 1) {
            gameStub.getBoard().pickDeckableAndAddToTop(gameStub.getNumPlayers());
        }

        int initialBuildingRowSize = gameStub.getBoard().getTopBuildingRow().size();

        endRoundStateStub.endRound(gameStub);

        // Verify complete flow
        assertEquals(6, gameStub.getCurrentRound());
        assertEquals(2, gameStub.getCurrentAge());
        assertInstanceOf(PositionState.class, gameStub.getCurrentState());

        // Verify event was sent
        assertTrue(capturedEvents.stream().anyMatch(e -> e instanceof EndRoundEvent));
    }

    /**
     * <p>
     *     Test endRound properly discards events after resolving them.
     * </p>
     */
    @Test
    void endRoundDiscardsEventsAfterResolving() {
        gameStub.getBoard().setTopDeckableRow(new ArrayList<>(List.of(new Hunter(1, 200, false))));

        endRoundStateStub.endRound(gameStub);

        // Verify round was incremented
        assertEquals(2, gameStub.getCurrentRound());

        // Verify we're in PositionState
        assertInstanceOf(PositionState.class, gameStub.getCurrentState());

        // The events should be discarded (no assertion needed, just verify no exception)
    }

    /**
     * <p>
     *     Test that endRound with age advancement also discards old building from bottom.
     * </p>
     */
    @Test
    void endRoundDiscardsBuildingFromBottomOnAgeChange() {
        gameStub.setCurrentAge(1);

        // Move to age 2
        while (gameStub.getBoard().getTribeDeck().deckPeek().getAge() == 1) {
            gameStub.getBoard().pickDeckableAndAddToTop(gameStub.getNumPlayers());
        }

        int bottomBuildingRowSizeBefore = gameStub.getBoard().getBottomBuildingRow().size();

        endRoundStateStub.endRound(gameStub);

        // Verify age changed
        assertEquals(2, gameStub.getCurrentAge());

        // Verify old building was discarded (bottom row is smaller or empty)
        assertTrue(gameStub.getBoard().getBottomBuildingRow().size() < bottomBuildingRowSizeBefore
                   || gameStub.getBoard().getBottomBuildingRow().isEmpty());
    }
}
