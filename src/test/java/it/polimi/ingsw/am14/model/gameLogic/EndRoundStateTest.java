package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.model.cards.deckCards.DeckCard;
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
}