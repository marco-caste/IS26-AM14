package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.exceptions.IllegalPlacementException;
import it.polimi.ingsw.am14.exceptions.UnknownPositionException;
import it.polimi.ingsw.am14.exceptions.WrongTurnException;
import it.polimi.ingsw.am14.model.boardComponents.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PositionStateTest {

    // Shared fixture object so each test can focus only on behavior under test.
    private static class TestFixture {
        private final PositionState state;
        private final Game game;
        private final Player p1;
        private final Player p2;

        private TestFixture(PositionState state, Game game, Player p1, Player p2) {
            this.state = state;
            this.game = game;
            this.p1 = p1;
            this.p2 = p2;
        }
    }

    private TestFixture buildFixture(int currentPlayerIndex) {
        PositionState state = new PositionState();
        Player p1 = new Player("P1", TotemColor.WHITE);
        Player p2 = new Player("P2", TotemColor.RED);

        // Turn order is deterministic in tests: p1 first, p2 second.
        TurnOrderCard turnOrderCard = new TurnOrderCard(2);
        turnOrderCard.addPlayer(p1);
        turnOrderCard.addPlayer(p2);

        // Real OffersPath for 2 players gives track cards B, C, E, F.
        OffersPath offersPath = new OffersPath(2);

        // Minimal board/game snapshot: only dependencies needed by PositionState are populated.
        Board board = new Board(
                turnOrderCard,
                offersPath,
                null,
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        Game game = new Game(
                state,
                new ArrayList<>(List.of(p1, p2)),
                board,
                currentPlayerIndex,
                2,
                1,
                1
        );

        return new TestFixture(state, game, p1, p2);
    }

    @Test
    public void testPlaceTotemThrowsUnknownPositionForMissingTrackCard() {
        TestFixture fixture = buildFixture(0);

        // 'Z' does not exist in offers path, so PositionState must reject it.
        assertThrows(UnknownPositionException.class,
                () -> fixture.state.placeTotem(fixture.game, fixture.p1, 'Z'));
    }

    @Test
    public void testPlaceTotemThrowsUnknownPositionForLowercaseTrackCardName() {
        TestFixture fixture = buildFixture(0);

        // OffersPath keys are uppercase, so lowercase input is currently treated as unknown.
        assertThrows(UnknownPositionException.class,
                () -> fixture.state.placeTotem(fixture.game, fixture.p1, 'b'));
    }

    @Test
    public void testPlaceTotemThrowsWrongTurnWhenPlayerIsNotCurrent() {
        TestFixture fixture = buildFixture(0);

        // currentPlayerIndex=0 means p1 is expected; p2 must trigger WrongTurnException.
        assertThrows(WrongTurnException.class,
                () -> fixture.state.placeTotem(fixture.game, fixture.p2, 'B'));
    }

    @Test
    public void testPlaceTotemAppliesTrackCardEffectAndIncrementsIndex() throws Exception {
        TestFixture fixture = buildFixture(0);

        fixture.state.placeTotem(fixture.game, fixture.p1, 'B');

        // Track card B is (top=0, bottom=1, food=0): effects must be applied on p1.
        assertNotNull(fixture.game.getBoard().getOffersPath().offersPathGet('B'));
        assertEquals(fixture.p1,
                fixture.game.getBoard().getOffersPath().offersPathGet('B').getPlayerTotem());
        assertEquals(0, fixture.p1.getNumTopPicksLeft());
        assertEquals(1, fixture.p1.getNumBottomPicksLeft());
        assertEquals(0, fixture.p1.getNumFood());

        // Successful placement advances turn index by 1.
        assertEquals(1, fixture.game.getCurrentPlayerIndex());
    }

    @Test
    public void testTwoValidPlacementsOnDifferentTrackCardsSetTotemsAndAdvanceIndex() throws Exception {
        TestFixture fixture = buildFixture(0);

        fixture.state.placeTotem(fixture.game, fixture.p1, 'B');

        fixture.state.placeTotem(fixture.game, fixture.p2, 'C');

        assertEquals(fixture.p1, fixture.game.getBoard().getOffersPath().offersPathGet('B').getPlayerTotem());
        assertEquals(fixture.p2, fixture.game.getBoard().getOffersPath().offersPathGet('C').getPlayerTotem());
        assertInstanceOf(PickState.class, fixture.game.getCurrentState());
    }

    @Test
    public void testIllegalPlacementDoesNotAdvanceTurnAndDoesNotChangeExistingTotem() throws Exception {
        TestFixture fixture = buildFixture(0);

        fixture.state.placeTotem(fixture.game, fixture.p1, 'B');

        assertThrows(IllegalPlacementException.class,
                () -> fixture.state.placeTotem(fixture.game, fixture.p2, 'B'));

        assertEquals(1, fixture.game.getCurrentPlayerIndex());
        assertEquals(fixture.p1, fixture.game.getBoard().getOffersPath().offersPathGet('B').getPlayerTotem());
    }

    @Test
    public void testPlaceTotemWithNullPlayerThrowsNullPointerExceptionCurrentBehavior() {
        TestFixture fixture = buildFixture(0);

        // Wrong-turn message dereferences p.getNickname(), so null player currently causes NPE.
        assertThrows(NullPointerException.class,
                () -> fixture.state.placeTotem(fixture.game, null, 'B'));
    }

    @Test
    public void testPlaceTotemWithNullContextThrowsNullPointerExceptionCurrentBehavior() {
        TestFixture fixture = buildFixture(0);

        assertThrows(NullPointerException.class,
                () -> fixture.state.placeTotem(null, fixture.p1, 'B'));
    }

    @Test
    public void testLastPlacementAppliesCorrectTrackCardEffectForSecondPlayer() throws Exception {
        TestFixture fixture = buildFixture(1);

        fixture.state.placeTotem(fixture.game, fixture.p2, 'C');

        // Track card C is (top=1, bottom=0, food=0).
        assertEquals(1, fixture.p2.getNumTopPicksLeft());
        assertEquals(0, fixture.p2.getNumBottomPicksLeft());
        assertEquals(0, fixture.p2.getNumFood());
    }

    @Test
    public void testCurrentPlayerIndexOutOfBoundsThrowsIndexOutOfBoundsException() {
        TestFixture fixture = buildFixture(2);

        // With 2 players, current index 2 is invalid and fails before transition logic.
        assertThrows(IndexOutOfBoundsException.class,
                () -> fixture.state.placeTotem(fixture.game, fixture.p1, 'B'));
    }
}
