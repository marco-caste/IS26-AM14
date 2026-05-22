package it.polimi.ingsw.am14.model.boardComponents;

import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TrackCardTest {

    private Player player;
    private TrackCard foodTrackCard;
    private TrackCard topPickTrackCard;
    private TrackCard bottomPickTrackCard;

    @BeforeEach
    public void setUp() {
        player = new Player("P1", TotemColor.WHITE);
        foodTrackCard = new TrackCard('a', 0, 0, 3, null);
        topPickTrackCard = new TrackCard('f', 2, 0, 0, null);
        bottomPickTrackCard = new TrackCard('b', 0, 1, 0, null);
    }

    @Test
    public void testConstructorInitializesPlayerTotemToNull() {
        assertNull(foodTrackCard.getPlayerTotem());
    }

    @Test
    public void testSetAndGetPlayerTotem() {
        bottomPickTrackCard.setPlayerTotem(player);

        assertEquals(player, bottomPickTrackCard.getPlayerTotem());
    }

    @Test
    public void testEffectSetsPicksAndAddsFood() {
        player.setNumFood(1);
        foodTrackCard.setPlayerTotem(player);

        foodTrackCard.effect();

        assertEquals(0, player.getNumTopPicksLeft());
        assertEquals(0, player.getNumBottomPicksLeft());
        assertEquals(4, player.getNumFood());
    }

    @Test
    public void testEffectOverwritesExistingPicks() {
        player.setNumTopPicksLeft(0);
        player.setNumFood(1);
        topPickTrackCard.setPlayerTotem(player);

        topPickTrackCard.effect();

        assertEquals(2, player.getNumTopPicksLeft());
        assertEquals(0, player.getNumBottomPicksLeft());
        assertEquals(1, player.getNumFood());
    }

    @Test
    public void testEffectWithoutPlayerTotemThrowsException() {
        assertThrows(NullPointerException.class, bottomPickTrackCard::effect);
    }
}
