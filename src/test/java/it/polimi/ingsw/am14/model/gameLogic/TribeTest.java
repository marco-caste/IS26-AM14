package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.model.cards.deckCards.*;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.buildings.types.*;
import it.polimi.ingsw.am14.model.cards.buildings.effects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TribeTest {

    private Tribe tribe;

    @BeforeEach
    public void setUp() {
        tribe = new Tribe();
    }

    // CONSTRUCTOR TESTS

    @Test
    public void testTribeConstructor() {
        // Verify tribe is created with all empty lists
        assertNotNull(tribe);
        assertEquals(0, tribe.getHuntersSize());
        assertEquals(0, tribe.getInventorsSize());
        assertEquals(0, tribe.getShamansSize());
        assertEquals(0, tribe.getGatherersSize());
        assertEquals(0, tribe.getArtistsSize());
        assertEquals(0, tribe.getBuildersSize());
        assertEquals(0, tribe.getBuildings().size());
    }

    // HUNTERS TESTS

    @Test
    public void testAddHunter() {
        Hunter hunter = new Hunter(2, 1, true);
        tribe.addHunter(hunter);
        assertEquals(1, tribe.getHuntersSize());
    }

    @Test
    public void testAddMultipleHunters() {
        Hunter hunter1 = new Hunter(2, 1, true);
        Hunter hunter2 = new Hunter(3, 2, false);
        tribe.addHunter(hunter1);
        tribe.addHunter(hunter2);
        assertEquals(2, tribe.getHuntersSize());
    }

    @Test
    public void testSetHunters() {
        ArrayList<Hunter> hunters = new ArrayList<>();
        Hunter hunter1 = new Hunter(2, 1, true);
        Hunter hunter2 = new Hunter(3, 2, false);
        hunters.add(hunter1);
        hunters.add(hunter2);
        tribe.setHunters(hunters);
        assertEquals(2, tribe.getHuntersSize());
    }

    @Test
    public void testSetHuntersEmpty() {
        tribe.addHunter(new Hunter(2, 1, true));
        ArrayList<Hunter> emptyHunters = new ArrayList<>();
        tribe.setHunters(emptyHunters);
        assertEquals(0, tribe.getHuntersSize());
    }

    // INVENTORS TESTS

    @Test
    public void testAddInventor() {
        Inventor inventor = new Inventor(2, 1, InventionType.LEATHER.toString());
        tribe.addInventor(inventor);
        assertEquals(1, tribe.getInventorsSize());
    }

    @Test
    public void testAddMultipleInventors() {
        Inventor inventor1 = new Inventor(2, 1, InventionType.LEATHER.toString());
        Inventor inventor2 = new Inventor(3, 2, InventionType.BOAT.toString());
        tribe.addInventor(inventor1);
        tribe.addInventor(inventor2);
        assertEquals(2, tribe.getInventorsSize());
    }

    @Test
    public void testSetInventors() {
        ArrayList<Inventor> inventors = new ArrayList<>();
        Inventor inventor1 = new Inventor(2, 1, InventionType.LEATHER.toString());
        Inventor inventor2 = new Inventor(3, 2, InventionType.BOAT.toString());
        inventors.add(inventor1);
        inventors.add(inventor2);
        tribe.setInventors(inventors);
        assertEquals(2, tribe.getInventorsSize());
    }

    @Test
    public void testGetInventors() {
        Inventor inventor = new Inventor(2, 1, InventionType.LEATHER.toString());
        tribe.addInventor(inventor);
        ArrayList<Inventor> inventors = tribe.getInventors();
        assertEquals(1, inventors.size());
        assertEquals(inventor, inventors.getFirst());
    }

    @Test
    public void testGetMultipleInventors() {
        Inventor inventor1 = new Inventor(2, 1, InventionType.LEATHER.toString());
        Inventor inventor2 = new Inventor(3, 2, InventionType.BOAT.toString());
        Inventor inventor3 = new Inventor(4, 3, InventionType.ARROW.toString());
        tribe.addInventor(inventor1);
        tribe.addInventor(inventor2);
        tribe.addInventor(inventor3);
        ArrayList<Inventor> inventors = tribe.getInventors();
        assertEquals(3, inventors.size());
        assertEquals(inventor1, inventors.get(0));
        assertEquals(inventor2, inventors.get(1));
        assertEquals(inventor3, inventors.get(2));
    }

    // SHAMANS TESTS

    @Test
    public void testAddShaman() {
        Shaman shaman = new Shaman(2, 1, 3);
        tribe.addShaman(shaman);
        assertEquals(1, tribe.getShamansSize());
    }

    @Test
    public void testAddMultipleShamans() {
        Shaman shaman1 = new Shaman(2, 1, 3);
        Shaman shaman2 = new Shaman(3, 2, 2);
        tribe.addShaman(shaman1);
        tribe.addShaman(shaman2);
        assertEquals(2, tribe.getShamansSize());
    }

    @Test
    public void testSetShamans() {
        ArrayList<Shaman> shamans = new ArrayList<>();
        Shaman shaman1 = new Shaman(2, 1, 3);
        Shaman shaman2 = new Shaman(3, 2, 2);
        shamans.add(shaman1);
        shamans.add(shaman2);
        tribe.setShamans(shamans);
        assertEquals(2, tribe.getShamansSize());
    }

    // GATHERERS TESTS

    @Test
    public void testAddGatherer() {
        Gatherer gatherer = new Gatherer(2, 1, 2);
        tribe.addGatherer(gatherer);
        assertEquals(1, tribe.getGatherersSize());
    }

    @Test
    public void testAddMultipleGatherers() {
        Gatherer gatherer1 = new Gatherer(2, 1, 2);
        Gatherer gatherer2 = new Gatherer(3, 2, 1);
        tribe.addGatherer(gatherer1);
        tribe.addGatherer(gatherer2);
        assertEquals(2, tribe.getGatherersSize());
    }

    @Test
    public void testSetGatherers() {
        ArrayList<Gatherer> gatherers = new ArrayList<>();
        Gatherer gatherer1 = new Gatherer(2, 1, 2);
        Gatherer gatherer2 = new Gatherer(3, 2, 1);
        gatherers.add(gatherer1);
        gatherers.add(gatherer2);
        tribe.setGatherers(gatherers);
        assertEquals(2, tribe.getGatherersSize());
    }

    // ARTISTS TESTS

    @Test
    public void testAddArtist() {
        Artist artist = new Artist(2, 1);
        tribe.addArtist(artist);
        assertEquals(1, tribe.getArtistsSize());
    }

    @Test
    public void testAddMultipleArtists() {
        Artist artist1 = new Artist(2, 1);
        Artist artist2 = new Artist(3, 2);
        tribe.addArtist(artist1);
        tribe.addArtist(artist2);
        assertEquals(2, tribe.getArtistsSize());
    }

    @Test
    public void testSetArtists() {
        ArrayList<Artist> artists = new ArrayList<>();
        Artist artist1 = new Artist(2, 1);
        Artist artist2 = new Artist(3, 2);
        artists.add(artist1);
        artists.add(artist2);
        tribe.setArtists(artists);
        assertEquals(2, tribe.getArtistsSize());
    }

    // BUILDERS TESTS

    @Test
    public void testAddBuilder() {
        Builder builder = new Builder(2, 1, 2, 5);
        tribe.addBuilder(builder);
        assertEquals(1, tribe.getBuildersSize());
    }

    @Test
    public void testAddMultipleBuilders() {
        Builder builder1 = new Builder(2, 1, 2, 5);
        Builder builder2 = new Builder(3, 2, 3, 4);
        tribe.addBuilder(builder1);
        tribe.addBuilder(builder2);
        assertEquals(2, tribe.getBuildersSize());
    }

    @Test
    public void testSetBuilders() {
        ArrayList<Builder> builders = new ArrayList<>();
        Builder builder1 = new Builder(2, 1, 2, 5);
        Builder builder2 = new Builder(3, 2, 3, 4);
        builders.add(builder1);
        builders.add(builder2);
        tribe.setBuilders(builders);
        assertEquals(2, tribe.getBuildersSize());
    }

    @Test
    public void testGetBuilders() {
        Builder builder = new Builder(2, 1, 2, 5);
        tribe.addBuilder(builder);
        ArrayList<Builder> builders = tribe.getBuilders();
        assertEquals(1, builders.size());
        assertEquals(builder, builders.getFirst());
    }

    @Test
    public void testGetMultipleBuilders() {
        Builder builder1 = new Builder(2, 1, 2, 5);
        Builder builder2 = new Builder(3, 2, 3, 4);
        Builder builder3 = new Builder(4, 3, 1, 6);
        tribe.addBuilder(builder1);
        tribe.addBuilder(builder2);
        tribe.addBuilder(builder3);
        ArrayList<Builder> builders = tribe.getBuilders();
        assertEquals(3, builders.size());
        assertEquals(builder1, builders.get(0));
        assertEquals(builder2, builders.get(1));
        assertEquals(builder3, builders.get(2));
    }

    // BUILDINGS TESTS

    @Test
    public void testGetBuildingsInitiallyEmpty() {
        ArrayList<Building> buildings = tribe.getBuildings();
        assertEquals(0, buildings.size());
        assertNotNull(buildings);
    }

    @Test
    public void testGetBuildingsAfterSet() {
        ArrayList<Building> emptyBuildings = new ArrayList<>();
        tribe.setBuildings(emptyBuildings);
        ArrayList<Building> buildings = tribe.getBuildings();
        assertEquals(0, buildings.size());
        assertNotNull(buildings);
    }

    @Test
    public void testAddBuilding() {
        // Test with ExtraEndTurnFood effect
        PassiveBuilding building = new PassiveBuilding(5, 3, 2, 1, new ExtraEndTurnFood());
        tribe.addBuilding(building);
        ArrayList<Building> buildings = tribe.getBuildings();
        assertEquals(1, buildings.size());
        assertEquals(building, buildings.getFirst());
    }

    @Test
    public void testAddMultipleBuildings() {
        // Test with different effect strategies
        PassiveBuilding building1 = new PassiveBuilding(5, 3, 2, 1, new ExtraShamanicStars());
        PassiveBuilding building2 = new PassiveBuilding(7, 4, 3, 2, new HuntExtraFoodAndPrestige());
        tribe.addBuilding(building1);
        tribe.addBuilding(building2);
        ArrayList<Building> buildings = tribe.getBuildings();
        assertEquals(2, buildings.size());
        assertEquals(building1, buildings.get(0));
        assertEquals(building2, buildings.get(1));
    }

    @Test
    public void testAddBuildingWithAllEffectTypes() {
        // Test with all different effect types
        PassiveBuilding b1 = new PassiveBuilding(3, 2, 1, 1, new InventorPairFood());
        PassiveBuilding b2 = new PassiveBuilding(4, 2, 1, 2, new PaintExtraFood());
        PassiveBuilding b3 = new PassiveBuilding(5, 3, 2, 3, new PlayerExtraDraft());
        PassiveBuilding b4 = new PassiveBuilding(6, 4, 2, 4, new EndGameSetPrestige());
        PassiveBuilding b5 = new PassiveBuilding(7, 5, 3, 5, new SetCompletionFood());
        PassiveBuilding b6 = new PassiveBuilding(8, 6, 3, 6, new DoubleBuilderPrestige());
        PassiveBuilding b7 = new PassiveBuilding(6, 3, 2, 7, new ScoringArtists());
        PassiveBuilding b8 = new PassiveBuilding(6, 3, 2, 8, new ScoringBuilders());
        PassiveBuilding b9 = new PassiveBuilding(6, 3, 2, 9, new ScoringGatherers());
        PassiveBuilding b10 = new PassiveBuilding(6, 3, 2, 10, new ScoringHunters());
        
        tribe.addBuilding(b1);
        tribe.addBuilding(b2);
        tribe.addBuilding(b3);
        tribe.addBuilding(b4);
        tribe.addBuilding(b5);
        tribe.addBuilding(b6);
        tribe.addBuilding(b7);
        tribe.addBuilding(b8);
        tribe.addBuilding(b9);
        tribe.addBuilding(b10);
        
        ArrayList<Building> buildings = tribe.getBuildings();
        assertEquals(10, buildings.size());
        assertEquals(b1, buildings.get(0));
        assertEquals(b5, buildings.get(4));
        assertEquals(b10, buildings.get(9));
    }

    @Test
    public void testAddBuildingWithMoreEffects() {
        // Test with remaining effect types
        PassiveBuilding b1 = new PassiveBuilding(6, 3, 2, 11, new ScoringInventors());
        PassiveBuilding b2 = new PassiveBuilding(6, 3, 2, 12, new ScoringShamans());
        PassiveBuilding b3 = new PassiveBuilding(5, 3, 2, 13, new Flat25Prestige());
        PassiveBuilding b4 = new PassiveBuilding(8, 4, 3, 14, new DoubleShamanicReward());
        PassiveBuilding b5 = new PassiveBuilding(7, 4, 3, 15, new SustenanceDiscountArtists());
        PassiveBuilding b6 = new PassiveBuilding(7, 4, 3, 16, new SustenanceDiscountGatherers());
        PassiveBuilding b7 = new PassiveBuilding(7, 4, 3, 17, new SustenanceDiscountInventors());
        PassiveBuilding b8 = new PassiveBuilding(8, 5, 3, 18, new ShamanicPenaltyShield());
        
        tribe.addBuilding(b1);
        tribe.addBuilding(b2);
        tribe.addBuilding(b3);
        tribe.addBuilding(b4);
        tribe.addBuilding(b5);
        tribe.addBuilding(b6);
        tribe.addBuilding(b7);
        tribe.addBuilding(b8);
        
        ArrayList<Building> buildings = tribe.getBuildings();
        assertEquals(8, buildings.size());
        assertEquals(b1, buildings.get(0));
        assertEquals(b8, buildings.get(7));
    }

    @Test
    public void testSetBuildings() {
        ArrayList<Building> buildings = new ArrayList<>();
        tribe.setBuildings(buildings);
        assertEquals(0, tribe.getBuildings().size());
        
        // After setting empty list, verify it stays empty
        ArrayList<Building> newBuildings = tribe.getBuildings();
        assertEquals(0, newBuildings.size());
    }

    @Test
    public void testGetBuildingsReturnsCopy() {
        // Verify that getBuildings returns a defensive copy
        ArrayList<Building> buildings1 = tribe.getBuildings();
        ArrayList<Building> buildings2 = tribe.getBuildings();
        
        // They should have the same content but be different objects
        assertEquals(buildings1.size(), buildings2.size());
        assertNotSame(buildings1, buildings2);
    }

    // COMBINATION TESTS

    @Test
    public void testAllAttributesAfterMultipleOperations() {
        // Add multiple character types
        Hunter hunter = new Hunter(2, 1, true);
        Inventor inventor = new Inventor(2, 2, InventionType.LEATHER.toString());
        Shaman shaman = new Shaman(2, 3, 4);
        Gatherer gatherer = new Gatherer(2, 4, 2);
        Artist artist = new Artist(2, 5);
        Builder builder = new Builder(2, 6, 3, 5);

        tribe.addHunter(hunter);
        tribe.addInventor(inventor);
        tribe.addShaman(shaman);
        tribe.addGatherer(gatherer);
        tribe.addArtist(artist);
        tribe.addBuilder(builder);

        // Verify all attributes
        assertEquals(1, tribe.getHuntersSize());
        assertEquals(1, tribe.getInventorsSize());
        assertEquals(1, tribe.getShamansSize());
        assertEquals(1, tribe.getGatherersSize());
        assertEquals(1, tribe.getArtistsSize());
        assertEquals(1, tribe.getBuildersSize());
    }

    @Test
    public void testIndependentCharacterTypes() {
        // Verify that adding one character type doesn't affect others
        Hunter hunter = new Hunter(2, 1, true);
        Inventor inventor = new Inventor(2, 2, InventionType.LEATHER.toString());
        tribe.addHunter(hunter);
        tribe.addInventor(inventor);

        assertEquals(1, tribe.getHuntersSize());
        assertEquals(1, tribe.getInventorsSize());
        assertEquals(0, tribe.getShamansSize());
        assertEquals(0, tribe.getGatherersSize());
        assertEquals(0, tribe.getArtistsSize());
        assertEquals(0, tribe.getBuildersSize());
    }

    @Test
    public void testSettersOverwrite() {
        // Test that setters overwrite previous values
        ArrayList<Hunter> hunters1 = new ArrayList<>();
        hunters1.add(new Hunter(2, 1, true));
        tribe.setHunters(hunters1);
        assertEquals(1, tribe.getHuntersSize());

        ArrayList<Hunter> hunters2 = new ArrayList<>();
        hunters2.add(new Hunter(3, 2, false));
        hunters2.add(new Hunter(4, 3, true));
        tribe.setHunters(hunters2);
        assertEquals(2, tribe.getHuntersSize());
    }

    @Test
    public void testGettersReturnCopies() {
        // Verify that getters return defensive copies
        tribe.addInventor(new Inventor(2, 1, InventionType.LEATHER.toString()));
        ArrayList<Inventor> inventors1 = tribe.getInventors();
        ArrayList<Inventor> inventors2 = tribe.getInventors();

        // They should have the same content but be different objects
        assertEquals(inventors1.size(), inventors2.size());
        assertNotSame(inventors1, inventors2);
    }

    @Test
    public void testMultipleCharactersOfSameType() {
        // Test adding multiple characters of the same type
        for (int i = 1; i <= 5; i++) {
            tribe.addHunter(new Hunter(2, i, true));
        }
        assertEquals(5, tribe.getHuntersSize());

        for (int i = 1; i <= 3; i++) {
            tribe.addArtist(new Artist(2, i));
        }
        assertEquals(3, tribe.getArtistsSize());

        // Verify others are still empty
        assertEquals(0, tribe.getInventorsSize());
        assertEquals(0, tribe.getShamansSize());
        assertEquals(0, tribe.getGatherersSize());
        assertEquals(0, tribe.getBuildersSize());
    }

}