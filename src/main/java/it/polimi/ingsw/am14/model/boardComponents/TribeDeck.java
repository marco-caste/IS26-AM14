package it.polimi.ingsw.am14.model.boardComponents;

import it.polimi.ingsw.am14.utils.CardManager;
import it.polimi.ingsw.am14.model.cards.deckCards.*;

import java.io.IOException;
import java.util.*;

public class TribeDeck {
    private final Stack<DeckCard> deckableDeck;

    /**
     * <p>
     *     builds the TribeDeck object:
     *     - retrieves data from Json using CardManager
     *     - shuffles the deck separately
     *     - pushes the final events first
     *     - then pushes all the other cards
     *
     * </p>
     * @param numPlayers between 2 and 5, included
     */
    public TribeDeck(int numPlayers) {
        this.deckableDeck = new Stack<>();
        // reads values from JSON data file, puts DeckCards in the corresponding eras;
        Map<Integer, ArrayList<DeckCard>> decks;
        try{
            decks = CardManager.buildDecksFromCardMap(CardManager.instantiateCardMap(CardManager.turnJsonIntoRawMap(numPlayers)));
        } catch (IOException e) {
            // the following lines are not tested;
            e.printStackTrace();
            return;
        }

        // shuffles the decks separately:
        Collections.shuffle(decks.get(1));
        Collections.shuffle(decks.get(2));
        Collections.shuffle(decks.get(3));

        // pushes the shuffled cards in the Queue;

        // SOLUZIONE PROVVISORIA
        ShamanEvent finalShaman = new ShamanEvent(3, 94);
        SustenanceEvent finalSustenance = new SustenanceEvent(3, 95);

        // push degli eventi finali
        this.deckableDeck.push(finalSustenance);
        this.deckableDeck.push(finalShaman);

        for (int i = 3; i >= 1; i--) {
            for (DeckCard c : decks.get(i)) {
                this.deckableDeck.push(c);
            }
        }
    }

    // POP FROM DECK

    /**
     * <p>
     *     performs Stack.pop on this.deckableDeck
     * </p>
     * @return the first DeckCard in this deck
     */
    public DeckCard deckPop() {
        return this.deckableDeck.pop();
    }

    // SIZE OF DECK

    /**
     *
     * @return this.deckableDeck.size()
     */
    public int deckSize() {
        return this.deckableDeck.size();
    }

    // PEEK FROM DECK

    /**
     * <p>
     *     performs Stack.peek on this.deckableDeck
     * </p>
     * @return the first DeckCard in this deck
     */
    public DeckCard deckPeek() {
        return this.deckableDeck.peek();
    }
}
