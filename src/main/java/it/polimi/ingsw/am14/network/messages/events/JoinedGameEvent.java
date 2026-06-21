package it.polimi.ingsw.am14.network.messages.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.clientModelController.ClientPlayer;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.clientModelController.ClientModel;
import it.polimi.ingsw.am14.view.viewEvents.JoinedGameViewEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static it.polimi.ingsw.am14.utils.Constants.*;

/**
 * Event representing a player joining a game.
 * Inherits from {@link ServerEvent}.
 * It contains the ID of the game, the player's information, the initial board state,
 * and information about the other players already in the game.
 */

public class JoinedGameEvent extends ServerEvent {
    private final String playerNickname;
    private final String totemColor;
    private final int gameId;
    private final int turnOrderCardIndex;
    private final int numPlayers;
    private final ArrayList<ArrayList<Integer>> board;
    private final Map<String, String> otherPlayersInfo; // nickname -> totemColor

    /**
     * Constructs a {@code JoinedGameEvent} triggered by the server model when a player joins the game.
     *
     * @param player       the {@link Player} who has just joined.
     * @param players      the list of all {@link Player} objects currently in the game.
     * @param gameId       the unique ID of the game.
     * @param initialBoard the initial state of the {@link Board}.
     * @param numPlayers   the total number of players expected in the game.
     */

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

    /**
     * Constructs a {@code JoinedGameEvent} from JSON data, used by Jackson upon receiving a message.
     *
     * @param description        the description of the event.
     * @param gameId             the unique ID of the game.
     * @param turnOrderCardIndex the index of the turn order card based on the number of players.
     * @param numPlayers         the total number of players in the game.
     * @param playerNickname     the nickname of the player who joined.
     * @param totemColor         the chosen totem color of the player.
     * @param board              the initial configuration of the board.
     * @param otherPlayersInfo   a map of the other players' nicknames and their corresponding totem colors.
     */

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


    /**
     * Applies the effect of this event to the provided client model.
     * Initializes the client board if the event receiver is the newly joined player,
     * otherwise adds the new player to the game state.
     *
     * @param model the {@link ClientModel} representing the client-side state of the game.
     */

    @Override
    public void effect(ClientModel model) {
//        model.setMessage(description);

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
        model.notifyObservers(new JoinedGameViewEvent(description)); // we notify the player view
    }

    /*
    HOW TO USE:

    Quando un giocatore si unisce alla partita, il server invia un JoinedGameEvent a tutti i giocatori già presenti nella partita.
    Se il giocatore destinatario è il giocatore che si è appena unito, inizializza clientBoard
    altrimenti, aggiunge un nuovo nickName alla lista dei giocatori e inizializza i suoi attributi (numFood, numPrestigePoints) a 0.
     */

    /**
     * Bufferizes the event into the network layer to be sent to the newly joined player
     * and broadcasted to all other connected clients in the game.
     *
     * @param network the network layer used to broadcast the event.
     */

    @Override
    public void bufferize(NetworkLayer network) {
        network.addToEventBuffer(network.getClientBy(playerNickname), this);
        network.broadcastUpdate(this, gameId);
    }
}
