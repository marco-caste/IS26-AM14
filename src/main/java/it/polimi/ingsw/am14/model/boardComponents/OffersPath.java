package it.polimi.ingsw.am14.model.boardComponents;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.utils.JsonCodec;

import java.util.HashSet;
import java.util.Map;
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

        this.offersPath = jsonCodec.readTrackCards("src/main/resources/TrackCards.json", numPlayers);
    }

    @JsonCreator
    public OffersPath(
            @JsonProperty("offersPath") Map<Character, TrackCard> offersPath) {
        this.offersPath = offersPath;
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
}
