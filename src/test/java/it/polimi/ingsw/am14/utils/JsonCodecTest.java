package it.polimi.ingsw.am14.utils;

import it.polimi.ingsw.am14.events.EndGameEvent;
import it.polimi.ingsw.am14.events.EndRoundEvent;
import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;
import it.polimi.ingsw.am14.events.GameStartedEvent;
import it.polimi.ingsw.am14.events.TotemPlacedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonCodecTest {

    private JsonCodec jsonCodec;

    @BeforeEach
    void setUp() {
        jsonCodec = new JsonCodec();
    }

    @Test
    void parseEvent_shouldDeserializeExceptionEvent() {
        String json = """
                {
                  "type": "ExceptionEvent",
                  "description": "Errore di test"
                }
                """;

        Event parsed = jsonCodec.parseEvent(json);

        assertInstanceOf(ExceptionEvent.class, parsed);
        assertEquals(ExceptionEvent.class, parsed.getClass());
        assertEquals("Errore di test", parsed.getDescription());
    }

    @Test
    void parseEvent_shouldDeserializeGameStartedEvent() {
        String json = """
                {
                  "type": "GameStartedEvent",
                  "description": "The game has started",
                  "playerOrder": ["Alice", "Bob", "Charlie"],
                  "totemOrder": ["RED", "BLUE", "GREEN"]
                }
                """;

        Event parsed = jsonCodec.parseEvent(json);

        assertInstanceOf(GameStartedEvent.class, parsed);
        assertEquals(GameStartedEvent.class, parsed.getClass());
        assertEquals("The game has started", parsed.getDescription());

        GameStartedEvent event = (GameStartedEvent) parsed;

        assertEquals(List.of("Alice", "Bob", "Charlie"),
                readField(event, "playerOrder", List.class));
        assertEquals(List.of("RED", "BLUE", "GREEN"),
                readField(event, "totemOrder", List.class));
    }

    @Test
    void parseEvent_shouldDeserializeTotemPlacedEvent() {
        String json = """
                {
                  "type": "TotemPlacedEvent",
                  "description": "Player Alice has placed their totem on track card A",
                  "nickname": "Alice",
                  "trackCardName": "A",
                  "removedTotemIndex": 2
                }
                """;

        Event parsed = jsonCodec.parseEvent(json);

        assertInstanceOf(TotemPlacedEvent.class, parsed);
        assertEquals(TotemPlacedEvent.class, parsed.getClass());
        assertEquals("Player Alice has placed their totem on track card A", parsed.getDescription());

        TotemPlacedEvent event = (TotemPlacedEvent) parsed;

        assertEquals("Alice", readField(event, "playerNickname", String.class));
        assertEquals('A', readField(event, "trackCardName", Character.class));
        assertEquals(2, readField(event, "removedTotemIndex", Integer.class));
    }

    @Test
    void parseEvent_shouldDeserializeEndRoundEvent() {
        String json = """
                {
                  "type": "EndRoundEvent",
                  "board": [
                    [1, 2],
                    [3, 4],
                    [5, 6],
                    [7, 8]
                  ],
                  "playersAttributes": {
                    "Alice": [2, 0],
                    "Bob": [3, 5]
                  },
                  "description": "End of round, the board has been updated"
                }
                """;

        Event parsed = jsonCodec.parseEvent(json);

        assertInstanceOf(EndRoundEvent.class, parsed);
        assertEquals(EndRoundEvent.class, parsed.getClass());
        assertEquals("End of round, the board has been updated", parsed.getDescription());

        EndRoundEvent event = (EndRoundEvent) parsed;

        assertEquals(List.of(
                        List.of(1, 2),
                        List.of(3, 4),
                        List.of(5, 6),
                        List.of(7, 8)),
                event.getBoard());

        assertEquals(Map.of(
                        "Alice", List.of(2, 0),
                        "Bob", List.of(3, 5)),
                event.getPlayerAttributes());
    }

    @Test
    void parseEvent_shouldDeserializeEndGameEvent() {
        String json = """
                {
                  "type": "EndGameEvent",
                  "description": "End of game, the board has been updated, the final ranking has been calculated",
                  "board": [
                    [10, 11],
                    [12, 13],
                    [14, 15],
                    [16, 17]
                  ],
                  "playersAttributes": {
                    "Alice": [10, 20],
                    "Bob": [5, 15]
                  }
                }
                """;

        Event parsed = jsonCodec.parseEvent(json);

        assertInstanceOf(EndGameEvent.class, parsed);
        assertEquals(EndGameEvent.class, parsed.getClass());
        assertEquals(
                "End of game, the board has been updated, the final ranking has been calculated",
                parsed.getDescription()
        );

        EndGameEvent event = (EndGameEvent) parsed;

        assertEquals(List.of(
                        List.of(10, 11),
                        List.of(12, 13),
                        List.of(14, 15),
                        List.of(16, 17)),
                readField(event, "board", List.class));

        assertEquals(Map.of(
                        "Alice", List.of(10, 20),
                        "Bob", List.of(5, 15)),
                readField(event, "playersAttributes", Map.class));
    }

    private static <T> T readField(Object target, String fieldName, Class<T> fieldType) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return fieldType.cast(field.get(target));
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Impossibile leggere il campo '" + fieldName + "'", e);
        }
    }
}