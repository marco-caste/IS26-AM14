package it.polimi.ingsw.am14.model.boardComponents;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.cards.deckCards.*;
import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.model.cards.buildings.Building;

import java.util.*;

public class Board {
    private final TurnOrderCard turnOrderCard;
    private final OffersPath offersPath;
    private final TribeDeck tribeDeck;
    private final BuildingDeck buildingDeck;
    private ArrayList<DeckCard> topDeckableRow;
    private ArrayList<Building> topBuildingRow;
    private ArrayList<DeckCard> bottomDeckableRow;
    private ArrayList<Building> bottomBuildingRow;
    private ArrayList<EventCard> toResolveSustenanceEventRow;
    private ArrayList<EventCard> toResolveOtherEventsRow;

    // overrides the default constructor

    /**
     * <p>
     *     calls the constructors of the other game objects, as well as for the ArrayLists
     * </p>
     * @param numPlayers between 2 and 5
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

    // called in case of a game crash, gets values read from a JSON files (by another method)
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
     * @return a defensive copy of this.topDeckableRow
     */
    public ArrayList<DeckCard> getTopDeckableRow() {
        return new ArrayList<>(this.topDeckableRow);
    }

    /**
     *
     * @return a defensive copy of this.topBuildingRow
     */
    public ArrayList<Building> getTopBuildingRow() {
        return new ArrayList<>(this.topBuildingRow);
    }

    /**
     *
     * @return a defensive copy of this.bottomDeckableRow
     */
    public ArrayList<DeckCard> getBottomDeckableRow() {
        return new ArrayList<>(this.bottomDeckableRow);
    }

    /**
     *
     * @return a defensive copy of this.bottomBuildingRow
     */
    public ArrayList<Building> getBottomBuildingRow() {
        return new ArrayList<>(this.bottomBuildingRow);
    }

    /**
     *
     * @param index >= 0
     * @return the Event at position index
     */
    public EventCard getFromResolveOtherEventsRow(int index) {
        return this.toResolveOtherEventsRow.get(index);
    }

    /**
     *
     * @param index >= 0
     * @return the Event at position index
     */
    public EventCard getFromResolveSustenanceEventRow(int index) {
        return this.toResolveSustenanceEventRow.get(index);
    }

    /**
     *
     * @return the last Event
     */
    @JsonIgnore
    public EventCard getLastOtherEvent() {
        return this.toResolveOtherEventsRow.getLast();
    }

    // SETTERS - TODO: da capire se necessari per il reload della partita dopo un crash
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
    public void setToResolveOtherEventsRow(ArrayList<EventCard> toResolveOtherEventsRow) {
        this.toResolveOtherEventsRow = toResolveOtherEventsRow;
    }
    public void setToResolveSustenanceEventRow(ArrayList<EventCard> toResolveSustenanceEventRow) {
        this.toResolveSustenanceEventRow = toResolveSustenanceEventRow;
    }

    // REMOVERS

    /**
     * <p>
     *     removes card from this.topBuildingRow
     * </p>
     * @param card not null
     */
    public void removeFromTopBuildingRow(Card card) {
        this.topBuildingRow.remove((Building) card);
    }

    /**
     * <p>
     *     removes card from this.topDeckableRow
     * </p>
     * @param card not null
     */
    public void removeFromTopDeckableRow(Card card) {
        this.topDeckableRow.remove((DeckCard) card);
    }

    /**
     * <p>
     *     removes card from this.bottomBuildingRow
     * </p>
     * @param card not null
     */
    public void removeFromBottomBuildingRow(Card card) {
        this.bottomBuildingRow.remove((Building) card);
    }

    /**
     * <p>
     *     removes card from this.bottomDeckableRow
     * </p>
     * @param card not null
     */
    public void removeFromBottomDeckableRow(Card card) {
        this.bottomDeckableRow.remove((DeckCard) card);
    }

    /**
     * <p>
     *     overload of removeFromBottomDeckableRow
     * </p>
     * @param cardIndex >= 0
     */
    public void removeFromBottomDeckableRow(int cardIndex) {
        this.bottomDeckableRow.remove(cardIndex);
    }

