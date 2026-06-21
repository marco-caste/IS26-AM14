package it.polimi.ingsw.am14.network.messages.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.clientModelController.ClientModel;
import it.polimi.ingsw.am14.clientModelController.ClientPlayer;
import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.view.viewEvents.PlayerReconnectedViewEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static it.polimi.ingsw.am14.utils.Constants.*;
import static it.polimi.ingsw.am14.utils.Constants.BOTTOM_BUILDING_ROW;
/**
 * Event representing a player successfully reconnecting to an ongoing game.
 * Inherits from {@link ServerEvent}.
 * It contains the full state of the game, including the board configuration,
 * player attributes, and their positions, in order to synchronize the reconnected client.
 */
public class PlayerReconnectedEvent extends ServerEvent {

    // TODO: AGGIUNGERE AGE
    private final int gameId;
    private final int turnOrderCardIndex;
    private final int numPlayers;
    private final ArrayList<ArrayList<Integer>> board;
    private final String playerNickname;
    private final Map<String, String> nicknameToTotemColor;
    private final Map<String, Integer> nicknameToNumFood;
    private final Map<String, Integer> nicknameToNumPrestigePoints;
    private final Map<String, Integer> nicknameToNumStars;
    private final Map<String, Integer> nicknameToTotalBuildingDiscount;
    private final Map<String, Integer> nicknameToTotalGatheringDiscount;
    private final Map<String, Character> nicknameToPlayerPosition;
    private final ArrayList<String> playersOrderInTurnOrderCard;
    private final Map<String, int []> nicknameToPlayerTribe;

    /**
     * Constructs a {@code PlayerReconnectedEvent} triggered by the server model when a player reconnects.
     *
     * @param playerNickname the nickname of the reconnected player.
     * @param gameId         the unique ID of the game the player reconnected to.
     * @param board          the current state of the game {@link Board}.
     * @param numPlayers     the total number of players in the game.
     * @param players        the list of {@link Player} instances currently in the game.
     */

