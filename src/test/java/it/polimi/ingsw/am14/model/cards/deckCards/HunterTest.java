package it.polimi.ingsw.am14.model.cards.deckCards;

import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HunterTest {

    private Player player;
    private Hunter hunterWithHam;
    private Hunter hunterWithoutHam;

    @BeforeEach
    public void setUp() {
        player = new Player("P1", TotemColor.WHITE);
        hunterWithHam = new Hunter(1, 10, true);
        hunterWithoutHam = new Hunter(1, 11, false);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals(1, hunterWithHam.getAge());
        assertEquals(10, hunterWithHam.getId());
    }

    @Test
    public void testIsPickableAlwaysTrue() {
        assertTrue(hunterWithHam.isPickable(player));
        assertTrue(hunterWithoutHam.isPickable(player));
    }

    @Test
    public void testApplyEffectWithHamAddsHunterAndFood() {
        hunterWithHam.applyEffect(player, null);

        assertEquals(1, player.getTribe().getHuntersSize());
        assertEquals(1, player.getNumFood());
    }

    @Test
    public void testApplyEffectWithoutHamAddsHunterButNoFood() {
        player.setNumFood(2);

        hunterWithoutHam.applyEffect(player, null);

        assertEquals(1, player.getTribe().getHuntersSize());
        assertEquals(2, player.getNumFood());
    }

    @Test
    public void testApplyEffectWithNullPlayerThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> hunterWithHam.applyEffect(null, null));
    }
}

