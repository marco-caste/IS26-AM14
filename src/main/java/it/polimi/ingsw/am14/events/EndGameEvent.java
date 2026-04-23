package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.view.ClientModel;

import java.util.List;

public class EndGameEvent extends UpdateEvent {
    EndRoundEvent endRoundEvent;

    public EndGameEvent(final Board board, final List<Player> finalRanking) {
        super("");
        this.endRoundEvent = new EndRoundEvent(board, finalRanking);
        this.description = "End of game, the board has been updated, the final ranking has been calculated";
    }

    @JsonCreator
    public EndGameEvent(
            @JsonProperty("description") String description,
            @JsonProperty("event") EndRoundEvent endRoundEvent) {
        super(description);
        this.endRoundEvent = endRoundEvent;
    }

    @Override
    public void effect(ClientModel model) {
        endRoundEvent.effect(model);
        model.updateEndGameEvent(description);
    }

}