    public PlayerReconnectedEvent(String playerNickname, int gameId, Board board, int numPlayers, List<Player> players) {
        super("Player \"" + playerNickname + "\" has re-established their connection to game [" + gameId + "].");
        this.playerNickname = playerNickname;
        this.gameId = gameId;
        this.turnOrderCardIndex = numPlayers - 2;
        // TurnOrderCards are indexed from 0 to 3 as numPlayers goes from 2 to 5.
        this.numPlayers = numPlayers;
        // the offersPath will be built by the View using numPlayers.

        this.board = new ArrayList<>(4);
        this.board.add(TOP_DECKABLE_ROW,
                board.getTopDeckableRow().stream()
                        .map(Card::getId)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
        this.board.add(TOP_BUILDING_ROW,
                board.getTopBuildingRow().stream()
                        .map(Card::getId)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
        this.board.add(BOTTOM_DECKABLE_ROW,
                board.getBottomDeckableRow().stream()
                        .map(Card::getId)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
        this.board.add(BOTTOM_BUILDING_ROW,
                board.getBottomBuildingRow().stream()
                        .map(Card::getId)
                        .collect(Collectors.toCollection(ArrayList::new))
        );

        this.playersOrderInTurnOrderCard = board.getTurnOrderCard().getPlayerOrder().stream()
                .map(
                        (player) -> {
                            // in the turnOrderCard we admit null values for players that are in the offersPath
                            // and thus not in the turnOrderCard. In this case we return an empty string for the nickname.
                            if (player == null) {
                                return "";
                            } else {
                                return player.getNickname();
                            }
                        }
                )
                .collect(Collectors.toCollection(ArrayList::new));

        nicknameToTotemColor = new HashMap<>();
        nicknameToNumFood = new HashMap<>();
        nicknameToNumPrestigePoints = new HashMap<>();
        nicknameToNumStars = new HashMap<>();
        nicknameToTotalBuildingDiscount = new HashMap<>();
        nicknameToTotalGatheringDiscount = new HashMap<>();
        nicknameToPlayerPosition = new HashMap<>();
        nicknameToPlayerTribe = new HashMap<>();

        players.forEach(player -> {
            nicknameToTotemColor.put(player.getNickname(), player.getTotemColor().toString());
            nicknameToNumFood.put(player.getNickname(), player.getNumFood());
            nicknameToNumPrestigePoints.put(player.getNickname(), player.getNumPrestigePoints());
            nicknameToNumStars.put(player.getNickname(), player.getNumStars());
            nicknameToTotalBuildingDiscount.put(player.getNickname(), player.getTotalBuildingDiscount());
            nicknameToTotalGatheringDiscount.put(player.getNickname(), player.getTotalGatheringDiscount());
            nicknameToPlayerPosition.put(player.getNickname(), board.findPlayerPosition(player.getNickname()).orElse(null));
            nicknameToPlayerTribe.put(player.getNickname(), player.getAllIds());
        });
    }

    /**
     * Constructs a {@code PlayerReconnectedEvent} from JSON data, used by Jackson upon receiving a message.
     *
     * @param description                      the description of the event.
     * @param gameId                           the unique ID of the game.
     * @param turnOrderCardIndex               the index of the turn order card based on the number of players.
     * @param numPlayers                       the total number of players in the game.
     * @param playerNickname                   the nickname of the reconnected player.
     * @param board                            the current state of the board.
     * @param nicknameToTotemColor             mapping of nicknames to their chosen totem colors.
     * @param nicknameToNumFood                mapping of nicknames to their current food amounts.
     * @param nicknameToNumPrestigePoints      mapping of nicknames to their current prestige points.
     * @param nicknameToNumStars               mapping of nicknames to their current stars.
     * @param nicknameToTotalBuildingDiscount  mapping of nicknames to their building discount.
     * @param nicknameToTotalGatheringDiscount mapping of nicknames to their gathering discount.
     * @param nicknameToPlayerPosition         mapping of nicknames to their current positions on the board.
     * @param playersOrderInTurnOrderCard      the turn order of the players.
     * @param nicknameToPlayerTribe            mapping of nicknames to their built tribe cards IDs.
     */

    @JsonCreator
    public PlayerReconnectedEvent(
            @JsonProperty("description") String description,
            @JsonProperty("gameId") int gameId,
            @JsonProperty("turnOrderCardIndex") int turnOrderCardIndex,
            @JsonProperty("numPlayers") int numPlayers,
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("board") ArrayList<ArrayList<Integer>> board,
            @JsonProperty("nicknameToTotemColor") Map<String, String> nicknameToTotemColor,
            @JsonProperty("nicknameToNumFood") Map<String, Integer> nicknameToNumFood,
            @JsonProperty("nicknameToNumPrestigePoints") Map<String, Integer> nicknameToNumPrestigePoints,
            @JsonProperty("nicknameToNumStars") Map<String, Integer> nicknameToNumStars,
            @JsonProperty("nicknameToTotalBuildingDiscount") Map<String, Integer> nicknameToTotalBuildingDiscount,
            @JsonProperty("nicknameToTotalGatheringDiscount") Map<String, Integer> nicknameToTotalGatheringDiscount,
            @JsonProperty("nicknameToPlayerPosition") Map<String, Character> nicknameToPlayerPosition,
            @JsonProperty("playersOrderInTurnOrderCard") ArrayList<String> playersOrderInTurnOrderCard,
            @JsonProperty("nicknameToPlayerTribe") Map<String, int []> nicknameToPlayerTribe) {
        super(description);
        this.gameId = gameId;
        this.turnOrderCardIndex = turnOrderCardIndex;
        this.numPlayers = numPlayers;
        this.playerNickname = playerNickname;
        this.board = board;
        this.nicknameToTotemColor = nicknameToTotemColor;
        this.nicknameToNumFood = nicknameToNumFood;
        this.nicknameToNumPrestigePoints = nicknameToNumPrestigePoints;
        this.nicknameToNumStars = nicknameToNumStars;
        this.nicknameToTotalBuildingDiscount = nicknameToTotalBuildingDiscount;
        this.nicknameToTotalGatheringDiscount = nicknameToTotalGatheringDiscount;
        this.nicknameToPlayerPosition = nicknameToPlayerPosition;
        this.playersOrderInTurnOrderCard = playersOrderInTurnOrderCard;
        this.nicknameToPlayerTribe = nicknameToPlayerTribe;
    }

    /**
     * Bufferizes the event into the network layer to be sent exclusively
     * to the player who just reconnected.
     *
     * @param network the network layer used to buffer the event for the specific client.
     */

    @Override
    public void bufferize(NetworkLayer network) {
        network.addToEventBuffer(network.getClientBy(playerNickname), this);
    }

    /**
     * Applies the effect of this event to the provided client model, completely restoring
     * the game state (players, board, points, resources, tribe cards) for the reconnected client
     * and notifying the view observers.
     *
     * @param model the {@link ClientModel} representing the client-side state of the game.
     */

    @Override
    public void effect(ClientModel model) {
        // use isEmpty because model.getHomePlayerNickname does not return null values;
        // in case homePlayer is null, it returns an empty String.

        nicknameToTotemColor.forEach((nickname, totemColor) -> {
            ClientPlayer player = new ClientPlayer(nickname);
            if (!nickname.equals(model.getHomePlayerNickname())) {
                model.addPlayer(player);
            } else {
                model.setHomePlayer(player);
            }
            player.setTotemColor(totemColor);
        });
        nicknameToNumFood.forEach((nickname, numFood) ->
                model.getPlayerByNickname(nickname).setNumFood(numFood));
        nicknameToNumPrestigePoints.forEach((nickname, numPrestigePoints) ->
                model.getPlayerByNickname(nickname).setNumPrestigePoints(numPrestigePoints));
        nicknameToNumStars.forEach((nickname, numStars) ->
                model.getPlayerByNickname(nickname).setNumStars(numStars));
        nicknameToTotalBuildingDiscount.forEach((nickname, totalBuildingDiscount) ->
                model.getPlayerByNickname(nickname).setTotalBuildingDiscount(totalBuildingDiscount));
        nicknameToTotalGatheringDiscount.forEach((nickname, totalGatheringDiscount) ->
                model.getPlayerByNickname(nickname).setTotalGatheringDiscount(totalGatheringDiscount));
        nicknameToPlayerPosition.forEach(model::updatePlayerPosition);
        nicknameToPlayerTribe.forEach((nickname, playerTribe) -> {
            for (int tribeElement : playerTribe) {
                model.getPlayerByNickname(nickname).addToPlayerTribe(tribeElement);
            }
        });
        model.setBoard(board);
        model.setNumPlayers(numPlayers);
        model.setGameId(gameId);
        model.setTurnOrderCardIndex(turnOrderCardIndex);
        model.setPlayerOrderInTurnOrderCard(playersOrderInTurnOrderCard);
        model.notifyObservers(new PlayerReconnectedViewEvent(description));
    }
}
