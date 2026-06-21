package it.polimi.ingsw.am14.model.boardComponents;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.utils.Codec;
import it.polimi.ingsw.am14.utils.JsonCodec;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * <p>
 *     OffersPath is a class that represents the "path of offers" in the game. It contains a map of TrackCards, where each TrackCard is associated with a character.
 * </p>
 */
public class OffersPath {
    /**
     * <p>
     *     offersPath is a map that associates a character with a TrackCard. The character represents the position of the TrackCard in the path of offers.
     * </p>
     */
    private final Map<Character, TrackCard> offersPath;

    /**
     * <p>
     *     calls the Codec parser, builds the offersPath according to the number of players
     * </p>
     * @param numPlayers between 2 and 5
     */
    public OffersPath(int numPlayers) {
        Codec jsonCodec = new JsonCodec();

        this.offersPath = jsonCodec.readTrackCards("src/main/resources/trackCards.json", numPlayers);
    }

    /**
     * <p>
     *     constructor used by the Codec
     * </p>
     * @param offersPath not null, the map of TrackCards to be used to build the OffersPath object
     */
    @JsonCreator
    public OffersPath(
            @JsonProperty("offersPath") Map<Character, TrackCard> offersPath) {
        this.offersPath = offersPath;
    }


    public Set<Character> offersPathKeySet() {
        return new HashSet<>(this.offersPath.keySet());     // defensive copy
    }

    public TrackCard offersPathGet(Character trackCardName){
        return this.offersPath.get(trackCardName);
    }

    /**
     * <p>
     *     Finds the position of a player in the offersPath based on their nickname.
     * </p>
     * @param nickname the nickname of the player to find
     * @return an Optional containing the character representing the player's position, or an empty Optional if not found
     */
    public Optional<Character> findPlayerPosition(String nickname) {
        for (Map.Entry<Character, TrackCard> entry : offersPath.entrySet()) {
            if (entry.getValue().getPlayerNickname() != null && entry.getValue().getPlayerNickname().equals(nickname)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }
}
