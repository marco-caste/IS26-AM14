package it.polimi.ingsw.am14.model.boardComponents;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.cards.deckCards.*;
import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.model.cards.buildings.Building;

import java.util.*;

/**
 * <p>
 *     The Board class represents the game board and contains all the components of the game, such as decks and tiles.
 * </p>
 */
public class Board {
    /**
     * <p>
     *     The TurnOrderCard represents the turn order of the players in a game.
     * </p>
     */
    private final TurnOrderCard turnOrderCard;
    /**
     * <p>
     *     The OffersPath represents the set of track cards in a game.
     * </p>
     */
    private final OffersPath offersPath;
    /**
     * <p>
     *     The TribeDeck represents the deck of tribe cards in a game.
     * </p>
     */
    private final TribeDeck tribeDeck;
    /**
     * <p>
     *     The BuildingDeck represents the deck of building cards in a game.
     * </p>
     */
    private final BuildingDeck buildingDeck;
    /**
     * <p>
     *     The topDeckableRow represents the row of deckable cards in a game.
     * </p>
     */
    private ArrayList<DeckCard> topDeckableRow;
    /**
     * <p>
     *     The topBuildingRow represents the row of building cards in a game.
     * </p>
     */
    private ArrayList<Building> topBuildingRow;
    /**
     * <p>
     *     The bottomDeckableRow represents the row of deckable cards in a game.
     * </p>
     */
    private ArrayList<DeckCard> bottomDeckableRow;
    /**
     * <p>
     *     The bottomBuildingRow represents the row of building cards in a game.
     * </p>
     */
    private ArrayList<Building> bottomBuildingRow;
    /**
     * <p>
     *     The toResolveSustenanceEventRow represents the row of sustenance event cards in a game.
     * </p>
     */
    private ArrayList<EventCard> toResolveSustenanceEventRow;
    /**
     * <p>
     *     The toResolveOtherEventsRow represents the row of other event cards in a game.
     * </p>
     */
    private ArrayList<EventCard> toResolveOtherEventsRow;


    /**
     * <p>
     *     Initializes the board with the correct number of players
     * </p>
     * @param numPlayers the number of players to be in the game, must be between 2 and 5
     */
    public Board(int numPlayers) {
        this.turnOrderCard                  = new TurnOrderCard(numPlayers);
        this.offersPath                     = new OffersPath(numPlayers);
        this.tribeDeck                      = new TribeDeck(numPlayers);
        this.buildingDeck                   = new BuildingDeck(numPlayers);
        this.topDeckableRow                 = new ArrayList<>();
        this.topBuildingRow                 = new ArrayList<>();
        this.bottomDeckableRow              = new ArrayList<>();
        this.bottomBuildingRow              = new ArrayList<>();
        this.toResolveOtherEventsRow        = new ArrayList<>();
        this.toResolveSustenanceEventRow    = new ArrayList<>();
    }

    /**
     * <p>
     *     Initializes the board with the correct number of players
     * </p>
     * @param turnOrderCard the TurnOrderCard to be used in the game
     * @param offersPath the OffersPath to be used in the game
     * @param tribeDeck the TribeDeck to be used in the game
     * @param buildingDeck the BuildingDeck to be used in the game
     * @param topDeckableRow the top row of DeckCards to be used in the game
     * @param topBuildingRow the top row of Buildings to be used in the game
     * @param bottomDeckableRow the bottom row of DeckCards to be used in the game
     * @param bottomBuildingRow the bottom row of Buildings to be used in the game
     * @param toResolveSustenanceEventRow the row of EventCards to be resolved for sustenance
     * @param toResolveOtherEventsRow the row of EventCards to be resolved for other events
     */
    @JsonCreator
    public Board(
            @JsonProperty("turnOrderCard") TurnOrderCard turnOrderCard,
            @JsonProperty("offersPath") OffersPath offersPath,
            @JsonProperty("tribeDeck") TribeDeck tribeDeck,
            @JsonProperty("buildingDeck") BuildingDeck buildingDeck,
            @JsonProperty("topDeckableRow") ArrayList<DeckCard> topDeckableRow,
            @JsonProperty("topBuildingRow") ArrayList<Building> topBuildingRow,
            @JsonProperty("bottomDeckableRow") ArrayList<DeckCard> bottomDeckableRow,
            @JsonProperty("bottomBuildingRow") ArrayList<Building> bottomBuildingRow,
            @JsonProperty("toResolveSustenanceEventRow") ArrayList<EventCard> toResolveSustenanceEventRow,
            @JsonProperty("toResolveOtherEventsRow") ArrayList<EventCard> toResolveOtherEventsRow) {
        this.turnOrderCard                  = turnOrderCard;
        this.offersPath                     = offersPath;
        this.tribeDeck                      = tribeDeck;
        this.buildingDeck                   = buildingDeck;
        this.topDeckableRow                 = topDeckableRow;
        this.topBuildingRow                 = topBuildingRow;
        this.bottomDeckableRow              = bottomDeckableRow;
        this.bottomBuildingRow              = bottomBuildingRow;
        this.toResolveSustenanceEventRow    = toResolveSustenanceEventRow;
        this.toResolveOtherEventsRow        = toResolveOtherEventsRow;
    }

