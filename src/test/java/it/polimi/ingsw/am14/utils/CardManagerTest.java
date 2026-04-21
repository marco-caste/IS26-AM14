package it.polimi.ingsw.am14.utils;

import it.polimi.ingsw.am14.model.cards.deckCards.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

class CardManagerTest {

    @Test
    void turnJsonIntoRawMap_twoPlayers_containsExpectedStructureAndCards() throws Exception {
        Map<Integer, Map<String, Map<String, ArrayList<Map<String, Object>>>>> raw = CardManager.turnJsonIntoRawMap(2);

        assertNotNull(raw);
        assertTrue(raw.containsKey(1));
        assertTrue(raw.containsKey(2));
        assertTrue(raw.containsKey(3));

        // era 1 -> Characters -> Artist : for 2 players there are exactly 3 artists (id 0,1,2)
        ArrayList<Map<String, Object>> era1Artists = raw.get(1).get("Characters").get("Artist");
        assertNotNull(era1Artists);
        assertEquals(3, era1Artists.size());

        // era 1 -> Events -> HuntEvent : exactly 1 card for 2 players
        ArrayList<Map<String, Object>> era1HuntEvents = raw.get(1).get("Events").get("HuntEvent");
        assertNotNull(era1HuntEvents);
        assertEquals(1, era1HuntEvents.size());
    }

    @Test
    void turnJsonIntoRawMap_fivePlayers_mergesAllMinPlayerEntriesCorrectly() throws Exception {
        Map<Integer, Map<String, Map<String, ArrayList<Map<String, Object>>>>> raw = CardManager.turnJsonIntoRawMap(5);

        // For era 1, Characters/Artist:
        // minPlayers=2 -> 3 cards (0,1,2)
        // minPlayers=3 -> 1 card (61)
        // minPlayers=4 -> 1 card (72)
        // minPlayers=5 -> 0 cards
        // TOTAL = 5
        ArrayList<Map<String, Object>> era1Artists = raw.get(1).get("Characters").get("Artist");
        assertEquals(5, era1Artists.size());

        // For era 3, Events/PaintEvent:
        // only minPlayers=2 contributes 1 card (id 60)
        ArrayList<Map<String, Object>> era3PaintEvents = raw.get(3).get("Events").get("PaintEvent");
        assertEquals(1, era3PaintEvents.size());
    }

    @Test
    void instantiateCardMap_createsConcreteCardInstancesWithCorrectType() throws Exception {
        Map<Integer, Map<String, Map<String, ArrayList<Map<String, Object>>>>> raw = CardManager.turnJsonIntoRawMap(2);
        Map<Integer, Map<String, Map<String, ArrayList<DeckCard>>>> cardMap = CardManager.instantiateCardMap(raw);

        assertNotNull(cardMap);
        assertTrue(cardMap.containsKey(1));

        ArrayList<DeckCard> artists = cardMap.get(1).get("Characters").get("Artist");
        assertFalse(artists.isEmpty());
        assertInstanceOf(Artist.class, artists.get(0));

        ArrayList<DeckCard> builders = cardMap.get(1).get("Characters").get("Builder");
        assertFalse(builders.isEmpty());
        assertInstanceOf(Builder.class, builders.get(0));

        ArrayList<DeckCard> huntEvents = cardMap.get(1).get("Events").get("HuntEvent");
        assertFalse(huntEvents.isEmpty());
        assertInstanceOf(HuntEvent.class, huntEvents.get(0));
    }

    @Test
    void buildDecksFromCardMap_buildsOneDeckPerEraWithExpectedCardCount_twoPlayers() throws Exception {
        Map<Integer, Map<String, Map<String, ArrayList<Map<String, Object>>>>> raw = CardManager.turnJsonIntoRawMap(2);
        Map<Integer, Map<String, Map<String, ArrayList<DeckCard>>>> cardMap = CardManager.instantiateCardMap(raw);
        Map<Integer, ArrayList<DeckCard>> decks = CardManager.buildDecksFromCardMap(cardMap);

        assertNotNull(decks);
        assertTrue(decks.containsKey(1));
        assertTrue(decks.containsKey(2));
        assertTrue(decks.containsKey(3));

        // From JSON for minPlayers=2 only:
        // Era 1: Characters 18 + Events 4 = 22
        // Era 2: Characters 17 + Events 4 = 21
        // Era 3: Characters 17 + Events 2 = 19
        assertEquals(21, decks.get(1).size());
        assertEquals(21, decks.get(2).size());
        assertEquals(19, decks.get(3).size());
    }

    @Test
    void buildCard_invalidCharacterName_throwsIllegalArgumentException() throws Exception {
        Method m = CardManager.class.getDeclaredMethod("buildCard", String.class, String.class, Map.class);
        m.setAccessible(true);

        InvocationTargetException ex = assertThrows(
                InvocationTargetException.class,
                () -> m.invoke(null, "Characters", "UnknownCharacter", Map.of("age", 1, "id", 999))
        );

        assertInstanceOf(IllegalArgumentException.class, ex.getCause());
        assertTrue(ex.getCause().getMessage().contains("Invalid card type"));
    }

