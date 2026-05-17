package it.polimi.ingsw.am14.network.messages.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.clientModelController.ClientPlayer;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.view.viewEvents.GameChoiceEvent;
import it.polimi.ingsw.am14.clientModelController.ClientModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static it.polimi.ingsw.am14.utils.Constants.*;

public class JoinedGameEvent extends ServerEvent {
    private final String playerNickname;
    private final String totemColor;
    private final int gameId;
    private final int turnOrderCardIndex;
    private final int numPlayers;
    private final ArrayList<ArrayList<Integer>> board;
    private final Map<String, String> otherPlayersInfo; // nickname -> totemColor

    public JoinedGameEvent(Player player, List<Player> players, int gameId, final Board initialBoard, int numPlayers) {
        super("Player \"" + player.getNickname() + "\" has joined game id: " + gameId + ".");
        this.playerNickname = player.getNickname();
        this.totemColor = player.getTotemColor().name();
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

        this.otherPlayersInfo = players.stream()
                .filter(p -> !p.getNickname().equals(playerNickname))
                .collect(Collectors.toMap(Player::getNickname, p -> p.getTotemColor().toString()));
    }

    @JsonCreator
    public JoinedGameEvent(
            @JsonProperty("description") String description,
            @JsonProperty("gameId") int gameId,
            @JsonProperty("turnOrderCard") int turnOrderCardIndex,
            @JsonProperty("numPlayers") int numPlayers,
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("totemColor") String totemColor,
            @JsonProperty("board") ArrayList<ArrayList<Integer>> board,
            @JsonProperty("otherPlayersInfo") Map<String, String> otherPlayersInfo) {
        super(description);
        this.gameId = gameId;
        this.turnOrderCardIndex = turnOrderCardIndex;
        this.numPlayers = numPlayers;
        this.playerNickname = playerNickname;
        this.totemColor = totemColor;
        this.board = board;
        this.otherPlayersInfo = otherPlayersInfo;
    }


    @Override
    public void effect(ClientModel model) {
        model.setMessage(description);

        //The event is received by the player that has just joined
        if (model.getHomePlayerNickname().equals(playerNickname)) {
            model.setGameId(gameId);
            model.setTurnOrderCardIndex(turnOrderCardIndex);
            model.setNumPlayers(numPlayers);
            model.setBoard(board);
            model.setHomePlayerTotemColor(totemColor);
            otherPlayersInfo.forEach((nickname, color) -> {
                ClientPlayer player = new ClientPlayer(nickname);
                player.setTotemColor(color);
                model.addPlayer(player);
            });
        }

        //The event is received by the players already in the game
        else {
            model.addPlayer(new ClientPlayer(playerNickname));
            model.getPlayerByNickname(playerNickname).setTotemColor(totemColor);
        }
        model.notifyObservers(new GameChoiceEvent()); // we notify the player view
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
        network.broadcastUpdate(this, gameId);
    }
}
