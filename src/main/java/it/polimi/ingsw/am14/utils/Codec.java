package it.polimi.ingsw.am14.utils;

import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.network.messages.requests.ClientRequest;
import it.polimi.ingsw.am14.network.messages.events.Event;
import it.polimi.ingsw.am14.model.boardComponents.TrackCard;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.deckCards.DeckCard;

import java.util.List;
import java.util.Map;

public interface Codec {

    /**
     * Reads the saved game data from the specified file and reconstructs the corresponding {@code LobbyController}.
     *
     * @param filePath the path of the save file to read
     * @return the {@code LobbyController} restored from the save data
     */
    LobbyController readSaveData(String filePath);

    /**
     * Reads the card descriptions from the specified file.
     *
     * @param filePath the path of the file containing the card descriptions
     * @return a map where each card ID is associated with its description
     */
    Map<Integer, String> readCardDescriptions(String filePath);

    /**
     * Writes the current game state to the specified file.
     *
     * @param controller the {@code LobbyController} to serialize
     * @param filePath the path of the file where the save data will be written
     */
    void writeSaveData(LobbyController controller, String filePath);

    /**
     * Parses a client request from its string representation.
     *
     * @param input the string to parse
     * @return the corresponding {@code ClientRequest}
     */
    ClientRequest parseClientRequest(String input);

    /**
     * Serializes a client request into its string representation.
     *
     * @param request the {@code ClientRequest} to serialize
     * @return the serialized request as a string
     */
    String writeClientRequest(ClientRequest request);

    /**
     * Parses an event from its string representation.
     *
     * @param input the string to parse
     * @return the corresponding {@code Event}
     */
    Event parseEvent(String input);

    /**
     * Serializes an event into its string representation.
     *
     * @param event the {@code Event} to serialize
     * @return the serialized event as a string
     */
    String writeEvent(Event event);

    /**
     * <p>
     *     reads the deck cards from the specified file path and builds a list of DeckCards
     * </p>
     * @param filePath not null, path of the file where the deck cards data is stored
     * @param numPlayers the initial number of players in the game, used to determine which cards to read from the file
     * @return a list of DeckCard objects read from the specified file path, sorted by number of players
     */
    List<DeckCard> readDeckCards(String filePath, int numPlayers);

    /**
     * <p>
     *     reads the building cards from the specified file path and builds a list of Building objects
     * </p>
     * @param filePath not null, path of the file where the building cards data is stored
     * @return a list of Building objects read from the specified file path, sorted by age
     */
    List<Building> readBuildings(String filePath);

    /**
     * <p>
     *     reads the track cards from the specified file path and builds a map of TrackCard objects,
     *     where the keys are the track card names and the values are the corresponding TrackCard objects
     * </p>
     * @param filePath not null, path of the file where the track cards data is stored
     * @param numPlayers the initial number of players in the game, used to determine which track cards to read from the file
     * @return a map of TrackCard objects read from the specified file path, where the keys are the TrackCard names, sorted alphabetically
     */
    Map<Character, TrackCard> readTrackCards(String filePath, int numPlayers);
}
