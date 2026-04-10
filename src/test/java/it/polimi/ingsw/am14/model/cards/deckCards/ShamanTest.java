package it.polimi.ingsw.am14.model.cards.deckCards;

import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShamanTest {

    private Player player;
    private Shaman shamanTwoStars;
    private Shaman shamanOneStar;

    @BeforeEach
    public void setUp() {
        player = new Player("P1", TotemColor.WHITE);
        shamanTwoStars = new Shaman(1, 15, 2);
        shamanOneStar = new Shaman(2, 16, 1);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals(1, shamanTwoStars.getAge());
        assertEquals(15, shamanTwoStars.getId());
    }

    @Test
    public void testApplyEffectAddsShamanAndStars() {
        shamanTwoStars.applyEffect(player, null);

        assertEquals(1, player.getTribe().getShamansSize());
        assertEquals(2, player.getNumStars());
    }

    @Test
    public void testApplyEffectAccumulatesStarsAcrossMultipleShamans() {
        shamanTwoStars.applyEffect(player, null);
        shamanOneStar.applyEffect(player, null);

        assertEquals(2, player.getTribe().getShamansSize());
        assertEquals(3, player.getNumStars());
    }

    @Test
    public void testApplyEffectIgnoresNullContextCurrentBehavior() {
        assertDoesNotThrow(() -> shamanTwoStars.applyEffect(player, null));
    }

    @Test
    public void testApplyEffectWithNullPlayerThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> shamanTwoStars.applyEffect(null, null));
    }
}

