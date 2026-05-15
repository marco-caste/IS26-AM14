package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.network.messages.events.GameRestartedEvent;
import it.polimi.ingsw.am14.network.messages.events.PlayerReconnectedEvent;

public class WaitingRoomAfterCrashState extends GameState {

    private int numPlayersReconnected = 0;

    @Override
    protected void onPlayerConnection(Game game, String nickname, String totemColor) {
        game.notifyObservers(
                //TODO: checka se va bene l'evento
                new PlayerReconnectedEvent(
                        nickname,
                        game.getGameId(),
                        game.getBoard(),
                        game.getNumPlayers(),
                        game.getPlayers()
                )
        );
        addNumPlayersConnected();
        if (numPlayersReconnected == game.getNumPlayers()) {
            this.nextState(game);
            game.notifyObservers(
                    new GameRestartedEvent(
                            game.getGameId(),
                            nickname
                    )
            );
        }
    }

    @Override
    protected void nextState(Game context) {
        context.setCurrentState(context.getPreviouslySavedState());
    }

    private void addNumPlayersConnected() {
        this.numPlayersReconnected++;
    }
}
