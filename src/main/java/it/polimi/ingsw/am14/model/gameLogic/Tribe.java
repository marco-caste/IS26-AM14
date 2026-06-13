package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.model.cards.deckCards.*;
import it.polimi.ingsw.am14.model.cards.buildings.Building;

import java.util.*;

/**
 * <p>
 *     The Tribe class represents the tribe of a player, containing all the hunters, inventors, shamans, gatherers, artists, builders and buildings of the player.
 * </p>
 * <p>
 *     If a new type of card is added to the game, the developer must extend this class and add a new ArrayList of the new type of card, with its getter, setter, adder and sizer.
 * </p>
 */
public class Tribe {
    private ArrayList<Hunter> hunters;
    private ArrayList<Inventor> inventors;
    private ArrayList<Shaman> shamans;
    private ArrayList<Gatherer> gatherers;
    private ArrayList<Artist> artists;
    private ArrayList<Builder> builders;
    private ArrayList<Building> buildings;

    public Tribe() {
        this.hunters = new ArrayList<>();
        this.inventors = new ArrayList<>();
        this.shamans = new ArrayList<>();
        this.gatherers = new ArrayList<>();
        this.artists = new ArrayList<>();
        this.builders = new ArrayList<>();
        this.buildings = new ArrayList<>();
    }

    // GETTERS

    /**
     * <p>
     *     defensive copy of this.inventors
     * </p>
     * @return a copy of this.inventors
     */
    public ArrayList<Inventor> getInventors() {
        return new ArrayList<>(inventors);
    }
    /**
     * <p>
     *     defensive copy of this.builders
     * </p>
     * @return a copy of this.builders
     */
    public ArrayList<Builder> getBuilders() {
        return new ArrayList<>(builders);
    }
    /**
     * <p>
     *     defensive copy of this.buildings
     * </p>
     * @return a copy of this.buildings
     */
    public ArrayList<Building> getBuildings() {
        return new ArrayList<>(buildings);
    }

    //SETTERS

    /**
     *
     * @param hunters not null
     */
    public void setHunters(ArrayList<Hunter> hunters) {
        this.hunters = hunters;
    }
    /**
     *
     * @param inventors not null
     */
    public void setInventors(ArrayList<Inventor> inventors) {
        this.inventors = inventors;
    }
    /**
     *
     * @param shamans not null
     */
    public void setShamans(ArrayList<Shaman> shamans) {
        this.shamans = shamans;
    }
    /**
     *
     * @param gatherers not null
     */
    public void setGatherers(ArrayList<Gatherer> gatherers) {
        this.gatherers = gatherers;
    }
    /**
     *
     * @param artists not null
     */
    public void setArtists(ArrayList<Artist> artists) {
        this.artists = artists;
    }
    /**
     *
     * @param builders not null
     */
    public void setBuilders(ArrayList<Builder> builders) {
        this.builders = builders;
    }
    /**
     *
     * @param buildings not null
     */
    public void setBuildings(ArrayList<Building> buildings) {
        this.buildings = buildings;
    }

    // ADDERS

    /**
     *
     * @param hunter not null
     */
    public void addHunter(Hunter hunter) {
        this.hunters.add(hunter);
    }

    /**
     *
     * @param inventor not null
     */
    public void addInventor(Inventor inventor) {
        this.inventors.add(inventor);
    }

    /**
     *
     * @param shaman not null
     */
    public void addShaman(Shaman shaman) {
        this.shamans.add(shaman);
    }

    /**
     *
     * @param gatherer not null
     */
    public void addGatherer(Gatherer gatherer) {
        this.gatherers.add(gatherer);
    }

    /**
     *
     * @param artist not null
     */
    public void addArtist(Artist artist) {
        this.artists.add(artist);
    }

    /**
     *
     * @param builder not null
     */
    public void addBuilder(Builder builder) {
        this.builders.add(builder);
    }

    /**
     *
     * @param building not null
     */
    public void addBuilding(Building building) {
        this.buildings.add(building);
    }

    // SIZERS

    /**
     *
     * @return this.hunters.size(), the number of hunters in this Tribe
     */
    public int getHuntersSize() {
        return hunters.size();
    }

    /**
     *
     * @return this.inventors.size(), the number of inventors in this Tribe
     */
    public int getInventorsSize() {
        return inventors.size();
    }

    /**
     *
     * @return this.shamans.size(), the number of shamans in this Tribe
     */
    public int getShamansSize() {
        return shamans.size();
    }

    /**
     *
     * @return this.gatherers.size(), the number of gatherers in this Tribe
     */
    public int getGatherersSize() {
        return gatherers.size();
    }

    /**
     *
     * @return this.artists.size(), the number of artists in this Tribe
     */
    public int getArtistsSize() {
        return artists.size();
    }

    /**
     *
     * @return this.builders.size(), the number of builders in this Tribe
     */
    public int getBuildersSize() {
        return builders.size();
    }

    public int getBuildingsSize() {
        return buildings.size();
    }

    public int[] getAllIds() {
        int[] ids = new int[getHuntersSize() + getInventorsSize() +
                getShamansSize() + getGatherersSize() + getArtistsSize() + getBuildersSize() +  getBuildingsSize()];
        for (int i = 0; i < getHuntersSize(); i++) {
            ids[i] = hunters.get(i).getId();
        }
        for (int i = 0; i < getInventorsSize(); i++) {
            ids[i + getHuntersSize()] = inventors.get(i).getId();
        }
        for (int i = 0; i < getShamansSize(); i++) {
            ids[i + getHuntersSize() + getInventorsSize()] = shamans.get(i).getId();
        }
        for (int i = 0; i < getGatherersSize(); i++) {
            ids[i + getHuntersSize() + getInventorsSize() + getShamansSize()] = gatherers.get(i).getId();
        }
        for (int i = 0; i < getArtistsSize(); i++) {
            ids[i + getHuntersSize() + getInventorsSize() + getShamansSize()
                    + getGatherersSize()] = artists.get(i).getId();
        }
        for (int i = 0; i < getBuildersSize(); i++) {
            ids[i + getHuntersSize() + getInventorsSize() + getShamansSize()
                    + getGatherersSize() + getArtistsSize()] = builders.get(i).getId();
        }
        for (int i = 0; i < getBuildingsSize(); i++) {
            ids[i + getHuntersSize() + getInventorsSize() + getShamansSize()
                    + getGatherersSize() + getArtistsSize() + getBuildersSize()] = buildings.get(i).getId();
        }
        return ids;
    }
}