    // GETTERS
    public OffersPath getOffersPath() {
        return offersPath;
    }
    public BuildingDeck getBuildingDeck() {
        return buildingDeck;
    }
    public TribeDeck getTribeDeck() {
        return tribeDeck;
    }
    public TurnOrderCard getTurnOrderCard() {
        return turnOrderCard;
    }

    /**
     *
     * @return a defensive copy of the top deck cards row
     */
    public ArrayList<DeckCard> getTopDeckableRow() {
        return new ArrayList<>(this.topDeckableRow);
    }

    /**
     *
     * @return a defensive copy of the top building cards row
     */
    public ArrayList<Building> getTopBuildingRow() {
        return new ArrayList<>(this.topBuildingRow);
    }

    /**
     *
     * @return a defensive copy of the bottom deck cards row
     */
    public ArrayList<DeckCard> getBottomDeckableRow() {
        return new ArrayList<>(this.bottomDeckableRow);
    }

    /**
     *
     * @return a defensive copy of the bottom building cards row
     */
    public ArrayList<Building> getBottomBuildingRow() {
        return new ArrayList<>(this.bottomBuildingRow);
    }

    /**
     *
     * @param index the index of the event, must be >= 0
     * @return the Event to be resolved, given its position {@code index}
     */
    public EventCard getFromResolveOtherEventsRow(int index) {
        return this.toResolveOtherEventsRow.get(index);
    }

    /**
     *
     * @param index the index of the sustenance event, must be >= 0
     * @return the Sustenance Event to be resolved, given its position {@code index}
     */
    public EventCard getFromResolveSustenanceEventRow(int index) {
        return this.toResolveSustenanceEventRow.get(index);
    }

    /**
     *
     * @return the last Event to be resolved
     */
    @JsonIgnore
    public EventCard getLastOtherEvent() {
        return this.toResolveOtherEventsRow.getLast();
    }

    // SETTERS
    public void setBottomBuildingRow(ArrayList<Building> bottomBuildingRow) {
        this.bottomBuildingRow = bottomBuildingRow;
    }
    public void setBottomDeckableRow(ArrayList<DeckCard> bottomDeckableRow) {
        this.bottomDeckableRow = bottomDeckableRow;
    }
    public void setTopBuildingRow(ArrayList<Building> topBuildingRow) {
        this.topBuildingRow = topBuildingRow;
    }
    public void setTopDeckableRow(ArrayList<DeckCard> topDeckableRow) {
        this.topDeckableRow = topDeckableRow;
    }

    // REMOVERS

    /**
     * <p>
     *     removes {@code card} from the top building row
     * </p>
     * @param card the card to be removed, must not be null
     */
    public void removeFromTopBuildingRow(Card card) {
        this.topBuildingRow.remove((Building) card);
    }

    /**
     * <p>
     *     removes {@code card} from the top deckable row
     * </p>
     * @param card the card to be removed, must not be null
     */
    public void removeFromTopDeckableRow(Card card) {
        this.topDeckableRow.remove((DeckCard) card);
    }

    /**
     * <p>
     *     removes {@code card} from the bottom building row
     * </p>
     * @param card the card to be removed, must not be null
     */
    public void removeFromBottomBuildingRow(Card card) {
        this.bottomBuildingRow.remove((Building) card);
    }

    /**
     * <p>
     *     removes {@code card} from the bottom deckable row
     * </p>
     * @param card the card to be removed, must not be null
     */
    public void removeFromBottomDeckableRow(Card card) {
        this.bottomDeckableRow.remove((DeckCard) card);
    }

    /**
     * <p>
     *     overload of removeFromBottomDeckableRow, removes a card from the bottom deckable row given its index
     * </p>
     * @param cardIndex the index of the card to be removed, must be >= 0
     */
    public void removeFromBottomDeckableRow(int cardIndex) {
        this.bottomDeckableRow.remove(cardIndex);
    }

    /**
     * <p>
     *     removes the last Event in the list of events to be resolved.
     * </p>
     */
    public void removeLastOtherEvent() {
        this.toResolveOtherEventsRow.removeLast();
    }

    // ADDERS

