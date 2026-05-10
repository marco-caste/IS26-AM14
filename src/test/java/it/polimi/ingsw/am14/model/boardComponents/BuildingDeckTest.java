package it.polimi.ingsw.am14.model.boardComponents;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuildingDeckTest {

    private BuildingDeck buildingDeck;
    private final static int numPlayers = 3;
    private static final int[][] initialBuildings = {
            {1, 2, 3}, {2, 2, 4}, {2, 3, 4}, {2, 3, 5}
    };

    @BeforeEach
    void setUp() {
        buildingDeck = new BuildingDeck(numPlayers);
    }

    @Test
    void buildingDeckSize() {
        assertEquals(
                initialBuildings[numPlayers - 2][0],
                buildingDeck.buildingDeckSize(1)
        );
        assertEquals(
                initialBuildings[numPlayers - 2][1],
                buildingDeck.buildingDeckSize(2)
        );
        assertEquals(
                initialBuildings[numPlayers - 2][2],
                buildingDeck.buildingDeckSize(3)
        );
    }
}