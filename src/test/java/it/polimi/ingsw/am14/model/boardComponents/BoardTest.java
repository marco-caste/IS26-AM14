package it.polimi.ingsw.am14.model.boardComponents;

import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.buildings.types.ImmediateBuilding;
import it.polimi.ingsw.am14.model.cards.deckCards.*;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    Board boardStub;
    static final int numPlayers = 2;

    @BeforeEach
    void setUp() {
        boardStub = new Board(numPlayers);
    }

    @Test
    void getTopDeckableRow() {
        ArrayList<DeckCard> topRow1 = new ArrayList<>();
        topRow1.add(new Hunter(1, 2, false));
        topRow1.add(new Hunter(2, 3, true));

        Board tempBoard = new Board(null,
                null,
                null,
                null,
                topRow1,
                null,
                null,
                null,
                null,
                null);

        assertEquals(topRow1, tempBoard.getTopDeckableRow());
        assertNotSame(topRow1, tempBoard.getTopDeckableRow());

    }

    @Test
    void getTopBuildingRow() {
        ArrayList<Building> topRow1 = new ArrayList<>();
        topRow1.add(new ImmediateBuilding(2, 2, 2, 3, null));
        topRow1.add(new ImmediateBuilding(2, 2, 2, 4, null));

        Board tempBoard = new Board(null,
                null,
                null,
                null,
                null,
                topRow1,
                null,
                null,
                null,
                null);

        assertEquals(topRow1, tempBoard.getTopBuildingRow());
        assertNotSame(topRow1, tempBoard.getTopBuildingRow());
    }

    @Test
    void getBottomDeckableRow() {
        ArrayList<DeckCard> topRow1 = new ArrayList<>();
        topRow1.add(new Hunter(1, 2, false));
        topRow1.add(new Hunter(2, 3, true));

        Board tempBoard = new Board(null,
                null,
                null,
                null,
                null,
                null,
                topRow1,
                null,
                null,
                null);

        assertEquals(topRow1, tempBoard.getBottomDeckableRow());
        assertNotSame(topRow1, tempBoard.getBottomDeckableRow());
    }

    @Test
    void getBottomBuildingRow() {
        ArrayList<Building> topRow1 = new ArrayList<>();
        topRow1.add(new ImmediateBuilding(2, 2, 2, 3, null));
        topRow1.add(new ImmediateBuilding(2, 2, 2, 4, null));

        Board tempBoard = new Board(null,
                null,
                null,
                null,
                null,
                null,
                null,
                topRow1,
                null,
                null);

        assertEquals(topRow1, tempBoard.getBottomBuildingRow());
        assertNotSame(topRow1, tempBoard.getBottomBuildingRow());
    }

    @Test
    void getFromResolveOtherEventsRow() {
        ArrayList<EventCard> OtherEvents = new ArrayList<>();
        EventCard eventStub2 = new HuntEvent(1, 5);
        OtherEvents.add(new PaintEvent(1, 3));
        OtherEvents.add(eventStub2);
        Board temporaryBoard = new Board(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                OtherEvents);

        assertThrows(IndexOutOfBoundsException.class, () -> temporaryBoard.getFromResolveOtherEventsRow(3));
        assertEquals(eventStub2, temporaryBoard.getFromResolveOtherEventsRow(1));
        assertNotEquals(eventStub2, temporaryBoard.getFromResolveOtherEventsRow(0));
        assertEquals(2, temporaryBoard.toResolveOtherEventsRowSize());
    }

    @Test
    void getFromResolveSustenanceEventRow() {
        ArrayList<EventCard> SustenanceEvents = new ArrayList<>();
        EventCard eventStub1 = new SustenanceEvent(2, 21);
        SustenanceEvents.add(eventStub1);
        SustenanceEvents.add(new SustenanceEvent(3, 24));

        Board temporaryBoard = new Board(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                SustenanceEvents,
                null);

        assertThrows(IndexOutOfBoundsException.class, () -> temporaryBoard.getFromResolveSustenanceEventRow(3));
        assertEquals(eventStub1, temporaryBoard.getFromResolveSustenanceEventRow(0));
        assertNotEquals(eventStub1, temporaryBoard.getFromResolveSustenanceEventRow(1));
        assertEquals(2, temporaryBoard.toResolveSustenanceEventsRowSize());
    }

    @Test
    void getLastOtherEvent() {
        ArrayList<EventCard> OtherEvents = new ArrayList<>();
        EventCard eventStub2 = new HuntEvent(1, 5);
        OtherEvents.add(new PaintEvent(1, 3));
        OtherEvents.add(eventStub2);

        Board temporaryBoard = new Board(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                OtherEvents);

        assertEquals(eventStub2, temporaryBoard.getLastOtherEvent());
    }

