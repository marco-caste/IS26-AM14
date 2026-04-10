package it.polimi.ingsw.am14.model.cards.deckCards;

import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.buildings.effects.DoubleShamanicReward;
import it.polimi.ingsw.am14.model.cards.buildings.effects.ShamanicPenaltyShield;
import it.polimi.ingsw.am14.model.cards.buildings.types.ShamanBonusEventBuilding;
import it.polimi.ingsw.am14.model.cards.buildings.types.ShamanMalusEventBuilding;
import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShamanEventCardTest {

    private ShamanEvent shamanEvent;
    private Game game;
    private Board board;
    private Player playerOne;
    private Player playerTwo;

    @BeforeEach
    void setUp() {
        shamanEvent = new ShamanEvent(2, 99);
        game = new Game(2);
        board = game.getBoard();
        playerOne = new Player("P1", TotemColor.WHITE);
        playerTwo = new Player("P2", TotemColor.BLUE);
        game.addPlayer(playerOne);
        game.addPlayer(playerTwo);
    }

    @Test
    void constructorAndGettersAreCorrect() {
        assertEquals(2, shamanEvent.getAge());
        assertEquals(99, shamanEvent.getId());
    }

    @Test
    void isPickableIsAlwaysFalseForEvents() {
        assertFalse(shamanEvent.isPickable(playerOne));
    }

    @Test
    void setupPhaseFourAddsEventToTopRowAndReturnsZero() {
        int result = shamanEvent.setupPhaseFour(board);

        assertEquals(0, result);
        assertEquals(1, board.topDeckableRowSize());
        assertTrue(board.getTopDeckableRow().contains(shamanEvent));
    }

    @Test
    void discardMovesEventToResolveRowAndRemovesFromBottomRowByIndex() {
        Hunter hunter = new Hunter(1, 10, false);
        board.addToBottomDeckableRow(hunter);
        board.addToBottomDeckableRow(shamanEvent);

        shamanEvent.discard(board, 1);

        assertEquals(1, board.bottomDeckableRowSize());
        assertTrue(board.getBottomDeckableRow().contains(hunter));
        assertFalse(board.getBottomDeckableRow().contains(shamanEvent));
        assertEquals(1, board.toResolveOtherEventsRowSize());
        assertSame(shamanEvent, board.getFromResolveOtherEventsRow(0));
    }

    @Test
    void removeEventRemovesLastOtherEventFromBoard() {
        ShamanEvent firstEvent = new ShamanEvent(1, 7);
        board.addToResolvingOtherEventsRow(firstEvent);
        board.addToResolvingOtherEventsRow(shamanEvent);

        shamanEvent.removeEvent(game);

        assertEquals(1, board.toResolveOtherEventsRowSize());
        assertSame(firstEvent, board.getFromResolveOtherEventsRow(0));
    }

    @Test
    void applyEffectWithUniqueMaxAndMinAppliesRewardPenaltyAndBuildingEffects() {
        Player playerThree = new Player("P3", TotemColor.RED);
        game.addPlayer(playerThree);

        playerOne.addToStars(3);
        playerTwo.addToStars(0);
        playerThree.addToStars(1);

        playerOne.getTribe().addBuilding(new ShamanBonusEventBuilding(1, 1, 1, 50, new DoubleShamanicReward()));
        playerTwo.getTribe().addBuilding(new ShamanMalusEventBuilding(1, 1, 1, 51, new ShamanicPenaltyShield()));

        board.addToResolvingOtherEventsRow(shamanEvent);

        shamanEvent.applyEffect(playerOne, game);

        assertEquals(20, playerOne.getNumPrestigePoints());
        assertEquals(0, playerTwo.getNumPrestigePoints());
        assertEquals(0, playerThree.getNumPrestigePoints());
        // Current implementation does not call removeEvent in applyEffect.
        assertEquals(1, board.toResolveOtherEventsRowSize());
    }

    @Test
    void applyEffectWithTieForMaxAndMinGivesNetGainToEveryone() {
        playerOne.addToStars(1);
        playerTwo.addToStars(1);

        shamanEvent.applyEffect(playerOne, game);

        assertEquals(5, playerOne.getNumPrestigePoints());
        assertEquals(5, playerTwo.getNumPrestigePoints());
    }

    @Test
    void applyEffectMinStarWithoutShieldLosesPrestigeAndKeepsFoodUnchanged() {
        playerOne.addToStars(2);
        playerTwo.addToStars(0);
        playerTwo.setNumFood(3);

        shamanEvent.applyEffect(playerOne, game);

        assertEquals(10, playerOne.getNumPrestigePoints());
        assertEquals(-5, playerTwo.getNumPrestigePoints());
        assertEquals(0, playerOne.getNumFood());
        assertEquals(3, playerTwo.getNumFood());
    }
}
