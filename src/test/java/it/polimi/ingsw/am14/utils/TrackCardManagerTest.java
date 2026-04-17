package it.polimi.ingsw.am14.utils;

import it.polimi.ingsw.am14.model.boardComponents.TrackCard;
import it.polimi.ingsw.am14.utils.TrackCardManager;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TrackCardManagerTest {

    @Test
    void turnJsonIntoTrackCardMap_twoPlayers_loadsOnlyMin2Cards() throws Exception {
        Map<Character, TrackCard> result = TrackCardManager.turnJsonIntoTrackCardMap(2);

        assertNotNull(result);
        assertEquals(4, result.size());

        assertTrue(result.containsKey('B'));
        assertTrue(result.containsKey('C'));
        assertTrue(result.containsKey('E'));
        assertTrue(result.containsKey('F'));

        assertFalse(result.containsKey('D'));
        assertFalse(result.containsKey('G'));
        assertFalse(result.containsKey('A'));
    }

    @Test
    void turnJsonIntoTrackCardMap_threePlayers_loadsMin2AndMin3Cards() throws Exception {
        Map<Character, TrackCard> result = TrackCardManager.turnJsonIntoTrackCardMap(3);

        assertNotNull(result);
        assertEquals(5, result.size());

        assertTrue(result.containsKey('B'));
        assertTrue(result.containsKey('C'));
        assertTrue(result.containsKey('D'));
        assertTrue(result.containsKey('E'));
        assertTrue(result.containsKey('F'));

        assertFalse(result.containsKey('G'));
        assertFalse(result.containsKey('A'));
    }

    @Test
    void turnJsonIntoTrackCardMap_fivePlayers_loadsAllCards() throws Exception {
        Map<Character, TrackCard> result = TrackCardManager.turnJsonIntoTrackCardMap(5);

        assertNotNull(result);
        assertEquals(7, result.size());

        assertTrue(result.containsKey('A'));
        assertTrue(result.containsKey('B'));
        assertTrue(result.containsKey('C'));
        assertTrue(result.containsKey('D'));
        assertTrue(result.containsKey('E'));
        assertTrue(result.containsKey('F'));
        assertTrue(result.containsKey('G'));
    }
}
