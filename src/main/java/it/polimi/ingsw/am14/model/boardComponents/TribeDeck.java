package it.polimi.ingsw.am14.model.boardComponents;

import it.polimi.ingsw.am14.model.cards.deckCards.*;
import it.polimi.ingsw.am14.utils.JsonCodec;

import java.util.*;
import java.util.stream.Collectors;

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
    public TribeDeck(int numPlayers){
        this.deckableDeck = new Stack<>();
        // reads values from JSON data file, puts DeckCards in the corresponding eras;
        Map<Integer, ArrayList<DeckCard>> decks = new HashMap<>();

        JsonCodec jsonCodec = new JsonCodec();

        decks.put(1, jsonCodec.readDeckCards("src/main/resources/DeckCards.json", numPlayers).stream().
                filter(c -> c.getAge() == 1).
                collect(Collectors.toCollection(ArrayList::new)));
        decks.put(2, jsonCodec.readDeckCards("src/main/resources/DeckCards.json", numPlayers).stream().
                filter(c -> c.getAge() == 2).
                collect(Collectors.toCollection(ArrayList::new)));
        decks.put(3, jsonCodec.readDeckCards("src/main/resources/DeckCards.json", numPlayers).stream().
                filter(c -> c.getAge() == 3).
                collect(Collectors.toCollection(ArrayList::new)));

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
