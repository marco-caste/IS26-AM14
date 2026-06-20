package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.boardComponents.TurnOrderCard;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.deckCards.DeckCard;
import it.polimi.ingsw.am14.model.cards.deckCards.EventCard;
import it.polimi.ingsw.am14.exceptions.WrongStateException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WaitingRoomStateTest {

    private Game buildWaitingGame(int numPlayers) {
        return new Game(numPlayers, 1);
    }

    @Test
    public void testOnPlayerConnectionAddsPlayerAndKeepsWaitingStateWhenNotFull() throws WrongStateException {
        Game game = buildWaitingGame(3);
        WaitingRoomState waitingState = new WaitingRoomState();
        Player p1 = new Player("P1", TotemColor.WHITE);

        waitingState.onPlayerConnection(game, "P1", "WHITE");

        assertEquals(1, game.playersSize());
        //assert p1.equals(game.getPlayers().get(0));
        //assert p1.equals(game.getBoard().getTurnOrderCard().getPlayer(0));
        assertInstanceOf(WaitingRoomState.class, game.getCurrentState());
    }

    @Test
    public void testOnPlayerConnectionTransitionsToPositionStateWhenRoomIsFull() {
        Game game = buildWaitingGame(3);
        WaitingRoomState waitingState = new WaitingRoomState();
        Player p1 = new Player("P1", TotemColor.WHITE);
        Player p2 = new Player("P2", TotemColor.RED);
        Player p3 = new Player("P3", TotemColor.BLUE);

        waitingState.onPlayerConnection(game, "P1", "WHITE");
        waitingState.onPlayerConnection(game, "P2", "RED");
        waitingState.onPlayerConnection(game, "P3", "BLUE");

        assertEquals(3, game.playersSize());
        assertInstanceOf(PositionState.class, game.getCurrentState());

        Set<Player> expectedPlayers = Set.of(p1, p2, p3);
        Set<Player> actualPlayers = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            actualPlayers.add(game.getBoard().getTurnOrderCard().getPlayer(i));
        }
        //assertEquals(expectedPlayers, actualPlayers);
    }
}

