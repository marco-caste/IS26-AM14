package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.clientModel.ClientModel;
import it.polimi.ingsw.am14.clientModel.ClientPlayer;
import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.network.NetworkLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static it.polimi.ingsw.am14.utils.Constants.*;
import static it.polimi.ingsw.am14.utils.Constants.BOTTOM_BUILDING_ROW;

public class PlayerReconnectedEvent extends ServerEvent {

    private final int gameId;
    private final int turnOrderCardIndex;
    private final int numPlayers;
    private final ArrayList<ArrayList<Integer>> board;
    private final String playerNickname;
    private final ArrayList<ClientPlayer> players;

    public PlayerReconnectedEvent(String playerNickname, int gameId, final Board initialBoard, int numPlayers, List<Player> players) {
        super("Player \"" + playerNickname + "\" has re-established their connection to game [" + gameId + "].");
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

        this.players = new ArrayList<ClientPlayer>(numPlayers);
    }

    @JsonCreator
    public PlayerReconnectedEvent(
            @JsonProperty("description") String description,
            @JsonProperty("gameId") int gameId,
            @JsonProperty("turnOrderCardIndex") int turnOrderCardIndex,
            @JsonProperty("numPlayers") int numPlayers,
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("board") ArrayList<ArrayList<Integer>> board,
            @JsonProperty("players") ArrayList<ClientPlayer> players) {
        super(description);
        this.gameId = gameId;
        this.turnOrderCardIndex = turnOrderCardIndex;
        this.numPlayers = numPlayers;
        this.playerNickname = playerNickname;
        this.board = board;
        this.players = players;
    }

    @Override
    public void bufferize(NetworkLayer network) {
        network.addToEventBuffer(network.getClientBy(playerNickname), this);
    }

    @Override
    public void effect(ClientModel model) {
        model.setMessage(description);
        if (model.getHomePlayer() == null) {
            model.setHomePlayer(new ClientPlayer(playerNickname));
        }
        model.notifyObservers("playerReconnected");
    }
}
