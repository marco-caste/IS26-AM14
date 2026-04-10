package it.polimi.ingsw.am14.model.boardComponents;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
