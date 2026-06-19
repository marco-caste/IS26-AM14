package it.polimi.ingsw.am14.model.boardComponents;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.cards.deckCards.*;
import it.polimi.ingsw.am14.utils.Codec;
import it.polimi.ingsw.am14.utils.JsonCodec;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *     TribeDeck is a Stack of DeckCards, which are the cards that can be drawn by the players during the game.
 *     The TribeDeck is built from a JSON file, which contains all the DeckCards available in the game.
 *     The TribeDeck is shuffled and the final events are pushed first, then all the other cards are pushed.
 * </p>
 */
public class TribeDeck {
    /**
     * <p>
     *     the Stack of DeckCards that can be drawn by the players during the game
     * </p>
     */
    private final Stack<DeckCard> deckableDeck;

    /**
     * <p>
     *     builds the TribeDeck object from a data file, which contains all the DeckCards available in the game, accordingly to the game rules.
     * </p>
     * @param numPlayers the number of players in the game, used to filter the DeckCards that can be drawn
     */
    public TribeDeck(int numPlayers){
        this.deckableDeck = new Stack<>();
        // reads values from JSON data file, puts DeckCards in the corresponding eras;
        Map<Integer, ArrayList<DeckCard>> decks = new HashMap<>();

        Codec jsonCodec = new JsonCodec();

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

    /**
     * <p>
     *     builds the TribeDeck object from a Stack of DeckCards
     * </p>
     * @param deckableDeck the Stack of DeckCards to be used as the deck, must not be null
     */
    @JsonCreator
    public TribeDeck(
            @JsonProperty("deckableDeck") Stack<DeckCard> deckableDeck) {
        this.deckableDeck = deckableDeck;
    }

    // POP FROM DECK

    /**
     * <p>
     *     performs Stack.pop on the deck, meaning it returns the first DeckCard in this deck and removes it from the deck
     * </p>
     * @return the first DeckCard in this deck
     */
    public DeckCard deckPop() {
        return this.deckableDeck.pop();
    }

    // SIZE OF DECK
    public int deckSize() {
        return this.deckableDeck.size();
    }

    // PEEK FROM DECK

    /**
     * <p>
     *     performs Stack.peek on the deck, meaning it returns the first DeckCard in this deck without removing it
     * </p>
     * @return the first DeckCard in this deck
     */
    public DeckCard deckPeek() {
        return this.deckableDeck.peek();
    }
}
