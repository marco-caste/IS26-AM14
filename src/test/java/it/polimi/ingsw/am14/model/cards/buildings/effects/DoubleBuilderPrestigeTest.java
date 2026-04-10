package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.cards.deckCards.Builder;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DoubleBuilderPrestigeTest {

    private Player player;
    private DoubleBuilderPrestige effect;
    private Builder firstBuilder;
    private Builder secondBuilder;

    @BeforeEach
    void setUp() {
        player = new Player("P1", TotemColor.WHITE);
        effect = new DoubleBuilderPrestige();
        firstBuilder = new Builder(1, 10, 1, 4);
        secondBuilder = new Builder(2, 20, 2, 7);
    }

    @Test
    void applyEffectWhenNoBuildersDoesNotChangePrestige() {
        effect.applyEffect(player, null);

        assertEquals(0, player.getNumPrestigePoints());
    }

    @Test
    void applyEffectWhenBuildersArePresentAddsAllTheirPrestigePoints() {
        player.getTribe().addBuilder(firstBuilder);
        player.getTribe().addBuilder(secondBuilder);

        effect.applyEffect(player, null);

        assertEquals(11, player.getNumPrestigePoints());
    }

    @Test
    void applyEffectAccumulatesOnExistingPrestigePoints() {
        player.addToPrestigePoints(5);
        player.getTribe().addBuilder(firstBuilder);

        effect.applyEffect(player, null);

        assertEquals(9, player.getNumPrestigePoints());
    }

    @Test
    void applyEffectWithNullGameDoesNotThrow() {
        player.getTribe().addBuilder(firstBuilder);

        assertDoesNotThrow(() -> effect.applyEffect(player, null));
    }

    @Test
    void applyEffectWithNullPlayerThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> effect.applyEffect(null, null));
    }
}

