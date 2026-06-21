package it.polimi.ingsw.am14.model.boardComponents;

import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class OffersPathTest {

    private OffersPath offersPath;

    @BeforeEach
    public void setUp() {
        offersPath = new OffersPath(2);
    }

    @Test
    public void testConstructorBuildsOffersPathForTwoPlayers() {
        assertNotNull(offersPath);
        assertNotNull(offersPath.offersPathKeySet());
        assertFalse(offersPath.offersPathKeySet().isEmpty());
    }

    @Test
    public void testOffersPathKeySetContainsExpectedKeysForTwoPlayers() {
        Set<Character> keySet = offersPath.offersPathKeySet();

        assertEquals(4, keySet.size());
        assertTrue(keySet.contains('B'));
        assertTrue(keySet.contains('C'));
        assertTrue(keySet.contains('E'));
        assertTrue(keySet.contains('F'));
    }

    @Test
    public void testOffersPathGetReturnsTrackCardForExistingKey() {
        TrackCard trackCard = offersPath.offersPathGet('B');

        assertNotNull(trackCard);
        assertEquals('B', trackCard.getName());
    }

    @Test
    public void testOffersPathGetReturnsNullForMissingKey() {
        assertNull(offersPath.offersPathGet('Z'));
    }

    @Test
    public void testOffersPathKeySetReturnsDefensiveCopy() {
        Set<Character> keySetCopy = offersPath.offersPathKeySet();
        keySetCopy.clear();

        Set<Character> newKeySet = offersPath.offersPathKeySet();
        assertEquals(4, newKeySet.size());
        assertTrue(newKeySet.contains('B'));
    }

    @Test
    public void testOffersPathGetIsConsistentWithKeySet() {
        Set<Character> keySet = offersPath.offersPathKeySet();

        for (Character key : keySet) {
            assertNotNull(offersPath.offersPathGet(key));
        }
    }

    @Test
    public void testFindPlayerPositionReturnsCorrectPositionAndEmptyWhenMissing() {
        Player p1 = new Player("P1", TotemColor.WHITE);
        Player p2 = new Player("P2", TotemColor.RED);

        Map<Character, TrackCard> customMap = new HashMap<>();
        customMap.put('B', new TrackCard('b', 0, 1, 0, p1));
        customMap.put('C', new TrackCard('c', 1, 0, 0, null));
        customMap.put('E', new TrackCard('e', 1, 1, 0, p2));

        OffersPath customOffersPath = new OffersPath(customMap);

        assertEquals(Optional.of('B'), customOffersPath.findPlayerPosition("P1"));
        assertEquals(Optional.of('E'), customOffersPath.findPlayerPosition("P2"));
        assertTrue(customOffersPath.findPlayerPosition("Unknown").isEmpty());
    }

}
