//package it.polimi.ingsw.am14.model.boardComponents;
//
//import it.polimi.ingsw.am14.model.cards.Card;
//import it.polimi.ingsw.am14.model.cards.deckCards.DeckCard;
//import it.polimi.ingsw.am14.model.cards.deckCards.ShamanEvent;
//import it.polimi.ingsw.am14.model.cards.deckCards.SustenanceEvent;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//// TESTCASE NOT WORKING
///*
//    it thoroughly tested the TribeDeck constructor by exploiting a previous error
//    which was having left TribeDeck.deckableDeck as a non - private field;
//    the interface tests are still working
// */
//
//
//class TribeDeckTest {
//
//    private TribeDeck deck;
//    private static final int numPlayers = 3;
//    ArrayList<DeckCard> cards;
//
//    @BeforeEach
//    void setUp() {
//        deck = new TribeDeck(numPlayers);
////        cards = new ArrayList<>(deck.deckableDeck);
//    }
//
//
//    @Test
//    void constructorTest() {
//        // first position: age 1
//        // note: converting the deck to ArrayList flips the order of the cards
//        // hence why it searches for final events as the first two cards
//
//        assertEquals(
//                1,
//                cards.getLast().getAge()
//        );
//
//        // last 2 cards are final shaman event and final sustenance event
//        assertEquals(
//                3,
//                cards.get(1).getAge()
//        );
//        assertEquals(
//                94,
//                cards.get(1).getId()
//        );
//        assertInstanceOf(
//                ShamanEvent.class,
//                cards.get(1)
//        );
//
//
//        assertEquals(
//                3,
//                cards.getFirst().getAge()
//        );
//        assertEquals(
//                95,
//                cards.getFirst().getId()
//        );
//        assertInstanceOf(
//                SustenanceEvent.class,
//                cards.getFirst()
//        );
//
//        // shuffle tests
//        Card card1 = cards.getFirst();
//        deck = new TribeDeck(numPlayers);
//
//        // the following assertion may not always verify,
//        // but it must be verified at least once
//        assertNotEquals(
//                card1, deck.deckPop()
//        );
//
//        Card firstAge2Card;
//        while(true) {
//            if (cards.getFirst().getAge() == 2) {
//                firstAge2Card = cards.getFirst();
//                break;
//            }
//            cards.removeFirst();
//        }
//        cards = new ArrayList<>(deck.deckableDeck);
//        while(true) {
//            if (cards.getFirst().getAge() == 2) {
//                card1 = cards.getFirst();
//                break;
//            }
//            cards.removeFirst();
//        }
//
//        // the following assertion may not always verify,
//        // but it must be verified at least once
//        assertNotEquals(firstAge2Card, card1);
//    }
//
//
//    @Test
//    void deckPop() {
//        int previousSize = deck.deckSize();
//        Card stub = deck.deckPop();
//
//        assertEquals(previousSize - 1, deck.deckSize());
//    }
//
//    @Test
//    void deckSize() {
//        deck = new TribeDeck(5);
//        assertEquals(96, deck.deckSize());
//    }
//
//    @Test
//    void deckPeek() {
//        int previousSize = deck.deckSize();
//        Card firstCardPeeked = deck.deckPeek();
//        assertEquals(previousSize, deck.deckSize());
//    }
//}
//
package it.polimi.ingsw.am14.model.boardComponents;

import it.polimi.ingsw.am14.model.cards.deckCards.DeckCard;
import it.polimi.ingsw.am14.model.cards.deckCards.ShamanEvent;
import it.polimi.ingsw.am14.model.cards.deckCards.SustenanceEvent;
import org.junit.jupiter.api.Test;

import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.*;

class TribeDeckTest {

    @Test
    void constructor_shouldInitializeDeckAndContainCards() {
        // Arrange
        int numPlayers = 4;

        // Act
        TribeDeck tribeDeck = new TribeDeck(numPlayers);

        // Assert
        // Il deck deve essere stato popolato dal costruttore
        assertTrue(tribeDeck.deckSize() > 0, "Il deck dovrebbe contenere carte dopo la costruzione.");

        // La prima carta non deve essere null
        DeckCard topCard = tribeDeck.deckPeek();
        assertNotNull(topCard, "La carta in cima al deck non dovrebbe essere null.");
    }

