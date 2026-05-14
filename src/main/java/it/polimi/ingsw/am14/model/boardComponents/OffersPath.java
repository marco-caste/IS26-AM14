package it.polimi.ingsw.am14.model.boardComponents;

import it.polimi.ingsw.am14.utils.JsonCodec;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class OffersPath {
    private final Map<Character, TrackCard> offersPath;

    /**
     * <p>
     *     calls the JSON parser, builds the offersPath according to the number of players
     * </p>
     * @param numPlayers between 2 and 5
     */
    public OffersPath(int numPlayers) {
        JsonCodec jsonCodec = new JsonCodec();

        this.offersPath = jsonCodec.readTrackCards("src/main/resources/trackCards.json", numPlayers);
    }

    /**
     *
     * @return a defensive copy of this.offersPath.keySet()
     */
    public Set<Character> offersPathKeySet() {
        return new HashSet<>(this.offersPath.keySet());     // defensive copy
    }

    /**
     *
     * @param trackCardName not null
     * @return this.offersPath.get(trackCardName) (if there is no such TrackCard, returns null)
     */
    public TrackCard offersPathGet(Character trackCardName){
        return this.offersPath.get(trackCardName);
    }

    public Optional<Character> findPlayerPosition(String nickname) {
        for (Map.Entry<Character, TrackCard> entry : offersPath.entrySet()) {
            if (entry.getValue().getPlayerNickname() != null && entry.getValue().getPlayerNickname().equals(nickname)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }
}
