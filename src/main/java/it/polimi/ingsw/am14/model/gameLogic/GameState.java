package it.polimi.ingsw.am14.model.gameLogic;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am14.exceptions.*;
import it.polimi.ingsw.am14.model.cards.Card;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Abstract base for all game states; implements the State design pattern.
 *
 * <p>
 * Each concrete subclass represents one logical state of the game (for example:
 * waiting room, position selection, picking, end-of-round, end-of-game, etc.).
 * The {@code Game} context delegates state-specific behavior to the current
 * {@code GameState} instance: actions such as {@link #placeTotem}, {@link #pickCard}
 * and {@link #onPlayerConnection} are handled by the active state object.
 * </p>
 *
 * <p>
 * This class acts as the engine of the game's state machine:
 * subclasses override the operation methods to provide permitted behavior for that
 * state and implement {@link #nextState(Game)} to perform state transitions.
 * Default implementations throw {@code WrongStateException} (or perform no-op)
 * to enforce that only allowed operations succeed in a given state.
 * </p>
 *
 * <p>Responsibilities:</p>
 *  <ul>
 *   <li>Define the state interface for game operations.</li>
 *   <li>Provide default (disabled) implementations that raise {@code WrongStateException}.</li>
 *   <li>Require subclasses to implement {@link #nextState(Game)} to advance the state machine.</li>
 *   <li>Serve as the single place to add or modify state-specific logic and transitions.</li>
 *  </ul>
 *
 * <p><b>Note:</b> concrete states may throw or propagate domain-specific exceptions
 * (e.g. {@code IllegalPickException}, {@code ForcedPickException}, {@code WrongTurnException},
 * {@code UnknownPositionException}, {@code IllegalPlacementException}) to signal invalid
 * operations according to game rules.</p>
 *
 * @see it.polimi.ingsw.am14.model.gameLogic.Game
 * @see #placeTotem(it.polimi.ingsw.am14.model.gameLogic.Game, it.polimi.ingsw.am14.model.gameLogic.Player, char)
 * @see #pickCard(it.polimi.ingsw.am14.model.gameLogic.Game, it.polimi.ingsw.am14.model.gameLogic.Player, java.util.Optional, int)
 * @see #onPlayerConnection(it.polimi.ingsw.am14.model.gameLogic.Game, String, String)
 * @see #nextState(it.polimi.ingsw.am14.model.gameLogic.Game)
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = WaitingRoomState.class, name = "waitingRoomState"),
        @JsonSubTypes.Type(value = PositionState.class, name = "positionState"),
        @JsonSubTypes.Type(value = PickState.class, name = "pickState"),
        @JsonSubTypes.Type(value = EndRoundState.class, name = "endRoundState"),
        @JsonSubTypes.Type(value = EndGameState.class, name = "endGameState"),
        @JsonSubTypes.Type(value = WaitingRoomAfterCrashState.class, name = "waitingRoomAfterCrashState")
})
abstract class GameState {

    @JsonCreator
    public GameState() {
    }

    /**
     * <p>
     *     Moves player p from the TurnOrderCard towards the required TrackCard, if it is possible to do so.
     * </p>
     * @param context the game context in which this action is performed
     * @param player the player performing this request
     * @param position the name of the track card on which player wants to place their totem
     * @throws WrongStateException by default if the current state does not allow this action
     * @throws IllegalPlacementException if an override call throws it
     * @throws UnknownPositionException if an override call throws it
     * @throws WrongTurnException if an override call throws it
     */
    protected void placeTotem(Game context, Player player, char position) throws WrongStateException, UnknownPositionException, WrongTurnException, IllegalPlacementException, SQLException, IllegalPickException, ForcedPickException {
        throw new WrongStateException("Not in the right state to place a totem");
    }

    /**
     * <p>
     *     Picks the card the player wants to pick, if it is possible to do so, putting it in the player's tribe and eventually applying its effects.
     * </p>
     * @param context the game context in which this action is performed
     * @param player the player performing this request
     * @param card the card the player wants to pick
     * @param row between 0 and 3
     * @throws WrongStateException by default if the current state does not allow this action
     * @throws IllegalPickException if an override call throws it
     * @throws ForcedPickException if an override call throws it
     * @throws WrongTurnException if an override call throws it
     */
    protected void pickCard(Game context, Player player, Optional<Card> card, int row) throws WrongStateException, IllegalPickException, ForcedPickException, WrongTurnException, SQLException {
        throw new WrongStateException("Not in the right state to pick a card");
    }

    /**
     * <p>
     *     Handles the connection of a player to the game
     * </p>
     * @param context the game context in which this action is performed
     * @param nickname the nickname of the player requesting to join the game
     * @param totemColor the color of the totem the player wants to use
     * @throws WrongStateException by default if the current state does not allow this action
     */
    protected void onPlayerConnection(Game context, String nickname, String totemColor) throws WrongStateException {
        throw new WrongStateException("Not in the right state to handle a player connection");
    }

    /**
     * <p>
     *     Transitions the game to the next state
     * </p>
     * @param context the game context in which this action is performed
     * @throws SQLException if an SQL error occurs
     */
    protected abstract void nextState(Game context) throws SQLException, IllegalPickException, ForcedPickException;

    /**
     * <p>
     *     Ends the current round and transitions the game to the next round, if it was not the last one.
     * </p>
     * @param context the game context in which this action is performed
     * @throws SQLException if an SQL error occurs
     */
    protected void endRound(Game context) throws SQLException {
    }

    /**
     * Initiates the control cycle after a card pick to evaluate the validity and consequences of the action.
     * <p>
     * By default, this method performs no operation. Concrete subclasses (specifically {@code ControlState})
     * override this method to handle post-pick validations, process additional pick allowances,
     * update turn counters, and compute whether control remains with the current player or transitions.
     * </p>
     *
     * @param context      the game context in which this action is performed
     * @param addTopPick   {@code true} if this step covers an extra top pick; {@code false} for regular drafts
     * @param selectedCard the card involved in the action
     * @param row          the board row index where the selection was performed
     * @throws SQLException if a database or state persistence error occurs during state transitions
     */
    protected void controlPick(Game context, boolean addTopPick, Card selectedCard, int row) throws SQLException{
    }

    /**
     * Retrieves the canonical text identifier associated with this specific game state.
     * <p>
     * This identifier is primarily utilized by the client-side architecture (e.g., {@code ClientModel})
     * to recognize, track, or reset the current state representation. By default, this base implementation
     * returns {@code null}. Concrete state subclasses are expected to override this method to return their
     * unique string designation (e.g., "PICK", "PLACE").
     * </p>
     *
     * @return a {@code String} representing the state mode, or {@code null} if not explicitly defined by the subclass
     */
    protected String getStateName() {
        return null;
    }
}