    /**
     * <p>
     *     adds {@code event} to the list of events to be resolved
     * </p>
     * @param event not null
     */
    public void addToResolvingOtherEventsRow(EventCard event) {
        this.toResolveOtherEventsRow.add(event);
    }

    /**
     * <p>
     *     adds {@code event} to the list of sustenance events to be resolved
     * </p>
     * @param event not null
     */
    public void addToResolvingSustenanceEventsRow(EventCard event) {
        this.toResolveSustenanceEventRow.add(event);
    }

    /**
     * <p>
     *     adds {@code card} to the bottom deck cards row
     * </p>
     * @param card not null
     */
    public void addToBottomDeckableRow(DeckCard card) {
        this.bottomDeckableRow.add(card);
    }

    /**
     * <p>
     *     adds {@code card} to the top deck cards row
     * </p>
     * @param card not null
     */
    public void addToTopDeckableRow(DeckCard card) {
        this.topDeckableRow.add(card);
    }

    /**
     * <p>
     *     adds {@code building} to the top building cards row
     * </p>
     * @param building not null
     */
    public void addToTopBuildingRow(Building building) {
        this.topBuildingRow.add(building);
    }

    // SIZERS
    public int topDeckableRowSize() {
        return this.topDeckableRow.size();
    }
    public int toResolveOtherEventsRowSize() {
        return this.toResolveOtherEventsRow.size();
    }
    public int toResolveSustenanceEventsRowSize() {
        return this.toResolveSustenanceEventRow.size();
    }
    public int bottomDeckableRowSize() {
        return this.bottomDeckableRow.size();
    }


    // METHODS EXPOSED FOR endRound():
    // takes all the DeckCards from the top row and puts them in the bottom row

    /**
     * <p>
     *     moves the cards from the top deck cards row to the bottom deck cards row and resets the top deck cards row
     * </p>
     */
    public void switchDeckablesFromTop() {
        this.bottomDeckableRow.addAll(this.topDeckableRow);
        this.topDeckableRow = new ArrayList<>();
    }
    // takes all the buildings from the top row and puts them in the bottom row

    /**
     * <p>
     *     moves the buildings from the top building cards row to the bottom building cards row and resets the top building cards row
     * </p>
     */
    public void switchBuildingsFromTop() {
        this.bottomBuildingRow.addAll(this.topBuildingRow);
        this.topBuildingRow = new ArrayList<>();
    }
    // iterates on the bottom row and for each card calls card.discard()

    /**
     * puts events in two separate lists, according to their type, in order for them to be resolved, and discards cards from the bottom deck cards row
     */
    public void idleEventsAndDiscardCharactersFromBottom() {
        int initialSize = this.bottomDeckableRow.size();
        for (int i = initialSize - 1; i >= 0; i--) {
            this.bottomDeckableRow.get(i).discard(this, i);
        }

        // sorts the events by age
        this.toResolveOtherEventsRow.sort((e1, e2) -> Integer.compare(e2.getAge(), e1.getAge()));
        this.toResolveSustenanceEventRow.sort(Comparator.comparingInt(Card::getAge));
    }

    /**
     * <p>
     *     removes buildings from the bottom building cards row.
     * </p>
     */
    public void discardBuildingFromBottom() {
        this.bottomBuildingRow = new ArrayList<>();
    }
    // it deletes the previous list of events to resolve by assigning to the reference a
    // new object.

    /**
     * <p>
     *     resets the temporary event rows
     * </p>
     */
    public void discardEvents() {
        this.toResolveOtherEventsRow = new ArrayList<>();
        this.toResolveSustenanceEventRow = new ArrayList<>();
    }
    // draws the correct number of cards to start the new round

    /**
     * <p>
     *     fills the top deck cards row with {@code numPlayers} + 4 cards
     * </p>
     * @param numPlayers the number of players in the game, between 2 and 5
     */

    public void pickDeckableAndAddToTop(int numPlayers) {
        for (int i = 0; i < numPlayers + 4 ; i++) {
            this.topDeckableRow.add(this.tribeDeck.deckPop());
        }
    }

    /**
     * <p>
     *     refills the top building cards row from the building deck, accordingly to the current age
     * </p>
     * @param currentAge the current age of thegame, must be between 1 and 3
     */
    public void pickBuildingAndAddToTop(int currentAge) {
        this.topBuildingRow.addAll(buildingDeck.getBuildings(currentAge));
    }

    /**
     * <p>
     *     finds the position of a player in the offers path, given its nickname
     * </p>
     * @param nickname the nickname of the player to be found, must not be null
     * @return an Optional containing the position of the player in the offers path, or an empty Optional if the player is not found
     */
    public Optional<Character> findPlayerPosition(String nickname) {
        return this.offersPath.findPlayerPosition(nickname);
    }
}