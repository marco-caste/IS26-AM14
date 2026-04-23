package it.polimi.ingsw.am14.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am14.events.ClientRequest;
import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.model.boardComponents.TrackCard;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.deckCards.DeckCard;
import it.polimi.ingsw.am14.model.gameLogic.Game;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonCodec implements Codec {


    /**
     * @param filePath not null, path of the file where the save data is stored
     * @return the saved Game object
     */
    @Override
    public Game readSaveData(String filePath) {
        return null;
    }

    /**
     * @param game     not null, the Game object to be saved
     * @param filePath not null, path of the file where the save data will be stored
     */
    @Override
    public void writeSaveData(Game game, String filePath) {

    }

    /**
     * @param input not null, the string to be parsed into a ClientRequest
     * @return the ClientRequest object represented by the input string
     */
    @Override
    public ClientRequest parseClientRequest(String input) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        try {
            return objectMapper.readValue(input, new TypeReference<ClientRequest>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param request not null, the ClientRequest object to be serialized
     * @return a string representation of the ClientRequest object
     */
    @Override
    public String writeClientRequest(ClientRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param input not null, the string to be parsed into an Event
     * @return the Event object represented by the input string
     */
    @Override
    public Event parseEvent(String input) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(input, new TypeReference<Event>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param event not null, the Event object to be serialized
     * @return a string representation of the Event object
     */
    @Override
    public String writeEvent(Event event) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * reads the deck cards from the specified file path and builds a list of DeckCards
     * </p>
     *
     * @param filePath   not null, path of the file where the deck cards data is stored
     * @param numPlayers the initial number of players in the game, used to determine which cards to read from the file
     * @return a list of DeckCard objects read from the specified file path, sorted by number of players
     */
    @Override
    public List<DeckCard> readDeckCards(String filePath, int numPlayers) {
        ObjectMapper objectMapper = new ObjectMapper();

        // filePath must be "resources/DeckCards.json"

        try {
            return objectMapper.readValue(new File(filePath), new TypeReference<List<List<DeckCard>>>(){}).stream().
                    limit(numPlayers - 2).flatMap(Collection::stream).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * reads the building cards from the specified file path and builds a list of Building objects
     * </p>
     *
     * @param filePath not null, path of the file where the building cards data is stored
     * @return a list of Building objects read from the specified file path, sorted by age
     */
    @Override
    public List<Building> readBuildings(String filePath) {

        // buildings will come from "resources/Buildings.json" as a List<List<Building>>

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(new File(filePath), new TypeReference<List<List<Building>>>(){}).stream().
                    flatMap(Collection::stream).
                    toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * reads the track cards from the specified file path and builds a map of TrackCard objects,
     * where the keys are the track card names and the values are the corresponding TrackCard objects
     * </p>
     *
     * @param filePath   not null, path of the file where the track cards data is stored
     * @param numPlayers the initial number of players in the game, used to determine which track cards to read from the file
     * @return a map of TrackCard objects read from the specified file path, where the keys are the TrackCard names
     */
    @Override
    public Map<Character, TrackCard> readTrackCards(String filePath, int numPlayers) {

        // track cards will come from "resources/TrackCards.json" as a List<List<TrackCard>>

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(new File(filePath), new TypeReference<List<List<TrackCard>>>(){}).stream().
                    limit(numPlayers - 2).
                    flatMap(Collection::stream).
                    collect(Collectors.toMap(TrackCard::getName, trackCard -> trackCard));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
