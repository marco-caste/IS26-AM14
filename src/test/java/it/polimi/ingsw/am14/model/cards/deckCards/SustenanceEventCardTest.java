package it.polimi.ingsw.am14.model.cards.deckCards;

import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.buildings.effects.SustenanceDiscountArtists;
import it.polimi.ingsw.am14.model.cards.buildings.effects.SustenanceDiscountGatherers;
import it.polimi.ingsw.am14.model.cards.buildings.types.SustenanceEventBuilding;
import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SustenanceEventCardTest {

    private SustenanceEvent sustenanceEvent;
    private Game game;
    private Board board;
    private Player playerOne;
    private Player playerTwo;

    @BeforeEach
    void setUp() {
        sustenanceEvent = new SustenanceEvent(2, 66);
        game = new Game(2);
        board = game.getBoard();
        playerOne = new Player("P1", TotemColor.WHITE);
        playerTwo = new Player("P2", TotemColor.BLUE);
        game.addPlayer(playerOne);
        game.addPlayer(playerTwo);
    }

    @Test
    void constructorAndGettersAreCorrect() {
        assertEquals(2, sustenanceEvent.getAge());
        assertEquals(66, sustenanceEvent.getId());
    }

    @Test
    void isPickableIsAlwaysFalseForEvents() {
        assertFalse(sustenanceEvent.isPickable(playerOne));
    }

    @Test
    void setupPhaseFourAddsEventToTopRowAndReturnsZero() {
        int result = sustenanceEvent.setupPhaseFour(board);

        assertEquals(0, result);
        assertEquals(1, board.topDeckableRowSize());
        assertTrue(board.getTopDeckableRow().contains(sustenanceEvent));
    }

    @Test
    void discardMovesEventToSustenanceResolveRowAndRemovesFromBottomRowByIndex() {
        Hunter hunter = new Hunter(1, 10, false);
        board.addToBottomDeckableRow(hunter);
        board.addToBottomDeckableRow(sustenanceEvent);

        sustenanceEvent.discard(board, 1);

        assertEquals(1, board.bottomDeckableRowSize());
        assertTrue(board.getBottomDeckableRow().contains(hunter));
        assertFalse(board.getBottomDeckableRow().contains(sustenanceEvent));
        assertEquals(1, board.toResolveSustenanceEventsRowSize());
        assertSame(sustenanceEvent, board.getFromResolveSustenanceEventRow(0));
    }

    @Test
    void discardAddsToSustenanceRowWithoutTouchingOtherEventsRow() {
        SustenanceEvent existingSustenanceEvent = new SustenanceEvent(1, 67);
        PaintEvent existingOtherEvent = new PaintEvent(1, 68);
        Hunter hunter = new Hunter(1, 69, false);

        board.addToResolvingSustenanceEventsRow(existingSustenanceEvent);
        board.addToResolvingOtherEventsRow(existingOtherEvent);
        board.addToBottomDeckableRow(sustenanceEvent);
        board.addToBottomDeckableRow(hunter);

        sustenanceEvent.discard(board, 0);

        assertEquals(1, board.bottomDeckableRowSize());
        assertSame(hunter, board.getBottomDeckableRow().get(0));
        assertEquals(2, board.toResolveSustenanceEventsRowSize());
        assertSame(existingSustenanceEvent, board.getFromResolveSustenanceEventRow(0));
        assertSame(sustenanceEvent, board.getFromResolveSustenanceEventRow(1));
        assertEquals(1, board.toResolveOtherEventsRowSize());
        assertSame(existingOtherEvent, board.getFromResolveOtherEventsRow(0));
    }

    @Test
    void removeEventRemovesLastOtherEventFromBoard() {
        SustenanceEvent firstEvent = new SustenanceEvent(1, 7);
        board.addToResolvingOtherEventsRow(firstEvent);
        board.addToResolvingOtherEventsRow(sustenanceEvent);

        sustenanceEvent.removeEvent(game);

        assertEquals(1, board.toResolveOtherEventsRowSize());
        assertSame(firstEvent, board.getFromResolveOtherEventsRow(0));
    }

    @Test
    void applyEffectDecrementsFoodForAllPlayersAndAppliesPrestigeLossIfFoodIsMissing() {
        Player playerThree = new Player("P3", TotemColor.RED);
        game.addPlayer(playerThree);

        playerOne.setNumFood(1);
        playerOne.getTribe().addHunter(new Hunter(1, 1, false));
        playerOne.getTribe().addHunter(new Hunter(1, 2, false));
        playerOne.getTribe().addArtist(new Artist(1, 3));

        playerTwo.setNumFood(5);
        playerTwo.getTribe().addHunter(new Hunter(1, 4, false));
        playerTwo.getTribe().addArtist(new Artist(1, 5));

        playerThree.setNumFood(1);
        playerThree.getTribe().addArtist(new Artist(1, 6));
        playerThree.getTribe().addArtist(new Artist(1, 7));
        playerThree.getTribe().addBuilding(new SustenanceEventBuilding(1, 1, 1, 101, new SustenanceDiscountArtists()));

        sustenanceEvent.applyEffect(playerOne, game);

        assertEquals(0, playerOne.getNumFood());
        assertEquals(-4, playerOne.getNumPrestigePoints());

        assertEquals(3, playerTwo.getNumFood());
        assertEquals(0, playerTwo.getNumPrestigePoints());

        assertEquals(1, playerThree.getNumFood());
        assertEquals(0, playerThree.getNumPrestigePoints());
    }

    @Test
    void applyEffectKeepsFoodIfBuildingWouldGenerateMoreThanInitialAmount() {
        playerOne.setNumFood(2);
        playerOne.getTribe().addArtist(new Artist(1, 11));
        playerOne.getTribe().addArtist(new Artist(1, 12));
        playerOne.getTribe().addBuilding(new SustenanceEventBuilding(1, 1, 1, 102, new SustenanceDiscountArtists()));

        playerTwo.setNumFood(0);

        sustenanceEvent.applyEffect(playerOne, game);

        assertEquals(2, playerOne.getNumFood());
        assertEquals(0, playerOne.getNumPrestigePoints());
    }

    @Test
    void applyEffectUsesGathererDiscountOnSustenanceCost() {
        playerOne.setNumFood(3);
        playerOne.getTribe().addHunter(new Hunter(1, 20, false));
        playerOne.getTribe().addHunter(new Hunter(1, 21, false));

        Gatherer gathererWithDiscount = new Gatherer(1, 22, 1);
        gathererWithDiscount.applyEffect(playerOne, null);

        playerTwo.setNumFood(0);

        sustenanceEvent.applyEffect(playerOne, game);

        assertEquals(1, playerOne.getNumFood());
        assertEquals(0, playerOne.getNumPrestigePoints());
        assertEquals(0, playerTwo.getNumFood());
        assertEquals(0, playerTwo.getNumPrestigePoints());
    }

    @Test
    void applyEffectUsesTwoGatherersCumulativeDiscountOnSustenanceCost() {
        playerOne.setNumFood(2);
        playerOne.getTribe().addHunter(new Hunter(1, 30, false));
        playerOne.getTribe().addHunter(new Hunter(1, 31, false));

        Gatherer firstGatherer = new Gatherer(1, 32, 1);
        Gatherer secondGatherer = new Gatherer(1, 33, 1);
        firstGatherer.applyEffect(playerOne, null);
        secondGatherer.applyEffect(playerOne, null);

        playerTwo.setNumFood(0);

        sustenanceEvent.applyEffect(playerOne, game);

        assertEquals(0, playerOne.getNumFood());
        assertEquals(0, playerOne.getNumPrestigePoints());
        assertEquals(0, playerTwo.getNumFood());
        assertEquals(0, playerTwo.getNumPrestigePoints());
    }

    @Test
    void applyEffectWithTwoGatherersStillInsufficientFoodAppliesResidualPrestigeLoss() {
        playerOne.setNumFood(1);
        playerOne.getTribe().addHunter(new Hunter(1, 40, false));
        playerOne.getTribe().addHunter(new Hunter(1, 41, false));

        Gatherer firstGatherer = new Gatherer(1, 42, 1);
        Gatherer secondGatherer = new Gatherer(1, 43, 1);
        firstGatherer.applyEffect(playerOne, null);
        secondGatherer.applyEffect(playerOne, null);

        playerTwo.setNumFood(0);

        sustenanceEvent.applyEffect(playerOne, game);

        assertEquals(0, playerOne.getNumFood());
        assertEquals(-2, playerOne.getNumPrestigePoints());
        assertEquals(0, playerTwo.getNumFood());
        assertEquals(0, playerTwo.getNumPrestigePoints());
    }

    @Test
    void applyEffectWithBuildingGeneratingMoreFoodThanNeededReturnsToOriginalAmount() {
        playerOne.setNumFood(1);
        Gatherer gathererWithDiscount = new Gatherer(1, 22, 3);
        gathererWithDiscount.applyEffect(playerOne, null);
        playerOne.getTribe().addBuilding(new SustenanceEventBuilding(1, 1, 1, 103, new SustenanceDiscountGatherers()));
        assertEquals(1, playerOne.getNumFood()); // less food when you buy

        playerTwo.setNumFood(0);

        sustenanceEvent.applyEffect(playerOne, game);

        assertEquals(1, playerOne.getNumFood());
        assertEquals(0, playerOne.getNumPrestigePoints());
    }
}
