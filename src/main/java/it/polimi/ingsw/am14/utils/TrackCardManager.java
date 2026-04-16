package it.polimi.ingsw.am14.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am14.model.boardComponents.TrackCard;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TrackCardManager {


    public static Map<Character, TrackCard> turnJsonIntoTrackCardMap(int numPlayers) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        // Read JSON file
        String json = new String(Files.readAllBytes(Paths.get("resources/TrackCards.json")));

        // tempMap: "minPlayers" -> list of raw track cards
        Map<String, List<Map<String, Object>>> tempMap =
                objectMapper.readValue(
                        json,
                        new TypeReference<Map<String, List<Map<String, Object>>>>() {}
                );

        // Final result: card letter -> TrackCard
        Map<Character, TrackCard> result = new HashMap<>();

        // Iterate by minPlayers
        for (Map.Entry<String, List<Map<String, Object>>> entry : tempMap.entrySet()) {
            int minPlayers = Integer.parseInt(entry.getKey());

            // Skip cards requiring more players than current game
            if (minPlayers > numPlayers) {
                continue;
            }

            // Build cards
            for (Map<String, Object> raw : entry.getValue()) {
                String name = raw.get("name").toString();
                int numPickTop = (int) (raw.get("numPickTop"));
                int numPickBottom = (int) (raw.get("numPickBottom"));
                int numFood = (int) (raw.get("numFood"));
                char key = name.charAt(0);

                TrackCard card = new TrackCard(key, numPickTop, numPickBottom, numFood);

                result.put(key, card);
            }
        }
        return result;
    }
}
