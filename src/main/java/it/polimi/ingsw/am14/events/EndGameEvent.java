package it.polimi.ingsw.am14.events;

import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.gameLogic.Player;

import java.util.List;

public class EndGameEvent extends UpdateEvent {
    EndRoundEvent endRoundEvent;

    public EndGameEvent(final Board board, final List<Player> finalRanking) {
        this.endRoundEvent = new EndRoundEvent(board, finalRanking);
        this.description = "End of game, the board has been updated, the final ranking has been calculated";
    }
}