    @Test
    void buildCard_invalidEventName_throwsIllegalArgumentException() throws Exception {
        Method m = CardManager.class.getDeclaredMethod("buildCard", String.class, String.class, Map.class);
        m.setAccessible(true);

        InvocationTargetException ex = assertThrows(
                InvocationTargetException.class,
                () -> m.invoke(null, "Events", "UnknownEvent", Map.of("age", 1, "id", 999))
        );

        assertInstanceOf(IllegalArgumentException.class, ex.getCause());
        assertTrue(ex.getCause().getMessage().contains("Invalid card type"));
    }

    @Test
    void buildCard_invalidType_throwsIllegalArgumentException() throws Exception {
        Method m = CardManager.class.getDeclaredMethod("buildCard", String.class, String.class, Map.class);
        m.setAccessible(true);

        InvocationTargetException ex = assertThrows(
                InvocationTargetException.class,
                () -> m.invoke(null, "NotAType", "Anything", Map.of("age", 1, "id", 999))
        );

        assertInstanceOf(IllegalArgumentException.class, ex.getCause());
        assertTrue(ex.getCause().getMessage().contains("Tipologia non gestita"));
    }
    @Test
    void instantiateCardMap_createsAllConcreteCardClasses() throws Exception {
        Map<Integer, Map<String, Map<String, ArrayList<Map<String, Object>>>>> raw = CardManager.turnJsonIntoRawMap(5);
        Map<Integer, Map<String, Map<String, ArrayList<DeckCard>>>> cardMap = CardManager.instantiateCardMap(raw);

        Set<Class<?>> created = new HashSet<>();

        for (Map<String, Map<String, ArrayList<DeckCard>>> typeMap : cardMap.values()) {
            for (Map<String, ArrayList<DeckCard>> nameMap : typeMap.values()) {
                for (ArrayList<DeckCard> cards : nameMap.values()) {
                    for (DeckCard c : cards) {
                        created.add(c.getClass());
                    }
                }
            }
        }

        // Characters
        assertTrue(created.contains(Artist.class));
        assertTrue(created.contains(Builder.class));
        assertTrue(created.contains(Gatherer.class));
        assertTrue(created.contains(Hunter.class));
        assertTrue(created.contains(Inventor.class));
        assertTrue(created.contains(Shaman.class));

        // Events
        assertTrue(created.contains(HuntEvent.class));
        assertTrue(created.contains(PaintEvent.class));
        assertTrue(created.contains(ShamanEvent.class));
        assertTrue(created.contains(SustenanceEvent.class));
    }

    @Test
    void instantiateCardMap_inventorStringIsConvertedToInventionType() throws Exception {
        Map<Integer, Map<String, Map<String, ArrayList<Map<String, Object>>>>> raw = CardManager.turnJsonIntoRawMap(5);
        Map<Integer, Map<String, Map<String, ArrayList<DeckCard>>>> cardMap = CardManager.instantiateCardMap(raw);

        List<InventionType> inventorTypes = new ArrayList<>();

        for (Map<String, Map<String, ArrayList<DeckCard>>> eraMap : cardMap.values()) {
            Map<String, ArrayList<DeckCard>> chars = eraMap.get("Characters");
            if (chars == null) continue;

            ArrayList<DeckCard> inventors = chars.get("Inventor");
            if (inventors == null) continue;

            for (DeckCard d : inventors) {
                assertInstanceOf(Inventor.class, d);
                Inventor inv = (Inventor) d;

                // Sostituisci getInventionType() col getter reale della tua classe Inventor
                inventorTypes.add(inv.getInvention());
            }
        }

        assertFalse(inventorTypes.isEmpty());

        // Dal JSON dovrebbero esserci diversi valori
        assertTrue(inventorTypes.contains(InventionType.LEATHER));
        assertTrue(inventorTypes.contains(InventionType.BOAT));
        assertTrue(inventorTypes.contains(InventionType.ARROW));
        assertTrue(inventorTypes.contains(InventionType.BREAD));
    }

    @Test
    void instantiateCardMap_expectedCountsPerCardName_forTwoPlayers() throws Exception {
        Map<Integer, Map<String, Map<String, ArrayList<Map<String, Object>>>>> raw = CardManager.turnJsonIntoRawMap(2);
        Map<Integer, Map<String, Map<String, ArrayList<DeckCard>>>> cardMap = CardManager.instantiateCardMap(raw);

        // Era 1, per 2 giocatori (dal tuo JSON)
        Map<String, ArrayList<DeckCard>> chars = cardMap.get(1).get("Characters");
        assertEquals(3, chars.get("Artist").size());
        assertEquals(3, chars.get("Builder").size());
        assertEquals(2, chars.get("Gatherer").size());
        assertEquals(3, chars.get("Hunter").size());
        assertEquals(4, chars.get("Inventor").size());
        assertEquals(2, chars.get("Shaman").size());

        Map<String, ArrayList<DeckCard>> events = cardMap.get(1).get("Events");
        assertEquals(1, events.get("HuntEvent").size());
        assertEquals(1, events.get("PaintEvent").size());
        assertEquals(1, events.get("ShamanEvent").size());
        assertEquals(1, events.get("SustenanceEvent").size());
    }
}