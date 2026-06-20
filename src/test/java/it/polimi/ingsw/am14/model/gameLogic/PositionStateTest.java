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

    private TestFixture buildFixture(int numPlayers) {
        PositionState state = new PositionState();
        Player p1 = new Player("P1", TotemColor.WHITE);
        Player p2 = new Player("P2", TotemColor.RED);
        Player p3 = new Player("P3", TotemColor.BLUE);

        // Crea il game con il numero di giocatori specificato
        Game game = new Game(numPlayers, 1);

        // Aggiungi i giocatori alla lista del game
        game.addPlayer(p1);
        game.addPlayer(p2);
        if (numPlayers > 2) {
            game.addPlayer(p3);
        }

        // Configura la TurnOrderCard con i giocatori in ordine deterministico
        game.getBoard().getTurnOrderCard().clearPlayerOrder();
        game.getBoard().getTurnOrderCard().addPlayer(p1);
        game.getBoard().getTurnOrderCard().addPlayer(p2);
        if (numPlayers > 2) {
            game.getBoard().getTurnOrderCard().addPlayer(p3);
        }

        // Imposta il currentPlayerIndex corretto
        game.setCurrentPlayerIndex(0);

        // Imposta lo stato a PositionState
        game.setCurrentState(state);

        return new TestFixture(state, game, p1, p2);
    }

    @Test
    public void testPlaceTotemThrowsUnknownPositionForMissingTrackCard() {
        TestFixture fixture = buildFixture(2);

        // 'Z' does not exist in offers path, so PositionState must reject it.
        assertThrows(UnknownPositionException.class,
                () -> fixture.state.placeTotem(fixture.game, fixture.p1, 'Z'));
    }

    @Test
    public void testPlaceTotemThrowsWrongTurnWhenPlayerIsNotCurrent() {
        TestFixture fixture = buildFixture(2);

        // currentPlayerIndex=0 means p1 is expected; p2 must trigger WrongTurnException.
        assertThrows(WrongTurnException.class,
                () -> fixture.state.placeTotem(fixture.game, fixture.p2, 'B'));
    }

    @Test
    public void testPlaceTotemAppliesTrackCardEffectAndIncrementsIndex() throws Exception {
        TestFixture fixture = buildFixture(2);

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
        // Usa 3 giocatori per evitare la transizione di stato dopo 2 piazzamenti
        TestFixture fixture = buildFixture(3);

        fixture.game.placeTotem(fixture.p1.getNickname(), 'B');

        fixture.game.placeTotem(fixture.p2.getNickname(), 'C');

        assertEquals(fixture.p1, fixture.game.getBoard().getOffersPath().offersPathGet('B').getPlayerTotem());
        assertEquals(fixture.p2, fixture.game.getBoard().getOffersPath().offersPathGet('C').getPlayerTotem());
        // Con 3 giocatori, dopo 2 piazzamenti siamo ancora in PositionState (non ControlState)
        assertInstanceOf(PositionState.class, fixture.game.getCurrentState());
    }

    @Test
    public void testIllegalPlacementDoesNotAdvanceTurnAndDoesNotChangeExistingTotem() throws Exception {
        TestFixture fixture = buildFixture(2);

        fixture.state.placeTotem(fixture.game, fixture.p1, 'B');

        assertThrows(IllegalPlacementException.class,
                () -> fixture.state.placeTotem(fixture.game, fixture.p2, 'B'));

        assertEquals(1, fixture.game.getCurrentPlayerIndex());
        assertEquals(fixture.p1, fixture.game.getBoard().getOffersPath().offersPathGet('B').getPlayerTotem());
    }

    @Test
    public void testPlaceTotemWithNullPlayerThrowsNullPointerExceptionCurrentBehavior() {
        TestFixture fixture = buildFixture(2);

        // Wrong-turn message dereferences p.getNickname(), so null player currently causes NPE.
        assertThrows(NullPointerException.class,
                () -> fixture.state.placeTotem(fixture.game, null, 'B'));
    }

    @Test
    public void testPlaceTotemWithNullContextThrowsNullPointerExceptionCurrentBehavior() {
        TestFixture fixture = buildFixture(2);

        assertThrows(NullPointerException.class,
                () -> fixture.state.placeTotem(null, fixture.p1, 'B'));
    }

    @Test
    public void testLastPlacementAppliesCorrectTrackCardEffectForSecondPlayer() throws Exception {
        TestFixture fixture = buildFixture(2);

        fixture.state.placeTotem(fixture.game, fixture.p1, 'B');
        fixture.state.placeTotem(fixture.game, fixture.p2, 'C');

        // Track card C is (top=1, bottom=0, food=0).
        assertEquals(1, fixture.p2.getNumTopPicksLeft());
        assertEquals(0, fixture.p2.getNumBottomPicksLeft());
        assertEquals(0, fixture.p2.getNumFood());
    }
}
