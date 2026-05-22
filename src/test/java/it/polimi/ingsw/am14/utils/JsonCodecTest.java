package it.polimi.ingsw.am14.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am14.controller.GameController;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.utils.JsonCodec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JsonCodecTest {

    private final JsonCodec jsonCodec = new JsonCodec();

    @TempDir
    Path tempDir;

    // ===== TESTS SAVE/LOAD =====

    @Test
    void writeSaveData_shouldWriteToProvidedFilePath() throws IOException {
        Map<String, Integer> nicknameToGameId = new HashMap<>();
        nicknameToGameId.put("Alice", 1);
        nicknameToGameId.put("Bob", 2);

        LobbyController lobby = new LobbyController(nicknameToGameId, new HashMap<>(), 0);

        Path saveFile = tempDir.resolve("custom_save.json");
        jsonCodec.writeSaveData(lobby, saveFile.toString());

        assertTrue(Files.exists(saveFile), "Il file deve essere creato nel path passato al metodo");
        assertTrue(Files.size(saveFile) > 0, "Il file non deve essere vuoto");
    }

    @Test
    void writeSaveData_shouldSerializeExpectedLobbyFields() throws IOException {
        Map<String, Integer> nicknameToGameId = new HashMap<>();
        nicknameToGameId.put("Alice", 7);
        nicknameToGameId.put("Bob", 7);

        LobbyController lobby = new LobbyController(nicknameToGameId, new HashMap<>(), 0);

        Path saveFile = tempDir.resolve("save.json");
        jsonCodec.writeSaveData(lobby, saveFile.toString());

        String rawJson = Files.readString(saveFile);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> json = mapper.readValue(rawJson, new TypeReference<>() {});

        assertTrue(json.containsKey("nicknameToGameId"));
        assertTrue(json.containsKey("gameIdToGameController"));

        Map<?, ?> nickMap = (Map<?, ?>) json.get("nicknameToGameId");
        assertEquals(2, nickMap.size());
        assertEquals(7, ((Number) nickMap.get("Alice")).intValue());
    }

    @Test
    void readSaveData_shouldDeserializeLobbyController() {
        Map<String, Integer> nicknameToGameId = new HashMap<>();
        nicknameToGameId.put("Alice", 3);
        nicknameToGameId.put("Bob", 5);

        LobbyController original = new LobbyController(nicknameToGameId, new HashMap<>(), 0);

        Path saveFile = tempDir.resolve("roundtrip.json");
        jsonCodec.writeSaveData(original, saveFile.toString());

        LobbyController restored = jsonCodec.readSaveData(saveFile.toString());

        assertNotNull(restored);
        assertEquals(Optional.of(3), restored.getGameIdByNickname("Alice"));
        assertEquals(Optional.of(5), restored.getGameIdByNickname("Bob"));
        assertTrue(restored.getGameIdToGameController().isEmpty(),
                "La mappa dei game controller deve rimanere vuota nel test minimale");
    }

    @Test
    void writeSaveData_shouldThrowRuntimeException_whenPathIsInvalid() {
        LobbyController lobby = new LobbyController(new HashMap<>(), new HashMap<>(), 0);

        Path invalidPath = tempDir.resolve("missing_folder").resolve("save.json");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> jsonCodec.writeSaveData(lobby, invalidPath.toString()));

        assertTrue(ex.getMessage().contains("[JsonCodec] Failed to write save data to file:"));
    }

    @Test
    void readSaveData_shouldThrowRuntimeException_whenFileDoesNotExist() {
        Path missingFile = tempDir.resolve("does_not_exist.json");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> jsonCodec.readSaveData(missingFile.toString()));

        assertTrue(ex.getMessage().contains("[JsonCodec] Failed to read save data from file:"));
    }
}