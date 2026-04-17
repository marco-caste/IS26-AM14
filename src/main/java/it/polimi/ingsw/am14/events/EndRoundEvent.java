package it.polimi.ingsw.am14.events;

import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.model.gameLogic.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class EndRoundEvent extends UpdateEvent {
    // constants
    private static final int TOP_DECKABLE_ROW = 0;
    private static final int TOP_BUILDING_ROW = 1;
    private static final int BOTTOM_DECKABLE_ROW = 2;
    private static final int BOTTOM_BUILDING_ROW = 3;

    // attributes
    private final ArrayList<ArrayList<Integer>> board;
    private final Map<String, ArrayList<Integer>> playersAttributes;          // pos0 = numFood, pos1 = numPrestigePoints

    public EndRoundEvent(final Board board,final ArrayList<Player> players) {

        // board is updated for everyone
        this.board = new ArrayList<>();
        this.board.set(TOP_DECKABLE_ROW,
                board.getTopDeckableRow().stream()
                        .map(Card::getId)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
        this.board.set(TOP_BUILDING_ROW,
                board.getTopBuildingRow().stream()
                        .map(Card::getId)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
        this.board.set(BOTTOM_DECKABLE_ROW,
                board.getBottomDeckableRow().stream()
                        .map(Card::getId)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
        this.board.set(BOTTOM_BUILDING_ROW,
                board.getBottomDeckableRow().stream()
                        .map(Card::getId)
                        .collect(Collectors.toCollection(ArrayList::new))
        );

        // playersAttributes is updated for everyone
        playersAttributes = new HashMap<>();

        for (Player player : players) {
            playersAttributes.put(player.getNickname(), new ArrayList<>());
            playersAttributes.get(player.getNickname()).add(player.getNumFood());
            playersAttributes.get(player.getNickname()).add(player.getNumPrestigePoints());
        }

        this.description = "End of round, the board has been updated";
    }

    /*
    HOW TO USE:

    Alla fine di ogni round, il server invia un EndRoundEvent a tutti i giocatori della partita.
    Ogni giocatore scorrerà la lista dei giocatori e aggiornerà i propri attributi e quelli degli altri giocatori.
    Ogni giocatore aggiornerà la propria board.
     */
}
