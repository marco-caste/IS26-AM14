package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.network.messages.events.GameStartedEvent;
import it.polimi.ingsw.am14.network.messages.events.JoinedGameEvent;

/**
 * <p>
 *     Represents the state of the game when players are waiting to join. In this state, players can connect to the game and choose their totem colors.
 *     Once all players have joined, the game transitions to the next state.
 * </p>
 */
class WaitingRoomState extends GameState {

    /**
     * {@inheritDoc}
     * <p>
     *     It checks if the chosen totem color is already taken by another player.
     *     If the color is available, it creates a new Player instance and adds it to the game. It also notifies observers about the new player joining.
     *     If all players have joined, it transitions the game to the next state and notifies observers that the game has started.
     * </p>
     * @param game the current game instance
     * @param nickname the nickname of the player trying to connect
     * @param totemColor the chosen totem color of the player trying to connect
     * @throws IllegalArgumentException if the chosen totem color is already taken by another player
     */
    @Override
    protected void onPlayerConnection(Game game, String nickname, String totemColor) throws IllegalArgumentException{

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

    /**
     * {@inheritDoc}
     * <p>
     *     For this state, the next state is always {@link PositionState}. It also performs the game setup process' phase 10.
     * </p>
     * @param context the current game instance
     */
    @Override
    protected void nextState(Game context) {
        context.setCurrentState(new PositionState());
        context.getBoard().getTurnOrderCard().setupPhaseTen();
    }
}
