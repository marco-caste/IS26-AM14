package it.polimi.ingsw.am14.model.boardComponents;

import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.model.cards.deckCards.DeckCard;
import it.polimi.ingsw.am14.model.cards.deckCards.ShamanEvent;
import it.polimi.ingsw.am14.model.cards.deckCards.SustenanceEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

// TESTCASE NOT WORKING
/*
    it thoroughly tested the TribeDeck constructor by exploiting a previous error
    which was having left TribeDeck.deckableDeck as a non - private field;
    the interface tests are still working
 */


class TribeDeckTest {

    private TribeDeck deck;
    private static final int numPlayers = 3;
    ArrayList<DeckCard> cards;

    @BeforeEach
    void setUp() {
        deck = new TribeDeck(numPlayers);
//        cards = new ArrayList<>(deck.deckableDeck);
    }

    /*
    @Test
    void constructorTest() {
        // first position: age 1
        // note: converting the deck to ArrayList flips the order of the cards
        // hence why it searches for final events as the first two cards

        assertEquals(
                1,
                cards.getLast().getAge()
        );

        // last 2 cards are final shaman event and final sustenance event
        assertEquals(
                3,
                cards.get(1).getAge()
        );
        assertEquals(
                94,
                cards.get(1).getId()
        );
        assertInstanceOf(
                ShamanEvent.class,
                cards.get(1)
        );


        assertEquals(
                3,
                cards.getFirst().getAge()
        );
        assertEquals(
                95,
                cards.getFirst().getId()
        );
        assertInstanceOf(
                SustenanceEvent.class,
                cards.getFirst()
        );

        // shuffle tests
        Card card1 = cards.getFirst();
        deck = new TribeDeck(numPlayers);

        // the following assertion may not always verify,
        // but it must be verified at least once
        assertNotEquals(
                card1, deck.deckPop()
        );

        Card firstAge2Card;
        while(true) {
            if (cards.getFirst().getAge() == 2) {
                firstAge2Card = cards.getFirst();
                break;
            }
            cards.removeFirst();
        }
        cards = new ArrayList<>(deck.deckableDeck);
        while(true) {
            if (cards.getFirst().getAge() == 2) {
                card1 = cards.getFirst();
                break;
            }
            cards.removeFirst();
        }

        // the following assertion may not always verify,
        // but it must be verified at least once
        assertNotEquals(firstAge2Card, card1);
    }
    */

    @Test
    void deckPop() {
        int previousSize = deck.deckSize();
        Card stub = deck.deckPop();

        assertEquals(previousSize - 1, deck.deckSize());
    }

    @Test
    void deckSize() {
        deck = new TribeDeck(5);
        assertEquals(96, deck.deckSize());
    }

    @Test
    void deckPeek() {
        int previousSize = deck.deckSize();
        Card firstCardPeeked = deck.deckPeek();
        assertEquals(previousSize, deck.deckSize());
    }
}