    @Test
    void constructor_shouldPlaceFinalEventsAtBottom_soTheyAreLastDrawn() {
        // Arrange
        int numPlayers = 4;
        TribeDeck tribeDeck = new TribeDeck(numPlayers);

        // Act
        // Svuoto tutto il deck; le ultime due pescate dovrebbero essere gli eventi finali
        DeckCard lastDrawn = null;
        DeckCard secondLastDrawn = null;

        while (tribeDeck.deckSize() > 0) {
            secondLastDrawn = lastDrawn;
            lastDrawn = tribeDeck.deckPop();
        }

        // Assert
        // In base al costruttore:
        // push(finalSustenance), push(finalShaman), poi tutte le altre carte
        // quindi a deck svuotato le ultime due carte pescate devono essere:
        // penultima = finalShaman, ultima = finalSustenance
        assertNotNull(secondLastDrawn);
        assertNotNull(lastDrawn);

        assertTrue(secondLastDrawn instanceof ShamanEvent,
                "La penultima carta pescata dovrebbe essere ShamanEvent.");
        assertTrue(lastDrawn instanceof SustenanceEvent,
                "L'ultima carta pescata dovrebbe essere SustenanceEvent.");

        // opzionale: controllo ID/era come da costruttore
        assertEquals(3, secondLastDrawn.getAge(), "ShamanEvent finale dovrebbe avere age 3.");
        assertEquals(3, lastDrawn.getAge(), "SustenanceEvent finale dovrebbe avere age 3.");
        assertEquals(94, secondLastDrawn.getId(), "ShamanEvent finale dovrebbe avere id 94.");
        assertEquals(95, lastDrawn.getId(), "SustenanceEvent finale dovrebbe avere id 95.");
    }

    @Test
    void deckPop_shouldReduceSizeByOne() {
        // Arrange
        TribeDeck tribeDeck = new TribeDeck(3);
        int initialSize = tribeDeck.deckSize();

        // Act
        DeckCard popped = tribeDeck.deckPop();

        // Assert
        assertNotNull(popped, "La carta estratta non dovrebbe essere null.");
        assertEquals(initialSize - 1, tribeDeck.deckSize(),
                "Dopo una pop, la size dovrebbe diminuire di 1.");
    }

    @Test
    void deckPeek_shouldNotRemoveCard() {
        // Arrange
        TribeDeck tribeDeck = new TribeDeck(2);
        int initialSize = tribeDeck.deckSize();

        // Act
        DeckCard peeked = tribeDeck.deckPeek();

        // Assert
        assertNotNull(peeked, "La peek non dovrebbe restituire null.");
        assertEquals(initialSize, tribeDeck.deckSize(),
                "La peek non dovrebbe modificare la dimensione del deck.");
    }

    @Test
    void popOnEmptyDeck_shouldThrowException() {
        // Arrange
        TribeDeck tribeDeck = new TribeDeck(5);

        // svuoto il deck
        while (tribeDeck.deckSize() > 0) {
            tribeDeck.deckPop();
        }

        // Act + Assert
        assertThrows(EmptyStackException.class, tribeDeck::deckPop,
                "Pop su deck vuoto dovrebbe lanciare EmptyStackException.");
    }

    @Test
    void peekOnEmptyDeck_shouldThrowException() {
        // Arrange
        TribeDeck tribeDeck = new TribeDeck(5);

        // svuoto il deck
        while (tribeDeck.deckSize() > 0) {
            tribeDeck.deckPop();
        }

        // Act + Assert
        assertThrows(EmptyStackException.class, tribeDeck::deckPeek,
                "Peek su deck vuoto dovrebbe lanciare EmptyStackException.");
    }
    @Test
    void constructor_withFivePlayers_shouldCreateDeckWith96Cards() {
        // Arrange
        int numPlayers = 5;

        // Act
        TribeDeck tribeDeck = new TribeDeck(numPlayers);

        // Assert
        assertEquals(96, tribeDeck.deckSize(),
                "Con 5 giocatori il deck dovrebbe contenere esattamente 96 carte.");
    }

}
