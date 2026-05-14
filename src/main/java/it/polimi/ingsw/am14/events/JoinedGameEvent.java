package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.clientModel.ClientModel;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static it.polimi.ingsw.am14.utils.Constants.*;

public class JoinedGameEvent extends ServerEvent {

    // attributes
    @JsonIgnore
    private String playerNickname;
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

        this.board = new ArrayList<>(4);
        this.board.add(TOP_DECKABLE_ROW,
                initialBoard.getTopDeckableRow().stream()
                        .map(Card::getId)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
        this.board.add(TOP_BUILDING_ROW,
                initialBoard.getTopBuildingRow().stream()
                        .map(Card::getId)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
        this.board.add(BOTTOM_DECKABLE_ROW,
                initialBoard.getBottomDeckableRow().stream()
                        .map(Card::getId)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
        this.board.add(BOTTOM_BUILDING_ROW,
                initialBoard.getBottomBuildingRow().stream()
                        .map(Card::getId)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
        this.description = "Player \"" + playerNickname + "\" has joined game [" + gameId + "].";
    }

    @JsonCreator
    public JoinedGameEvent(
            @JsonProperty("description") String description,
            @JsonProperty("gameId") int gameId,
            @JsonProperty("turnOrderCard") int turnOrderCardIndex,
            @JsonProperty("numPlayers") int numPlayers,
            @JsonProperty("board") ArrayList<ArrayList<Integer>> board) {
        super(description);
        this.gameId = gameId;
        this.turnOrderCardIndex = turnOrderCardIndex;
        this.numPlayers = numPlayers;
        this.board = board;
    }


    @Override
    public void effect(ClientModel model) {
        model.setMessage(description);
        model.setGameId(gameId);
        model.setTurnOrderCardIndex(turnOrderCardIndex);
        model.setNumPlayers(numPlayers);
        model.setBoard(board);
        model.notifyObservers("lobbyUpdate"); // we notify the player view
    }

    /*
    HOW TO USE:

    Quando un giocatore si unisce alla partita, il server invia un JoinedGameEvent a tutti i giocatori già presenti nella partita.
    Se il giocatore destinatario è il giocatore che si è appena unito, inizializza clientBoard
    altrimenti, aggiunge un nuovo nickName alla lista dei giocatori e inizializza i suoi attributi (numFood, numPrestigePoints) a 0.
     */

    @Override
    public void bufferize(NetworkLayer network) {
        network.addToEventBuffer(network.getClientBy(playerNickname), this);
    }
}
