package it.polimi.ingsw.am14.utils;

import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.events.ClientRequest;
import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.model.boardComponents.TrackCard;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.deckCards.DeckCard;
import it.polimi.ingsw.am14.model.gameLogic.Game;

import java.util.List;
import java.util.Map;

public interface Codec {

    // serviranno:
    /*
        DESERIALIZZAZIONE:
    - un metodo per leggere le deckCard da file
    - un metodo per leggere i building da file
    - un metodo per leggere le trackCard da file

    - un metodo per leggere il salvataggio della partita su file    ok
    - un metodo per deserializzare eventi   ok
    - un metodo per deserializzare le clientRequest   ok
        SERIALIZZAZIONE:
    - un metodo per serializzare eventi  ok
    - un metodo per serializzare clientRequest  ok
    - un metodo per serializzare il salvataggio della partita su file   ok
     */

    /**
     *
     * @param filePath not null, path of the file where the save data is stored
     * @return the saved Game object
     */
    Game readSaveData(String filePath);

    /**
     *
     * @param controller not null, the LobbyController object to be saved
     * @param filePath not null, path of the file where the save data will be stored
     */
    void writeSaveData(LobbyController controller, String filePath);

    /**
     *
     * @param input not null, the string to be parsed into a ClientRequest
     * @return the ClientRequest object represented by the input string
     */
    ClientRequest parseClientRequest(String input);

    /**
     *
     * @param request not null, the ClientRequest object to be serialized
     * @return a string representation of the ClientRequest object
     */
    String writeClientRequest(ClientRequest request);

    /**
     *
     * @param input not null, the string to be parsed into an Event
     * @return the Event object represented by the input string
     */
    Event parseEvent(String input);

    /**
     *
     * @param event not null, the Event object to be serialized
     * @return a string representation of the Event object
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