    /**
     * <p>
     *     removes the last Event in this.toResolveOtherEventsRow
     * </p>
     */
    public void removeLastOtherEvent() {
        this.toResolveOtherEventsRow.removeLast();
    }

    // ADDERS

    /**
     * <p>
     *     adds event to this.toResolveOtherEventsRow
     * </p>
     * @param event not null
     */
    public void addToResolvingOtherEventsRow(EventCard event) {
        this.toResolveOtherEventsRow.add(event);
    }

    /**
     * <p>
     *     adds event to this.toResolveSustenanceEventRow
     * </p>
     * @param event not null
     */
    public void addToResolvingSustenanceEventsRow(EventCard event) {
        this.toResolveSustenanceEventRow.add(event);
    }

    /**
     * <p>
     *     adds card to this.bottomDeckableRow
     * </p>
     * @param card not null
     */
    public void addToBottomDeckableRow(DeckCard card) {
        this.bottomDeckableRow.add(card);
    }

    /**
     * <p>
     *     adds card to this.topDeckableRow
     * </p>
     * @param card not null
     */
    public void addToTopDeckableRow(DeckCard card) {
        this.topDeckableRow.add(card);
    }

    /**
     * <p>
     *     adds building to this.topBuildingRow
     * </p>
     * @param building not null
     */
    public void addToTopBuildingRow(Building building) {
        this.topBuildingRow.add(building);
    }

    // SIZERS

    /**
     *
     * @return this.topDeckableRow.size()
     */
    public int topDeckableRowSize() {
        return this.topDeckableRow.size();
    }

    /**
     *
     * @return this.toResolveOtherEventsRow.size()
     */
    public int toResolveOtherEventsRowSize() {
        return this.toResolveOtherEventsRow.size();
    }

    /**
     *
     * @return this.toResolveSustenanceEventRow.size()
     */
    public int toResolveSustenanceEventsRowSize() {
        return this.toResolveSustenanceEventRow.size();
    }

    /**
     *
     * @return this.bottomDeckableRow.size()
     */
    public int bottomDeckableRowSize() {
        return this.bottomDeckableRow.size();
    }


    // METHODS EXPOSED FOR endRound():
    // takes all the DeckCards from the top row and puts them in the bottom row

    /**
     * <p>
     *     moves the elements of this.topDeckableRow to this.bottomDeckableRow
     *     resets this.topDeckableRow
     * </p>
     */
    public void switchDeckablesFromTop() {
        this.bottomDeckableRow.addAll(this.topDeckableRow);
        this.topDeckableRow = new ArrayList<>();
    }
    // takes all the buildings from the top row and puts them in the bottom row

    /**
     * <p>
     *     moves the elements of this.topBuildingRow to this.bottomDeckableRow
     *     resets this.topBuildingRow
     * </p>
     */
    public void switchBuildingsFromTop() {
        this.bottomBuildingRow.addAll(this.topBuildingRow);
        this.topBuildingRow = new ArrayList<>();
    }
    // iterates on the bottom row and for each card calls card.discard()

    /**
     * <p>
     *     uses polymorphism to discard cards from this.bottomDeckableRow
     *     and sorts the event temporary rows by age (descending)
     * </p>
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
     *     resets this.bottomBuildingRow
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
     *     fills the deckableTopRow with numPlayers + 4 cards
     * </p>
     * @param numPlayers between 2 and 4
     */
    public void pickDeckableAndAddToTop(int numPlayers) {
        for (int i = 0; i < numPlayers + 4; i++) {
            this.topDeckableRow.add(this.tribeDeck.deckPop());
        }
    }

    /**
     * <p>
     *     refills this.topBuildingRow with the buildings from buildingDeck, of the current age
     * </p>
     * @param currentAge between 1 and 3
     */
    public void pickBuildingAndAddToTop(int currentAge) {
        this.topBuildingRow.addAll(buildingDeck.getBuildings(currentAge));
    }

    public Optional<Character> findPlayerPosition(String nickname) {
        return this.offersPath.findPlayerPosition(nickname);
    }
}