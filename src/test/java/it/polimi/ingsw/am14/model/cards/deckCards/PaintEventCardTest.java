package it.polimi.ingsw.am14.model.cards.deckCards;

import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.buildings.effects.PaintExtraFood;
import it.polimi.ingsw.am14.model.cards.buildings.types.PaintEventBuilding;
import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaintEventCardTest {

    private PaintEvent paintEvent;
    private Game game;
    private Board board;
    private Player playerOne;
    private Player playerTwo;

    @BeforeEach
    void setUp() {
        paintEvent = new PaintEvent(2, 88);
        game = new Game(2,  1);
        board = game.getBoard();
        playerOne = new Player("P1", TotemColor.WHITE);
        playerTwo = new Player("P2", TotemColor.BLUE);
        game.addPlayer(playerOne);
        game.addPlayer(playerTwo);
    }

    @Test
    void constructorAndGettersAreCorrect() {
        assertEquals(2, paintEvent.getAge());
        assertEquals(88, paintEvent.getId());
    }

    @Test
    void isPickableIsAlwaysFalseForEvents() {
        assertFalse(paintEvent.isPickable(playerOne));
    }

    @Test
    void setupPhaseFourAddsEventToTopRowAndReturnsZero() {
        int result = paintEvent.setupPhaseFour(board);

        assertEquals(0, result);
        assertEquals(1, board.topDeckableRowSize());
        assertTrue(board.getTopDeckableRow().contains(paintEvent));
    }

    @Test
    void discardMovesEventToResolveRowAndRemovesFromBottomRowByIndex() {
        Hunter hunter = new Hunter(1, 10, false);
        board.addToBottomDeckableRow(hunter);
        board.addToBottomDeckableRow(paintEvent);

        paintEvent.discard(board, 1);

        assertEquals(1, board.bottomDeckableRowSize());
        assertTrue(board.getBottomDeckableRow().contains(hunter));
        assertFalse(board.getBottomDeckableRow().contains(paintEvent));
        assertEquals(1, board.toResolveOtherEventsRowSize());
        assertSame(paintEvent, board.getFromResolveOtherEventsRow(0));
    }

    @Test
    void removeEventRemovesLastOtherEventFromBoard() {
        PaintEvent firstEvent = new PaintEvent(1, 7);
        board.addToResolvingOtherEventsRow(firstEvent);
        board.addToResolvingOtherEventsRow(paintEvent);

        paintEvent.removeEvent(game);

        assertEquals(1, board.toResolveOtherEventsRowSize());
        assertSame(firstEvent, board.getFromResolveOtherEventsRow(0));
    }

    @Test
    void applyEffectCoversLossAndWinBranchesAndRemovesEvent() {
        playerOne.getTribe().addArtist(new Artist(1, 1));
        playerTwo.getTribe().addArtist(new Artist(1, 2));
        playerTwo.getTribe().addArtist(new Artist(1, 3));

        board.addToResolvingOtherEventsRow(paintEvent);

        paintEvent.applyEffect(playerOne, game);

        assertEquals(-2, playerOne.getNumPrestigePoints());
        assertEquals(4, playerTwo.getNumPrestigePoints());
        assertEquals(0, playerOne.getNumFood());
        assertEquals(0, playerTwo.getNumFood());
        assertEquals(0, board.toResolveOtherEventsRowSize());
    }

    @Test
    void applyEffectTriggersPaintBuildingForCallingPlayerEachIteration() {
        playerOne.getTribe().addArtist(new Artist(1, 1));
        playerOne.getTribe().addBuilding(new PaintEventBuilding(1, 1, 1, 50, new PaintExtraFood()));

        board.addToResolvingOtherEventsRow(paintEvent);

        paintEvent.applyEffect(playerOne, game);

        assertEquals(2, playerOne.getNumFood());
        assertEquals(-2, playerOne.getNumPrestigePoints());
        assertEquals(0, playerTwo.getNumFood());
        assertEquals(0, playerTwo.getNumPrestigePoints());
        assertEquals(0, board.toResolveOtherEventsRowSize());
    }
}
