package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.NetworkLayer;
import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.Card;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class JoinedGameEvent extends ServerEvent {
    // constants
    private static final int TOP_DECKABLE_ROW = 0;
    private static final int TOP_BUILDING_ROW = 1;
    private static final int BOTTOM_DECKABLE_ROW = 2;
    private static final int BOTTOM_BUILDING_ROW = 3;

    // attributes
    private final String playerNickname;
    // TODO: totemColor
    private final int gameId;
    private final int turnOrderCardIndex;
    private final int numPlayers;
    private final ArrayList<ArrayList<Integer>> board;

    public JoinedGameEvent(String playerNickname, int gameId, final Board initialBoard, int numPlayers) {
        super("");
        this.playerNickname = playerNickname;
        this.gameId = gameId;
        this.turnOrderCardIndex = numPlayers - 2;
        // TurnOrderCards are indexed from 0 to 3 as numPlayers goes from 2 to 5.
        this.numPlayers = numPlayers;
        // the offersPath will be built by the View using numPlayers.

        this.board = new ArrayList<>();
        this.board.set(TOP_DECKABLE_ROW,
                initialBoard.getTopDeckableRow().stream()
                        .map(Card::getId)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
        this.board.set(TOP_BUILDING_ROW,
                initialBoard.getTopBuildingRow().stream()
                        .map(Card::getId)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
        this.board.set(BOTTOM_DECKABLE_ROW,
                initialBoard.getBottomDeckableRow().stream()
                        .map(Card::getId)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
        this.board.set(BOTTOM_BUILDING_ROW,
                initialBoard.getBottomDeckableRow().stream()
                        .map(Card::getId)
                        .collect(Collectors.toCollection(ArrayList::new))
        );

        this.description = "Player \"" + playerNickname + "\" has joined game [" + gameId + "].";
    }

    @JsonCreator
    public JoinedGameEvent(
            @JsonProperty("description") String description,
            @JsonProperty("nickname") String playerNickname,
            @JsonProperty("gameID") int gameId,
            @JsonProperty("turnOrderCard") int turnOrderCardIndex,
            @JsonProperty("numPlayers") int numPlayers,
            @JsonProperty("board") ArrayList<ArrayList<Integer>> board) {
        super(description);
        this.playerNickname = playerNickname;
        this.gameId = gameId;
        this.turnOrderCardIndex = turnOrderCardIndex;
        this.numPlayers = numPlayers;
        this.board = board;
    }

    /*
    HOW TO USE:

    Quando un giocatore si unisce alla partita, il server invia un JoinedGameEvent a tutti i giocatori già presenti nella partita.
    Se il giocatore destinatario è il giocatore che si è appena unito, inizializza clientBoard
    altrimenti, aggiunge un nuovo nickName alla lista dei giocatori e inizializza i suoi attributi (numFood, numPrestigePoints) a 0.
     */

    @Override
    public void bufferize(NetworkLayer network) {
        network.getClientBy(this.playerNickname).addToEventBuffer(this);
    }
}