//    @Test
//    void setBottomBuildingRow() {
//    }
//
//    @Test
//    void setBottomDeckableRow() {
//    }
//
//    @Test
//    void setTopBuildingRow() {
//    }
//
//    @Test
//    void setTopDeckableRow() {
//    }
//
//    @Test
//    void setToResolveOtherEventsRow() {
//    }
//
//    @Test
//    void setToResolveSustenanceEventRow() {
//    }

    @Test
    void removeFromTopBuildingRow() {
        ArrayList<Building> topRow1 = new ArrayList<>();
        Card card1 = new ImmediateBuilding(2, 2, 2, 3, null);
        topRow1.add((Building) card1);
        Card card2 = new ImmediateBuilding(2, 2, 2, 4, null);
        topRow1.add((Building) card2);

        Board tempBoard = new Board(null,
                null,
                null,
                null,
                null,
                topRow1,
                null,
                null,
                null,
                null);

        tempBoard.removeFromTopBuildingRow(card1);
        assertEquals(1, tempBoard.getTopBuildingRow().size());
        assertEquals(card2, tempBoard.getTopBuildingRow().getFirst());
    }

    @Test
    void removeFromTopDeckableRow() {
        ArrayList<DeckCard> deckables = new ArrayList<>();
        Card card1 = new Hunter(2, 2, false);
        deckables.add((DeckCard) card1);
        Card card2 = new Gatherer(2, 3, 3);
        deckables.add((DeckCard) card2);

        Board tempBoard = new Board(null,
                null,
                null,
                null,
                deckables,
                null,
                null,
                null,
                null,
                null);

        tempBoard.removeFromTopDeckableRow(card1);
        assertEquals(1, tempBoard.getTopDeckableRow().size());
        assertEquals(card2, tempBoard.getTopDeckableRow().getFirst());
    }

    @Test
    void removeFromBottomBuildingRow() {
        ArrayList<Building> topRow1 = new ArrayList<>();
        Card card1 = new ImmediateBuilding(2, 2, 2, 3, null);
        topRow1.add((Building) card1);
        Card card2 = new ImmediateBuilding(2, 2, 2, 4, null);
        topRow1.add((Building) card2);

        Board tempBoard = new Board(null,
                null,
                null,
                null,
                null,
                null,
                null,
                topRow1,
                null,
                null);

        tempBoard.removeFromBottomBuildingRow(card1);
        assertEquals(1, tempBoard.getBottomBuildingRow().size());
        assertEquals(card2, tempBoard.getBottomBuildingRow().getFirst());
    }

    @Test
    void removeFromBottomDeckableRow() {
        ArrayList<DeckCard> deckables = new ArrayList<>();
        Card card1 = new Hunter(2, 2, false);
        deckables.add((DeckCard) card1);
        Card card2 = new Gatherer(2, 3, 3);
        deckables.add((DeckCard) card2);

        Board tempBoard = new Board(null,
                null,
                null,
                null,
                null,
                null,
                deckables,
                null,
                null,
                null);

        tempBoard.removeFromBottomDeckableRow(card1);
        assertEquals(1, tempBoard.getBottomDeckableRow().size());
        assertEquals(card2, tempBoard.getBottomDeckableRow().getFirst());
    }

    @Test
    void removeLastOtherEvent() {
        ArrayList<EventCard> events = new ArrayList<>();
        EventCard event = new ShamanEvent(2, 1);
        events.add(event);
        events.add(new HuntEvent(2, 2));

        Board tempBoard = new Board(null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                events);

        tempBoard.removeLastOtherEvent();
        assertEquals(1, tempBoard.toResolveOtherEventsRowSize());
        assertEquals(event, tempBoard.getFromResolveOtherEventsRow(0));
    }

    @Test
    void addToResolvingOtherEventsRow() {
        ShamanEvent event = new ShamanEvent(1, 2);
        boardStub.addToResolvingOtherEventsRow(event);

        assertEquals(1, boardStub.toResolveOtherEventsRowSize());
        assertEquals(event, boardStub.getFromResolveOtherEventsRow(0));
    }

    @Test
    void addToResolvingSustenanceEventsRow() {
        SustenanceEvent event = new SustenanceEvent(1, 2);
        boardStub.addToResolvingSustenanceEventsRow(event);

        assertEquals(1, boardStub.toResolveSustenanceEventsRowSize());
        assertEquals(event, boardStub.getFromResolveSustenanceEventRow(0));
    }

    @Test
    void addToBottomDeckableRow() {
        Hunter hunter = new Hunter(1, 1, false);
        boardStub.addToBottomDeckableRow(hunter);

        assertEquals(1, boardStub.bottomDeckableRowSize());
        assertEquals(hunter, boardStub.getBottomDeckableRow().getFirst());
    }

    @Test
    void topDeckableRowSize() {
        ArrayList<DeckCard> deckCards = new ArrayList<>();
        deckCards.add(new Hunter(1, 2, false));
        deckCards.add(new Hunter(1, 2, true));
        Board tempBoard = new Board(null,
                null,
                null,
                null,
                deckCards,
                null,
                null,
                null,
                null,
                null);

        assertEquals(2, tempBoard.topDeckableRowSize());
    }

    @Test
    void toResolveOtherEventsRowSize() {
        ArrayList<EventCard> events = new ArrayList<>();
        EventCard event = new ShamanEvent(2, 1);
        events.add(event);
        events.add(new HuntEvent(2, 2));

        Board tempBoard = new Board(null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                events);

        assertEquals(2, tempBoard.toResolveOtherEventsRowSize());
    }

    @Test
    void toResolveSustenanceEventsRowSize() {
        ArrayList<EventCard> SustenanceEvents = new ArrayList<>();
        EventCard eventStub1 = new SustenanceEvent(2, 21);
        SustenanceEvents.add(eventStub1);
        SustenanceEvents.add(new SustenanceEvent(3, 24));

        Board temporaryBoard = new Board(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                SustenanceEvents,
                null);

        assertEquals(2, temporaryBoard.toResolveSustenanceEventsRowSize());
    }

    @Test
    void bottomDeckableRowSize() {
        ArrayList<DeckCard> deckCards = new ArrayList<>();
        deckCards.add(new Hunter(1, 2, false));
        deckCards.add(new Hunter(1, 2, true));
        Board tempBoard = new Board(null,
                null,
                null,
                null,
                null,
                null,
                deckCards,
                null,
                null,
                null);

        assertEquals(2, tempBoard.bottomDeckableRowSize());
    }

    @Test
    void switchDeckablesFromTop() {
        ArrayList<DeckCard> deckCards = new ArrayList<>();
        deckCards.add(new Hunter(1, 2, false));
        deckCards.add(new Hunter(1, 2, true));
        deckCards.add(new ShamanEvent(2, 4));
        Board tempBoard = new Board(null,
                null,
                null,
                null,
                deckCards,
                null,
                new ArrayList<>(),
                null,
                null,
                null);

        tempBoard.switchDeckablesFromTop();

        assertEquals(3, tempBoard.bottomDeckableRowSize());
        assertEquals(0, tempBoard.topDeckableRowSize());
        assertEquals(deckCards, tempBoard.getBottomDeckableRow());
    }

    @Test
    void switchBuildingsFromTop() {
        ArrayList<Building> previousTopBuildingRow = boardStub.getTopBuildingRow();
        boardStub.switchBuildingsFromTop();

        assertEquals(previousTopBuildingRow.size(), boardStub.getBottomBuildingRow().size());
        assertEquals(0, boardStub.getTopBuildingRow().size());
    }

    @Test
    void idleEventsAndDiscardCharactersFromBottom() {
        ArrayList<DeckCard> bottomDeckables = new ArrayList<>();
        ShamanEvent s2 = new ShamanEvent(2, 3);
        ShamanEvent s3 = new ShamanEvent(3, 3);
        SustenanceEvent g2 = new SustenanceEvent(2, 4);
        SustenanceEvent g3 = new SustenanceEvent(3, 5);

        bottomDeckables.add(s2);
        bottomDeckables.add(g3);
        bottomDeckables.add(s3);
        bottomDeckables.add(g2);
        bottomDeckables.add(new Shaman(3, 2, 4));

        Board tempBoard = new Board(
                null,
                null,
                null,
                null,
                null,
                null,
                bottomDeckables,
                null,
                new ArrayList<>(),
                new ArrayList<>());

        tempBoard.idleEventsAndDiscardCharactersFromBottom();

        ArrayList<EventCard> stubArrayList = new ArrayList<>();
        stubArrayList.add(s3);
        stubArrayList.add(s2);

        assertEquals(0, tempBoard.bottomDeckableRowSize());
        assertEquals(stubArrayList.getFirst(), tempBoard.getFromResolveOtherEventsRow(0));
        assertEquals(stubArrayList.get(1), tempBoard.getFromResolveOtherEventsRow(1));

        stubArrayList = new ArrayList<>();
        stubArrayList.add(g2);
        stubArrayList.add(g3);

        assertEquals(stubArrayList.getFirst(), tempBoard.getFromResolveSustenanceEventRow(0));
        assertEquals(stubArrayList.get(1), tempBoard.getFromResolveSustenanceEventRow(1));
    }

    @Test
    void discardBuildingFromBottom() {
        ArrayList<Building> topRow1 = new ArrayList<>();
        Card card1 = new ImmediateBuilding(2, 2, 2, 3, null);
        topRow1.add((Building) card1);
        Card card2 = new ImmediateBuilding(2, 2, 2, 4, null);
        topRow1.add((Building) card2);

        Board tempBoard = new Board(null,
                null,
                null,
                null,
                null,
                null,
                null,
                topRow1,
                null,
                null);

        tempBoard.discardBuildingFromBottom();

        assertEquals(0, tempBoard.getBottomBuildingRow().size());
    }

    @Test
    void discardEvents() {
        ArrayList<EventCard> otherEvents = new ArrayList<>();
        EventCard event = new ShamanEvent(2, 1);
        otherEvents.add(event);
        otherEvents.add(new HuntEvent(2, 2));
        ArrayList<EventCard> sustenanceEvents = new ArrayList<>();
        sustenanceEvents.add(new SustenanceEvent(2, 3));
        sustenanceEvents.add(new SustenanceEvent(1, 5));

        Board tempBoard = new Board(null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                sustenanceEvents,
                otherEvents);

        tempBoard.discardEvents();

        assertEquals(0, tempBoard.toResolveOtherEventsRowSize());
        assertEquals(0, tempBoard.toResolveSustenanceEventsRowSize());
    }

    @Test
    void pickDeckableAndAddToTop() {
        boardStub.pickDeckableAndAddToTop(numPlayers);

        assertEquals(6, boardStub.topDeckableRowSize());
    }

    @Test
    void pickBuildingAndAddToTop() {
        int numBuildings = boardStub.getBuildingDeck().getBuildings(1).size();
        ArrayList<Building> buildings = new ArrayList<>(boardStub.getBuildingDeck().getBuildings(1));

        boardStub.pickBuildingAndAddToTop(1);

        assertEquals(numBuildings, boardStub.getTopBuildingRow().size());
        assertEquals(buildings, boardStub.getTopBuildingRow());
    }

    @Test
    void findPlayerPosition() {
        Player p1 = new Player("P1", TotemColor.WHITE);
        Player p2 = new Player("P2", TotemColor.RED);

        boardStub.getOffersPath().offersPathGet('B').setPlayerTotem(p1);
        boardStub.getOffersPath().offersPathGet('E').setPlayerTotem(p2);

        assertEquals(Optional.of('B'), boardStub.findPlayerPosition("P1"));
        assertEquals(Optional.of('E'), boardStub.findPlayerPosition("P2"));
        assertTrue(boardStub.findPlayerPosition("Unknown").isEmpty());
    }
}