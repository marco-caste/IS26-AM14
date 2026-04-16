package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.exceptions.IllegalSetupException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameFactoryTest {

    static final int numPlayers = 3;
    static Game currentGame;

    @Test
    void setup() {
        // not 100% coverage because of constructor not being called

        assertThrows(IllegalSetupException.class, () -> {
            GameFactory.setup(6);
        });
        assertThrows(IllegalSetupException.class, () -> {
            GameFactory.setup(1);
        });

        try {
            assertNotSame(null, GameFactory.setup(numPlayers));
        } catch (IllegalSetupException e) {
            throw new RuntimeException(e);
        }

        try {
            currentGame = GameFactory.setup(numPlayers);
        } catch (IllegalSetupException e) {
            throw new RuntimeException(e);
        }

        assertEquals(numPlayers, currentGame.getNumPlayers());

    }
}