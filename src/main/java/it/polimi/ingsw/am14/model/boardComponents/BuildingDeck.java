package it.polimi.ingsw.am14.model.boardComponents;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.utils.Codec;
import it.polimi.ingsw.am14.utils.JsonCodec;

import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.am14.utils.Constants.initialBuildings;

/**
 * <p>
 *     represents the deck of buildings used in the game
 * </p>
 */
public class BuildingDeck {
    /**
     * the buildingDecks map contains the decks of buildings for each age
     * the key is the age (1, 2, or 3) and the value is the list of buildings for that age
     */
    private final Map<Integer, ArrayList<Building>> buildingDecks;

    // organizes the number of buildings to use in the game according
    // to the number of players: initialBuildings[numPlayers - 1][age - 1]
    // is the number of age "age" buildings for a game of "numPlayers" players


    /**
     * <p>
     *     builds a building deck for the given number of players. Will read the buildings from memory and shuffle them, then will take the right number of buildings for each age according to the number of players.
     * </p>
     * @param numPlayers the number of players in the game
     */
    public BuildingDeck(int numPlayers){
        this.buildingDecks = new HashMap<>();
        this.buildingDecks.put(1, new ArrayList<>());
        this.buildingDecks.put(2, new ArrayList<>());
        this.buildingDecks.put(3, new ArrayList<>());
        // reads values from JSON data file, puts Buildings in the corresponding eras;
        Codec jsonCodec = new JsonCodec();

        Map<Integer, ArrayList<Building>> completeDecks = new HashMap<>();

        completeDecks.put(1, jsonCodec.readBuildings("src/main/resources/Buildings.json").stream().
                filter(b -> b.getAge() == 1).
                collect(Collectors.toCollection(ArrayList::new)));
        completeDecks.put(2, jsonCodec.readBuildings("src/main/resources/Buildings.json").stream().
                filter(b -> b.getAge() == 2).
                collect(Collectors.toCollection(ArrayList::new)));
        completeDecks.put(3, jsonCodec.readBuildings("src/main/resources/Buildings.json").stream().
                filter(b -> b.getAge() == 3).
                collect(Collectors.toCollection(ArrayList::new)));


        // shuffles the decks separately:
        Collections.shuffle(completeDecks.get(1));
        Collections.shuffle(completeDecks.get(2));
        Collections.shuffle(completeDecks.get(3));

        // pushes the shuffled cards in the Queue;
        for (int i = 1; i <= 3; i++) {      // cycles through ages 1 to 3
            for (int j = 0; j < initialBuildings[numPlayers - 2][i - 1]; j++) {
                // initialBuildings[numPlayers - 1][i - 1] is the number of building cards
                // given numPlayers, for the current era i;
               this.buildingDecks.get(i).add(completeDecks.get(i).get(j));
            }
        }
    }

    /**
     * <p>
     *     builds a building deck from the given map of building decks, used for deserialization
     * </p>
     * @param buildingDecks a map of building decks, where the keys are the ages and the values are the corresponding lists of buildings
     */
    @JsonCreator
    public BuildingDeck(
            @JsonProperty("buildingDecks") Map<Integer, ArrayList<Building>> buildingDecks) {
        this.buildingDecks = buildingDecks;
    }

    // SIZE OF THE BUILDING ARRAY AT AGE "age"
    public int buildingDeckSize(int age) {
        return this.buildingDecks.get(age).size();
    }

    // GETTER FOR THE BUILDING DECKS
    protected ArrayList<Building> getBuildings(int age) {
        return this.buildingDecks.get(age);
    }

    // GETS A BUILDING (EFFECTIVELY FINAL OBJECT)

    /**
     *
     * @param age the age of the building to retrieve, must be between 1 and 3, included
     * @param position the position in the deck of the building to retrieve, must be between 0 and the size of the building deck for the given age, excluded
     * @return the building of age "age" in position "position"
     */
    public Building getBuilding(int age, int position) {
        return this.buildingDecks.get(age).get(position);
    }
}
