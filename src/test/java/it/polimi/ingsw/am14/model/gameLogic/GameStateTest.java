package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.model.exceptions.WrongStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameStateTest {

    private GameState gameState;

    private static class TestGameState extends GameState {
        @Override
        protected void nextState(Game context) {
            // No-op: needed only to instantiate GameState in tests.
        }
    }

    @BeforeEach
    public void setUp() {
        gameState = new TestGameState();
    }

    @Test
    public void testPlaceTotemDefaultThrowsWrongStateException() {
        assertThrows(WrongStateException.class, () -> gameState.placeTotem(null, null, 'A'));
    }

    @Test
    public void testPickCardDefaultThrowsWrongStateException() {
        Optional<Card> card = Optional.empty();

        assertThrows(WrongStateException.class, () -> gameState.pickCard(null, null, card, 0));
    }

    @Test
    public void testOnPlayerConnectionDefaultThrowsWrongStateException() {
        assertThrows(WrongStateException.class, () -> gameState.onPlayerConnection(null, null, null));
    }

    @Test
    public void testEndRoundDefaultDoesNothing() {
        assertDoesNotThrow(() -> gameState.endRound(null));
    }
}

