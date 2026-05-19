package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.model.cards.buildings.effects.Flat25Prestige;
import it.polimi.ingsw.am14.model.cards.buildings.effects.ScoringBuilders;
import it.polimi.ingsw.am14.model.cards.buildings.types.EndGameBuilding;
import it.polimi.ingsw.am14.model.cards.deckCards.*;
import it.polimi.ingsw.am14.exceptions.WrongStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EndGameStateTest {

    Game gameStub;
    EndGameState endGameStateStub;
    Player p1;
    Player p2;

    @BeforeEach
    void setUp() {
        gameStub = new Game(2,1);
        endGameStateStub = new EndGameState();
        gameStub.setCurrentState(endGameStateStub);

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
                    endGameStateStub.placeTotem(gameStub, new Player("player", TotemColor.WHITE), 'a');
                }
        );
    }

    @Test
    void pickCard() {
        assertThrows(
                WrongStateException.class,
                () -> {
                    endGameStateStub.pickCard(gameStub, new Player("player", TotemColor.WHITE), Optional.of(new Shaman(1, 2, 3)), 2);
                }
        );
    }

    @Test
    void onPlayerConnection() {
        assertThrows(
                WrongStateException.class,
                () -> {
                    endGameStateStub.onPlayerConnection(gameStub,"player", "WHITE");
                }
        );
    }

    //These tests work only if database is active
/*
    @Test
    void endRoundAppliesEndGameVisitorEffects() throws SQLException {
        p1.getTribe().addBuilder(new Builder(1, 31, 0, 3));
        p1.getTribe().addBuilder(new Builder(1, 32, 0, 3));
        p1.getTribe().addBuilding(new EndGameBuilding(0, 3, 1, 33, new ScoringBuilders()));

        endGameStateStub.endRound(gameStub);

        assertEquals(17, p1.getNumPrestigePoints());
    }

    @Test
    void endRoundAddsPrestigeFromBuildersBuildingsArtistsAndInventors() throws SQLException {
        p1.getTribe().addBuilder(new Builder(1, 41, 0, 3));
        p1.getTribe().addBuilder(new Builder(1, 42, 0, 4));
        p1.getTribe().addBuilding(new EndGameBuilding(0, 25, 1, 43, new Flat25Prestige()));

        p1.getTribe().addArtist(new Artist(1, 44));
        p1.getTribe().addArtist(new Artist(1, 45));
        p1.getTribe().addArtist(new Artist(1, 46));

        p1.getTribe().addInventor(new Inventor(1, 47, InventionType.BOAT.toString()));
        p1.getTribe().addInventor(new Inventor(1, 48, InventionType.BOAT.toString()));
        p1.getTribe().addInventor(new Inventor(1, 49, InventionType.LEATHER.toString()));

        endGameStateStub.endRound(gameStub);

        assertEquals(48, p1.getNumPrestigePoints());
    }

    @Test
    void endRoundProcessesDeckablesAndClearsEventQueues() throws SQLException {
        gameStub.getBoard().setTopDeckableRow(new ArrayList<>(List.of(
                new Hunter(1, 51, false),
                new SustenanceEvent(1, 52)
        )));

        endGameStateStub.endRound(gameStub);

        assertEquals(0, gameStub.getBoard().topDeckableRowSize());
        assertEquals(0, gameStub.getBoard().bottomDeckableRowSize());
        assertEquals(0, gameStub.getBoard().toResolveOtherEventsRowSize());
        assertEquals(0, gameStub.getBoard().toResolveSustenanceEventsRowSize());
    }

    @Test
    void endRoundProcessesOtherEventsQueue() throws SQLException {
        p1.getTribe().addHunter(new Hunter(1, 60, false));
        gameStub.getBoard().setTopDeckableRow(new ArrayList<>(List.of(
                new HuntEvent(1, 61)
        )));

        endGameStateStub.endRound(gameStub);

        assertEquals(2, p1.getNumFood());  // 2 food because p1 is in first position on TurnOrderCard and
                                                    // EndGameState.endRound() resolves the TurnOrderCard's bonus/malus
        assertEquals(1, p1.getNumPrestigePoints());
        assertEquals(0, gameStub.getBoard().toResolveOtherEventsRowSize());
    }

    @Test
    void endRoundRankingComparatorCoversPrestigeGreaterBranch() {
        p1.addToPrestigePoints(5);
        p2.setNumFood(2);

        assertDoesNotThrow(() -> endGameStateStub.endRound(gameStub));
    }

    @Test
    void endRoundRankingComparatorCoversFoodGreaterBranchWhenPrestigeTied() {
        p1.setNumFood(5);
        p2.setNumFood(2);

        assertDoesNotThrow(() -> endGameStateStub.endRound(gameStub));
    }

    @Test
    void endRoundRankingComparatorCoversFoodLowerBranchWhenPrestigeTied() {
        p1.setNumFood(0);
        p2.setNumFood(10);

        assertDoesNotThrow(() -> endGameStateStub.endRound(gameStub));
    }

    @Test
    void endRoundRankingComparatorCoversFoodEqualBranchWhenPrestigeTied() {
        p1.setNumFood(0);
        p2.setNumFood(2);

        assertDoesNotThrow(() -> endGameStateStub.endRound(gameStub));
    }

    // TODO: optional test for complete sorting of players at the end of endRound()

 */
}