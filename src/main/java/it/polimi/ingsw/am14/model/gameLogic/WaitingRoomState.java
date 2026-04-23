package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.events.GameStartedEvent;
import it.polimi.ingsw.am14.events.JoinedGameEvent;

class WaitingRoomState extends GameState {

    @Override
    protected void onPlayerConnection(Game game, String nickname, String totemColor) {

        Player p = new Player(nickname, TotemColor.valueOf(totemColor));
        game.addPlayer(p);
        game.getBoard().getTurnOrderCard().addPlayer(p);

        // observer pattern: sends event to event buffer
        game.notifyObservers(
                new JoinedGameEvent(
                        nickname,
                        game.getGameId(),
                        game.getBoard(),
                        game.getNumPlayers()
                )
        );

        if (game.playersSize() == game.getNumPlayers()) {
            this.nextState(game);
        }

    }

    @Override
    protected void nextState(Game context) {
        context.setCurrentState(new PositionState());
        context.getBoard().getTurnOrderCard().setupPhaseTen();

        // observer pattern: sends event to event buffer
        context.notifyObservers(
                new GameStartedEvent(
                        context.getBoard().getTurnOrderCard().getPlayerOrder(),
                        context.getGameId()
                )
        );
    }
}
