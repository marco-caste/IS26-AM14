package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.NetworkLayer;
import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.view.ClientModel;

import java.util.List;

public class EndGameEvent extends UpdateEvent {
    EndRoundEvent endRoundEvent;

    public EndGameEvent(final Board board, final List<Player> finalRanking, int gameId) {
        super("");
        this.endRoundEvent = new EndRoundEvent(board, finalRanking, gameId);
        // gameId is only used for bufferization, will not be sent to the client
        this.description = "End of game, the board has been updated, the final ranking has been calculated";
    }

    @JsonCreator
    public EndGameEvent(
            @JsonProperty("description") String description,
            @JsonProperty("event") EndRoundEvent endRoundEvent) {
        super(description);
        this.endRoundEvent = endRoundEvent;
    }

    public void bufferize(NetworkLayer networkLayer) {
        networkLayer.broadcastUpdate(this, endRoundEvent.getGameId());
    }

    @Override
    public void effect(ClientModel model) {
        model.setBoard(endRoundEvent.getBoard());
        model.updatePlayerRoundAttributes(endRoundEvent.getPlayerAttributes());
        model.setMessage(description);
        model.notifyObservers();
    }
}
