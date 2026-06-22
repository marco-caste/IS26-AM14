package it.polimi.ingsw.am14.network.messages.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.view.viewEvents.EndGameViewEvent;
import it.polimi.ingsw.am14.clientModelController.ClientModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Event representing the end of a game.
 * Inherits from {@link UpdateEvent}.
 * It contains the final state of the board, the final attributes of the players,
 * the game's final ranking, and the overall global ranking.
 */

public class EndGameEvent extends UpdateEvent {

    private final ArrayList<ArrayList<Integer>> board;
    private final Map<String, ArrayList<Integer>> playersAttributes;
    private ArrayList<String> finalRank = new ArrayList<>();
    private final List<String> globalRank;
    @JsonIgnore
    private int gameId;

    /**
     * Constructs an {@code EndGameEvent} triggered by the server model at the end of the game.
     *
     * @param board        the final {@link Board} state of the game.
     * @param finalRanking a list of {@link Player} objects ordered by their final score.
     * @param gameId       the unique ID of the game this event belongs to.
     * @param globalRank   the updated global ranking across the server.
     */

    public EndGameEvent(final Board board, final List<Player> finalRanking, int gameId, final List<String> globalRank) {
        super("");
        this.gameId = gameId;

        // board is updated for everyone
        this.board = new ArrayList<>();
        this.board.add(
                board.getTopDeckableRow().stream()
                        .map(Card::getId)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
        this.board.add(
                board.getTopBuildingRow().stream()
                        .map(Card::getId)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
        this.board.add(
                board.getBottomDeckableRow().stream()
                        .map(Card::getId)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
        this.board.add(
                board.getBottomBuildingRow().stream()
                        .map(Card::getId)
                        .collect(Collectors.toCollection(ArrayList::new))
        );

        // playersAttributes is updated for everyone
        playersAttributes = new HashMap<>();
        for (Player player : finalRanking) {
            playersAttributes.put(player.getNickname(), new ArrayList<>());
            finalRank.add(player.getNickname());
            playersAttributes.get(player.getNickname()).add(player.getNumFood());
            playersAttributes.get(player.getNickname()).add(player.getNumPrestigePoints());
        }
        this.globalRank = globalRank;
        this.description = "End of game, the board has been updated, the final ranking has been calculated";
    }

    /**
     * Constructs an {@code EndGameEvent} from JSON data, used by Jackson upon receiving a message.
     *
     * @param description       the description of the event.
     * @param finalRank         the list of player nicknames ordered by their final placement.
     * @param board             the state of the board represented by card IDs.
     * @param playersAttributes a map associating each player's nickname with a list of their attributes.
     * @param globalRank        the overall global ranking leaderboard.
     */

    @JsonCreator
    public EndGameEvent(
            @JsonProperty("description") String description,
            @JsonProperty("finalRank") ArrayList<String> finalRank,
            @JsonProperty("board") ArrayList<ArrayList<Integer>> board,
            @JsonProperty("playersAttributes") Map<String, ArrayList<Integer>> playersAttributes,
            @JsonProperty("globalRank") List<String> globalRank){
        super(description);
        this.board = board;
        this.playersAttributes = playersAttributes;
        this.finalRank= finalRank;
        this.globalRank = globalRank;
    }

    /**
     * Bufferizes the event into the network layer to be broadcasted to all connected clients.
     *
     * @param networkLayer the network layer used to broadcast the event.
     */

    public void bufferize(NetworkLayer networkLayer) {
        networkLayer.broadcastUpdate(this, this.gameId);
    }

    /**
     * Applies the effect of this event to the provided client model, updating the local state
     * with the final game results and notifying the view observers.
     *
     * @param model the {@link ClientModel} representing the client-side state of the game.
     */

    @Override
    public void effect(ClientModel model) {
        model.setBoard(this.board);
        model.updatePlayerRoundAttributes(this.playersAttributes);
        model.setFinalRank(this.finalRank);
        model.setGlobalRank(this.globalRank);
        model.notifyObservers( new EndGameViewEvent(description));
    }
}
