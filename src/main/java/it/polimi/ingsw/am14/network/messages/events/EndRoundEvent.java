package it.polimi.ingsw.am14.network.messages.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.view.viewEvents.EndRoundViewEvent;
import it.polimi.ingsw.am14.clientModelController.ClientModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Event representing the end of a round during the game.
 * Inherits from {@link UpdateEvent}.
 * It contains the updated state of the board, the updated attributes
 * for all players, and the current age of the game.
 */

public class EndRoundEvent extends UpdateEvent {

    // attributes
    private final ArrayList<ArrayList<Integer>> board;
    private final Map<String, ArrayList<Integer>> playersAttributes;          // pos0 = numFood, pos1 = numPrestigePoints
    @JsonIgnore
    private int gameId; // only used for bufferization, will not be sent to the client
    private final int currentAge;

    /**
     * Constructs an {@code EndRoundEvent} triggered by the server model at the end of a round.
     *
     * @param board      the current {@link Board} state at the end of the round.
     * @param players    the list of {@link Player} objects participating in the game.
     * @param gameId     the unique ID of the game this event belongs to.
     * @param currentAge the updated current age of the game.
     */

    public EndRoundEvent(final Board board,final List<Player> players, int gameId, int currentAge) {
        this.gameId = gameId;
        super("");

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

        for (Player player : players) {
            playersAttributes.put(player.getNickname(), new ArrayList<>());
            playersAttributes.get(player.getNickname()).add(player.getNumFood());
            playersAttributes.get(player.getNickname()).add(player.getNumPrestigePoints());
        }

        this.description = "End of round, the board has been updated";
        this.currentAge = currentAge;

    }

    // called by the JSON parser
    /**
     * Constructs an {@code EndRoundEvent} from JSON data, used by Jackson upon receiving a message.
     *
     * @param board             the state of the board represented by lists of card IDs.
     * @param playersAttributes a map associating each player's nickname with a list of their attributes (food and prestige points).
     * @param description       the description of the event.
     * @param currentAge        the current age of the game.
     */

    @JsonCreator
    public EndRoundEvent(
            @JsonProperty("board") ArrayList<ArrayList<Integer>> board,
            @JsonProperty("playersAttributes") Map<String, ArrayList<Integer>> playersAttributes,
            @JsonProperty("description") String description,
            @JsonProperty("currentAge") int currentAge) {
        super(description);
        this.board = board;
        this.playersAttributes = playersAttributes;
        this.currentAge = currentAge;
    }

    /**
     * Gets the unique identifier of the game.
     *
     * @return the game ID.
     */

    public int getGameId() {
        return gameId;
    }

    /**
     * Bufferizes the event into the network layer to be broadcasted to all connected clients.
     *
     * @param networkLayer the network layer used to broadcast the event.
     */

    public void bufferize(NetworkLayer networkLayer) {
        networkLayer.broadcastUpdate(this, gameId);
    }

    /**
     * Applies the effect of this event to the provided client model, updating the local side attributes
     * for the new round and notifying the view observers.
     *
     * @param model the {@link ClientModel} representing the client-side state of the game.
     */

    @Override
    public void effect(ClientModel model) {
        model.setBoard(board);
        model.updatePlayerRoundAttributes(playersAttributes);
//        model.setMessage(description);
        model.updateAge(currentAge);
        model.setPlaceState();
        model.notifyObservers(new EndRoundViewEvent(description));
    }



    /**
     * Gets the current board state represented by multi-dimensional lists of card IDs.
     *
     * @return an {@code ArrayList} containing lists of integer card IDs for each row on the board.
     */

    public ArrayList<ArrayList<Integer>>  getBoard() {
        return board;
    }

    /**
     * Gets the current attributes associated with each player.
     *
     * @return a map associating player nicknames with a list containing their food and prestige points.
     */

    public Map<String,ArrayList<Integer>> getPlayerAttributes()  {
        return playersAttributes;
    }
}
