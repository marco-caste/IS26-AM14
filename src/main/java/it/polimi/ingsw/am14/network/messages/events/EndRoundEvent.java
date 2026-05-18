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

public class EndRoundEvent extends UpdateEvent {

    // attributes
    private final ArrayList<ArrayList<Integer>> board;
    private final Map<String, ArrayList<Integer>> playersAttributes;          // pos0 = numFood, pos1 = numPrestigePoints
    @JsonIgnore
    private int gameId; // only used for bufferization, will not be sent to the client
    private final int currentAge;

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
        this.currentAge = currentAge; // TODO: verifica come si comporta con json

    }

    // called by the JSON parser
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


    public int getGameId() {
        return gameId;
    }

    public void bufferize(NetworkLayer networkLayer) {
        networkLayer.broadcastUpdate(this, gameId);
    }

    @Override
    public void effect(ClientModel model) {
        model.setBoard(board);
        model.updatePlayerRoundAttributes(playersAttributes);
        model.setMessage(description);
        model.updateAge(currentAge);
        model.setPlaceState();
        model.notifyObservers(new EndRoundViewEvent());
    }


    //getter
    public ArrayList<ArrayList<Integer>>  getBoard() {
        return board;
    }

    public Map<String,ArrayList<Integer>> getPlayerAttributes()  {
        return playersAttributes;
    }
}
