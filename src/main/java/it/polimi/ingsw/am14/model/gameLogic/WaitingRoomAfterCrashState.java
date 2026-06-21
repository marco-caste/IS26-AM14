package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.network.messages.events.GameRestartedEvent;
import it.polimi.ingsw.am14.network.messages.events.PlayerReconnectedEvent;
/**
 * State implementation that manages fault tolerance and session recovery immediately following a server crash
 * or massive network failure.
 * <p>
 * This class acts as a synchronized staging environment (Waiting Room). When a game is reloaded from a persistent
 * save state, control is assigned here until all original participants successfully establish a new connection.
 * Upon complete reconnection, the state executes a crucial structural re-linking routine: it synchronizes volatile
 * network player references with stale structural placements inside the board layouts (such as {@code TurnOrderCard}
 * and {@code OffersPath}) before safely rolling back control to the game state active right before the crash occurred.
 * </p>
 */
public class WaitingRoomAfterCrashState extends GameState {

    private int numPlayersReconnected = 0;
    /**
     * Handles the physical reconnection event of a player returning to the recovered game environment.
     * <p>
     * The processing lifecycle carries out the following steps:
     * <ol>
     * <li>Broadcasts a {@link PlayerReconnectedEvent} notification to push current board layouts and historical models
     * down to the newly connected view client.</li>
     * <li>Increments the internal connection counter via {@link #addNumPlayersConnected()}.</li>
     * <li>Checks if the quorum of reconnected users matches the original game size setup ({@code game.getNumPlayers()}).</li>
     * <li>If the quorum is reached, it initiates a defensive dual-pass mapping sweep to overwrite stale player references
     * with new session tokens within the {@code TurnOrderCard} sequence and the grid-mapped {@code OffersPath} track slots.</li>
     * <li>Advances the state machine using {@link #nextState(Game)} and fires a global {@link GameRestartedEvent}
     * to inform all participants that structural setup has stabilized and active turns are resuming.</li>
     * </ol>
     * </p>
     *
     * @param game        the central {@link Game} context manager directing recovery routines
     * @param nickname    the unique textual signature profile of the returning user
     * @param totemColor  the aesthetic identifier mapped to the player's game token
     */
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
            } catch (Exception _) {}

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
    /**
     * Restores control to the historically recorded functional flow by popping the saved state token.
     * <p>
     * Re-assigns the central engine status pointer dynamically back to the instance retrieved from
     * {@link Game#getPreviouslySavedState()}, ensuring seamless continuity of game history.
     * </p>
     *
     * @param context the central game engine tracking wrapper {@link Game}
     */
    @Override
    protected void nextState(Game context) {
        context.setCurrentState(context.getPreviouslySavedState());
    }

    private void addNumPlayersConnected() {
        this.numPlayersReconnected++;
    }
}
