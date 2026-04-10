package it.polimi.ingsw.am14.model.cards.deckCards;

import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GathererTest {

    private Player player;
    private Gatherer gatherer;
    private Gatherer gatherer2;

    @BeforeEach
    public void setUp() {
        player = new Player("P1", TotemColor.WHITE);
        gatherer = new Gatherer(1, 6, 3);
        gatherer2 = new Gatherer(1, 7, 3);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals(1, gatherer.getAge());
        assertEquals(6, gatherer.getId());
    }

    @Test
    public void testApplyEffectAddsGathererAndDiscount() {
        gatherer.applyEffect(player, null);

        assertEquals(1, player.getTribe().getGatherersSize());
        assertEquals(3, player.getTotalGatheringDiscount());
    }

    @Test
    public void testApplyEffectAccumulatesDiscountAcrossMultipleGatherers() {
        gatherer.applyEffect(player, null);
        gatherer2.applyEffect(player, null);

        assertEquals(2, player.getTribe().getGatherersSize());
        assertEquals(6, player.getTotalGatheringDiscount());
    }

    @Test
    public void testApplyEffectIgnoresNullContextCurrentBehavior() {
        assertDoesNotThrow(() -> gatherer.applyEffect(player, null));
    }

    @Test
    public void testApplyEffectWithNullPlayerThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> gatherer.applyEffect(null, null));
    }
}

