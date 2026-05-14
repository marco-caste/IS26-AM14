package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.events.GameStartedEvent;
import it.polimi.ingsw.am14.events.JoinedGameEvent;

class WaitingRoomState extends GameState {

    @Override
    protected void onPlayerConnection(Game game, String nickname, String totemColor) {

        TotemColor color = TotemColor.valueOf(totemColor.toUpperCase());
        if(game.getPlayers().stream().map(Player::getTotemColor).anyMatch(color::equals)){
            throw new IllegalArgumentException("Totem color already taken");
        }
        Player p = new Player(nickname,color);
        game.addPlayer(p);
        game.getBoard().getTurnOrderCard().addPlayer(p);

        // observer pattern: sends event to event buffer
        game.notifyObservers(
                new JoinedGameEvent(
                        p,
                        game.getPlayers(),
                        game.getGameId(),
                        game.getBoard(),
                        game.getNumPlayers()
                )
        );

        if (game.playersSize() == game.getNumPlayers()) {
            this.nextState(game);
            // observer pattern: sends event to event buffer
            game.notifyObservers(
                    new GameStartedEvent(
                            game.getBoard().getTurnOrderCard().getPlayerOrder(),
                            game.getGameId(),
                            nickname
                    )
            );
        }
    }

    @Override
    protected void nextState(Game context) {
        context.setCurrentState(new PositionState());
        context.getBoard().getTurnOrderCard().setupPhaseTen();
    }
}
