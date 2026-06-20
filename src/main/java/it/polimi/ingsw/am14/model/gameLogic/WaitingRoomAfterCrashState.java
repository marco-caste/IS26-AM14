package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.network.messages.events.GameRestartedEvent;
import it.polimi.ingsw.am14.network.messages.events.PlayerReconnectedEvent;

public class WaitingRoomAfterCrashState extends GameState {

    private int numPlayersReconnected = 0;

    @Override
    protected void onPlayerConnection(Game game, String nickname, String totemColor) {
        game.notifyObservers(
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

            try {
                for (Player p : game.getPlayers()) {
                    Player p1;
                    if (!game.getBoard().getTurnOrderCard().getPlayerOrder().isEmpty()) {
                        for (int i = 0; i < game.getBoard().getTurnOrderCard().getPlayerOrder().size(); i++) {

                            try {
                                p1 = game.getBoard().getTurnOrderCard().getPlayer(i);
                            } catch (NullPointerException _) {
                                continue;
                            }
                            if (p.getNickname().equals(p1.getNickname())) {
                                game.getBoard().getTurnOrderCard().setPlayerReference(i, p);
                            }
                        }
                    }
                    for (Character trackCardName : game.getBoard().getOffersPath().offersPathKeySet()) {
                        p1 = game.getBoard().getOffersPath().offersPathGet(trackCardName).getPlayerTotem();
                        if (p1 == null) {
                            continue;
                        }
                        if (p.getNickname().equals(p1.getNickname())) {
                            game.getBoard().getOffersPath().offersPathGet(trackCardName).setPlayerTotem(p);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error while setting player references in turn order card and offers path: " + e.getMessage());
            }

            this.nextState(game);
            game.notifyObservers(
                    new GameRestartedEvent(
                            game.getGameId(),
                            nickname,
                            game.previouslySavedStateName()
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
