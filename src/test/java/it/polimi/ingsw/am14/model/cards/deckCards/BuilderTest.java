package it.polimi.ingsw.am14.model.cards.deckCards;

import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuilderTest {

    Builder builderStub;
    Player playerStub;

    @BeforeEach
    void setUp() {
        builderStub = new Builder(2, 22, 3, 4);
        playerStub = new Player("playerStub", TotemColor.RED);
    }

    @Test
    void getPrestigePoints() {
        assertEquals(4, builderStub.getPrestigePoints());
    }

    @Test
    void applyEffect() {
        builderStub.applyEffect(playerStub, null);

        assertEquals(playerStub.getTribe().getBuilders().getFirst(), builderStub);
        assertEquals(1, playerStub.getTribe().getBuildersSize());
        assertEquals(3, playerStub.getTotalBuildingDiscount());
    }
}