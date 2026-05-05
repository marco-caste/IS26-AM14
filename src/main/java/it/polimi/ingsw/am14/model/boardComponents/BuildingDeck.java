package it.polimi.ingsw.am14.model.boardComponents;


import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.utils.JsonCodec;

import java.util.*;
import java.util.stream.Collectors;

public class BuildingDeck {
    private final Map<Integer, ArrayList<Building>> buildingDecks;

    // organizes the number of buildings to use in the game according
    // to the number of players: initialBuildings[numPlayers - 1][age - 1]
    // is the number of age "age" buildings for a game of "numPlayers" players
    private static final int[][] initialBuildings = {
            {1, 2, 3}, {2, 2, 4}, {2, 3, 4}, {2, 3, 5}
    };

    /**
     * <p>
     *     builds a deck of buildings
     *     - retrieves data from Json using BuildingManager class
     *     - shuffles the buildings separately
     *     - puts the correct number of buildings per age in this.buildingDecks,
     *          according to this.initialBuildings
     * </p>
     * @param numPlayers between 2 and 5
     */
    public BuildingDeck(int numPlayers){
        this.buildingDecks = new HashMap<>();
        this.buildingDecks.put(1, new ArrayList<>());
        this.buildingDecks.put(2, new ArrayList<>());
        this.buildingDecks.put(3, new ArrayList<>());
        // reads values from JSON data file, puts Buildings in the corresponding eras;
        JsonCodec jsonCodec = new JsonCodec();

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

    // SIZE OF THE BUILDING ARRAY AT AGE age

    /**
     *
     * @param age between 1 and 3, included
     * @return the size of the building deck of age "age"
     */
    public int buildingDeckSize(int age) {
        return this.buildingDecks.get(age).size();
    }

    // GETS AN ARRAYLIST OF BUILDINGS (PROTECTED, CANNOT BE CALLED FROM OUTSIDE)

    /**
     *
     * @param age between 1 and 3, included
     * @return the list of buildings of age "age"
     */
    protected ArrayList<Building> getBuildings(int age) {
        return this.buildingDecks.get(age);
    }

    // GETS A BUILDING (EFFECTIVELY FINAL OBJECT)

    /**
     *
     * @param age between 1 and 3, included
     * @param position between 0 and this.buildingDeckSize(age) - 1
     * @return the building of age "age" in position "position"
     */
    public Building getBuilding(int age, int position) {
        return this.buildingDecks.get(age).get(position);
    }
}
