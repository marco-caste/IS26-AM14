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

public class EndGameEvent extends UpdateEvent {

    private final ArrayList<ArrayList<Integer>> board;
    private final Map<String, ArrayList<Integer>> playersAttributes;
    private ArrayList<String> finalRank = new ArrayList<>();
    @JsonIgnore
    private int gameId;

    public EndGameEvent(final Board board, final List<Player> finalRanking, int gameId) {
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
        this.description = "End of game, the board has been updated, the final ranking has been calculated";
    }

    @JsonCreator
    public EndGameEvent(
            @JsonProperty("description") String description,
            @JsonProperty("finalRank") ArrayList<String> finalRank,
            @JsonProperty("board") ArrayList<ArrayList<Integer>> board,
            @JsonProperty("playersAttributes") Map<String, ArrayList<Integer>> playersAttributes){
        super(description);
        this.board = board;
        this.playersAttributes = playersAttributes;
        this.finalRank= finalRank;
    }

    public void bufferize(NetworkLayer networkLayer) {
        networkLayer.broadcastUpdate(this, this.gameId);
    }

    @Override
    public void effect(ClientModel model) {
        model.setBoard(this.board);
        model.updatePlayerRoundAttributes(this.playersAttributes);
        model.setFinalRank(this.finalRank);
        //TODO: model.setGameId(-1);
        model.notifyObservers( new EndGameViewEvent(description));
    }
}
