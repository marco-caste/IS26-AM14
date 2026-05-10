package it.polimi.ingsw.am14.utils;

import it.polimi.ingsw.am14.controller.GameController;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.events.EndGameEvent;
import it.polimi.ingsw.am14.events.EndRoundEvent;
import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;
import it.polimi.ingsw.am14.events.GameStartedEvent;
import it.polimi.ingsw.am14.events.TotemPlacedEvent;
import it.polimi.ingsw.am14.model.gameLogic.Game;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonCodecTest {

    private JsonCodec jsonCodec;

    @BeforeEach
    void setUp() {
        jsonCodec = new JsonCodec();
    }

    // ===== STUB CLASSES (no Mockito needed) =====

    private static class StubGame extends Game {
        public StubGame(int gameId) {
            super(2, gameId);  // Crea Game con 2 giocatori
        }
    }

    private static class StubGameController {
        private final StubGame game;

        public StubGameController(StubGame game) {
            this.game = game;
        }

        public Game getGame() {
            return game;
        }
    }

    // ===== TESTS =====

    @Test
    void writeSaveData_shouldWriteGameDataToFile() throws IOException {
        // Setup
        StubGame game = new StubGame(1);
        StubGameController gameController = new StubGameController(game);
        Map<Integer, StubGameController> gameMap = Map.of(1, gameController);

        // Create a wrapper that implements the interface
        LobbyController lobbyController = new LobbyController(new HashMap<>()) {
            @Override
            public Map<Integer, GameController> getGameIdToGameController() {
                return gameMap.entrySet().stream()
                        .collect(java.util.stream.Collectors.toMap(
                                Map.Entry::getKey,
                                e -> new GameController(e.getValue().getGame())
                        ));
            }
        };

        String filePath = "test_save.json";

        // Execute
        jsonCodec.writeSaveData(lobbyController, filePath);

        // Verify - controlla che il file sia stato creato
        assertTrue(Files.exists(Path.of("save.json")),
                "Il file save.json dovrebbe essere stato creato");
    }

    @Test
    void writeSaveData_shouldSerializeMultipleGames() throws IOException {
        // Setup
        StubGame game1 = new StubGame(1);
        StubGame game2 = new StubGame(2);
        StubGameController gameController1 = new StubGameController(game1);
        StubGameController gameController2 = new StubGameController(game2);
        Map<Integer, StubGameController> gameMap = Map.of(1, gameController1, 2, gameController2);

        LobbyController lobbyController = new LobbyController(new HashMap<>()) {
            @Override
            public Map<Integer, GameController> getGameIdToGameController() {
                return gameMap.entrySet().stream()
                        .collect(java.util.stream.Collectors.toMap(
                                Map.Entry::getKey,
                                e -> new GameController(e.getValue().getGame())
                        ));
            }
        };

        String filePath = "test_save.json";

        // Execute
        jsonCodec.writeSaveData(lobbyController, filePath);

        // Verify
        String fileContent = Files.readString(Path.of("save.json"));
        assertNotNull(fileContent, "Il contenuto del file non dovrebbe essere nullo");
        assertFalse(fileContent.isEmpty(), "Il contenuto del file non dovrebbe essere vuoto");
        assertTrue(fileContent.contains("[1,2]") || fileContent.contains("[1, 2]"),
                "Il file dovrebbe contenere i game IDs");
    }

    @Test
    void writeSaveData_shouldHandleEmptyGameMap() throws IOException {
        // Setup
        LobbyController lobbyController = new LobbyController(new HashMap<>()) {
            @Override
            public Map<Integer, GameController> getGameIdToGameController() {
                return new HashMap<>();
            }
        };

        String filePath = "test_save.json";

        // Execute
        jsonCodec.writeSaveData(lobbyController, filePath);

        // Verify - verifica che il file sia stato creato anche con mappa vuota
        assertTrue(Files.exists(Path.of("save.json")),
                "Il file dovrebbe essere creato anche con nessuna partita");

        String fileContent = Files.readString(Path.of("save.json"));
        assertTrue(fileContent.contains("[]"),
                "Il file dovrebbe contenere un array vuoto");
    }

    @Test
    void writeSaveData_shouldCreateValidJsonFormat() throws IOException {
        // Setup
        StubGame game = new StubGame(5);
        StubGameController gameController = new StubGameController(game);
        Map<Integer, StubGameController> gameMap = Map.of(5, gameController);

        LobbyController lobbyController = new LobbyController(new HashMap<>()) {
            @Override
            public Map<Integer, GameController> getGameIdToGameController() {
                return gameMap.entrySet().stream()
                        .collect(java.util.stream.Collectors.toMap(
                                Map.Entry::getKey,
                                e -> new GameController(e.getValue().getGame())
                        ));
            }
        };

        // Execute
        jsonCodec.writeSaveData(lobbyController, "test_save.json");

        // Verify
        String fileContent = Files.readString(Path.of("save.json"));
        assertTrue(fileContent.startsWith("[") && fileContent.contains("{"),
                "Il file dovrebbe contenere JSON valido");
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

    // Cleanup dopo i test
    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Path.of("save.json"));
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