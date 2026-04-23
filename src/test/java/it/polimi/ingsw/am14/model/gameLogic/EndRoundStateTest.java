package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.model.cards.deckCards.Shaman;
import it.polimi.ingsw.am14.model.cards.deckCards.Hunter;
import it.polimi.ingsw.am14.exceptions.WrongStateException;
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

    // TODO: this is a game situation, 9 rounds need to be elapsed, so it will be tested in integration tests with Game
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
}