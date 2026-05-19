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

public class PlayerReconnectedEvent extends ServerEvent {

    //TODO: incompleta manca la turnOrderCard se c'è qualcuno sopra
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
    private final Map<String, int []> nicknameToPlayerTribe;

    public PlayerReconnectedEvent(String playerNickname, int gameId, final Board board, int numPlayers, List<Player> players) {
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
        this.nicknameToPlayerTribe = nicknameToPlayerTribe;
    }

    @Override
    public void bufferize(NetworkLayer network) {
        network.addToEventBuffer(network.getClientBy(playerNickname), this);
    }

    @Override
    public void effect(ClientModel model) {
//        model.setMessage(description);
        if (model.getHomePlayerNickname() == null) {
            nicknameToTotemColor.forEach((nickname, totemColor) -> {
                ClientPlayer player = new ClientPlayer(nickname);
                if (nickname.equals(nicknameToTotemColor.get(nickname))) {
                    model.setHomePlayer(player);
                } else {
                    model.addPlayer(player);
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
            nicknameToPlayerPosition.forEach((nickname, playerPosition) ->
                    model.getPlayerByNickname(nickname).setPlayerPosition(playerPosition));
            nicknameToPlayerTribe.forEach((nickname, playerTribe) -> {
                for (int tribeElement : playerTribe) {
                    model.getPlayerByNickname(nickname).addToPlayerTribe(tribeElement);
                }
            });
            model.setBoard(board);
            model.setNumPlayers(numPlayers);
            model.setGameId(gameId);
            model.setTurnOrderCardIndex(turnOrderCardIndex);
        }
        model.notifyObservers(new PlayerReconnectedViewEvent(description));
    }
}
