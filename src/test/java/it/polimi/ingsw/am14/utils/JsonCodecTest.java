package it.polimi.ingsw.am14.utils;

import it.polimi.ingsw.am14.model.boardComponents.TrackCard;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.deckCards.DeckCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonCodecTest {

    private ArrayList<DeckCard> deckCards;
    private ArrayList<Building> buildings;
    private Map<Character, TrackCard> trackCards;
    private JsonCodec jsonCodec;

    @BeforeEach
    void setUp() {
        jsonCodec = new JsonCodec();
        deckCards = new ArrayList<>(jsonCodec.readDeckCards("resources/deckCards.json", 5));
        buildings = new ArrayList<>(jsonCodec.readBuildings("resources/buildings.json"));
        trackCards = jsonCodec.readTrackCards("resources/trackCards.json", 5);
    }

    @Test
    void readDeckCards() {
        for (DeckCard c : deckCards) {
            System.out.println(c.toString());
        }
    }

    @Test
    void readBuildings() {
        for (Building b : buildings) {
            System.out.println(b.toString());
        }
    }

    @Test
    void readTrackCards() {
        for (Character c : trackCards.keySet()) {
            System.out.println(trackCards.get(c).toString());
        }
    }
}