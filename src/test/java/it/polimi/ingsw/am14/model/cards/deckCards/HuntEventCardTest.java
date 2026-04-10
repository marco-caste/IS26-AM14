package it.polimi.ingsw.am14.model.cards.deckCards;

import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.buildings.effects.HuntExtraFoodAndPrestige;
import it.polimi.ingsw.am14.model.cards.buildings.types.HuntEventBuilding;
import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HuntEventCardTest {

    private HuntEvent huntEvent;
    private Game game;
    private Board board;
    private Player playerOne;
    private Player playerTwo;

    @BeforeEach
    void setUp() {
        huntEvent = new HuntEvent(2, 77);
        game = new Game(2);
        board = game.getBoard();
        playerOne = new Player("P1", TotemColor.WHITE);
        playerTwo = new Player("P2", TotemColor.BLUE);
        game.addPlayer(playerOne);
        game.addPlayer(playerTwo);
    }

    @Test
    void constructorAndGettersAreCorrect() {
        assertEquals(2, huntEvent.getAge());
        assertEquals(77, huntEvent.getId());
    }

    @Test
    void isPickableIsAlwaysFalseForEvents() {
        assertFalse(huntEvent.isPickable(playerOne));
    }

    @Test
    void setupPhaseFourAddsEventToTopRowAndReturnsZero() {
        int result = huntEvent.setupPhaseFour(board);

        assertEquals(0, result);
        assertEquals(1, board.topDeckableRowSize());
        assertTrue(board.getTopDeckableRow().contains(huntEvent));
    }

    @Test
    void discardMovesEventToResolveRowAndRemovesFromBottomRowByIndex() {
        Hunter hunter = new Hunter(1, 10, false);
        board.addToBottomDeckableRow(hunter);
        board.addToBottomDeckableRow(huntEvent);

        huntEvent.discard(board, 1);

        assertEquals(1, board.bottomDeckableRowSize());
        assertTrue(board.getBottomDeckableRow().contains(hunter));
        assertFalse(board.getBottomDeckableRow().contains(huntEvent));
        assertEquals(1, board.toResolveOtherEventsRowSize());
        assertSame(huntEvent, board.getFromResolveOtherEventsRow(0));
    }

    @Test
    void removeEventRemovesLastOtherEventFromBoard() {
        HuntEvent firstEvent = new HuntEvent(1, 11);
        board.addToResolvingOtherEventsRow(firstEvent);
        board.addToResolvingOtherEventsRow(huntEvent);

        huntEvent.removeEvent(game);

        assertEquals(1, board.toResolveOtherEventsRowSize());
        assertSame(firstEvent, board.getFromResolveOtherEventsRow(0));
    }

    @Test
    void applyEffectGivesFoodAndPrestigeAndResolvesEvent() {
        playerOne.setNumFood(0);
        playerTwo.setNumFood(0);
        playerOne.addToPrestigePoints(-playerOne.getNumPrestigePoints());
        playerTwo.addToPrestigePoints(-playerTwo.getNumPrestigePoints());
        playerOne.getTribe().addHunter(new Hunter(1, 1, false));
        playerOne.getTribe().addHunter(new Hunter(1, 2, false));
        playerTwo.getTribe().addHunter(new Hunter(1, 3, false));

        // This building effect is triggered in checkBuilding during each players iteration.
        playerOne.getTribe().addBuilding(new HuntEventBuilding(1, 1, 1, 100, new HuntExtraFoodAndPrestige()));

        board.addToResolvingOtherEventsRow(new ShamanEvent(1, 1));
        board.addToResolvingOtherEventsRow(huntEvent);

        huntEvent.applyEffect(playerOne, game);

        assertEquals(4, playerOne.getNumFood());
        assertEquals(6, playerOne.getNumPrestigePoints());
        assertEquals(1, playerTwo.getNumFood());
        assertEquals(2, playerTwo.getNumPrestigePoints());
        assertEquals(1, board.toResolveOtherEventsRowSize());
    }
}

