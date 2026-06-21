package it.polimi.ingsw.am14.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.exceptions.*;
import it.polimi.ingsw.am14.model.gameLogic.Game;

import java.sql.SQLException;
/**
 * The master server-side controller within the distributed MVC architecture.
 * <p>
 * This class isolates and protects the central {@link Game} engine instance from concurrent
 * multi-threaded network requests arriving from client connections (via RMI or Sockets). It guarantees
 * atomic execution of game operations by wrapping business logic calls in deterministic, non-fair synchronized
 * block monitors. Additionally, it integrates Jackson annotations to support native JSON deserialization,
 * which enables structural reconstruction of the match session during fault tolerance recovery sequences.
 * </p>
 */
public class GameController {
    private final Game game;
    /**
     * Constructs a {@code GameController} instance binding a specific game core model layer.
     * <p>
     * Annotated with {@link JsonCreator} and {@link JsonProperty} to allow automated Jackson mapping engine
     * reconstructions when inflating historical match contexts from cold file storage or transactional text databases.
     * </p>
     *
     * @param game the target core {@link Game} context manager instance being bound to this controller wrapper
     */
    @JsonCreator
    public GameController(
            @JsonProperty("game") Game game) {
        this.game = game;
    }
    /**
     * Forwards a card drafting request initiated by a remote client down into the state machine model.
     * <p>
     * This method locks the centralized {@code game} monitor to prevent overlapping state mutations
     * if multiple network worker threads invoke actions simultaneously.
     * </p>
     *
     * @param nickname the unique user nickname token requesting the draft action
     * @param cardId   the unique catalog identification index number of the chosen target card
     * @param row      the vertical tier index mapping the specific board grid layout component
     * @throws WrongStateException      if the underlying game engine is not currently resting in a picking context phase
     * @throws WrongTurnException       if the username parameters do not match chronological turn track listings
     * @throws UnknownPositionException  if row indices map outside systemic constants boundaries
     * @throws IllegalPlacementException if downstream placement validations veto coordinate parameters
     * @throws UnknownNicknameException  if the transmitted username signature fails matching sweeps against registered profiles
     * @throws IllegalPickException     if quantitative token allowances inside the player profile are depleted for the chosen row
     * @throws ForcedPickException      if rules enforce explicit selection vectors that conflict with this choice
     * @throws SQLException             if database transactional logs or status flags encounter underlying infrastructure failures
     */
    public void pickCard(String nickname, int cardId, int row) throws WrongStateException, WrongTurnException, UnknownPositionException, IllegalPlacementException, UnknownNicknameException, IllegalPickException, ForcedPickException, SQLException {
        synchronized (game) {
            game.pickCard(nickname, cardId, row);
        }
    }
    /**
     * Routes a totem placement positioning request initiated by a client down into the board tracking model.
     * <p>
     * Wraps execution inside a synchronization block to prevent simultaneous token overrides
     * on the shared track pathways.
     * </p>
     *
     * @param nickname the unique username signature executing token deployments
     * @param position the unique character coordinate slot key matching a target path coordinate (e.g., 'B', 'C')
     * @throws WrongStateException      if the active game phase rejects totem deployment interactions
     * @throws WrongTurnException       if the executing identity's order sequence does not match active turn indicators
     * @throws UnknownPositionException  if the character parameter falls outside valid path mappings
     * @throws IllegalPlacementException if the target track coordinate is already claimed by another user's totem
     * @throws UnknownNicknameException  if the incoming username token fails validation checks
     * @throws SQLException             if network event synchronization hits relational logging failures
     * @throws IllegalPickException     if structural sequential triggers cause state conflicts down the pipeline
     * @throws ForcedPickException      if boundary tracking conditions veto operations during execution loops
     */
    public void placeTotem(String nickname, char position) throws WrongStateException, WrongTurnException, UnknownPositionException, IllegalPlacementException, UnknownNicknameException, SQLException, IllegalPickException, ForcedPickException {
        synchronized (game) {
            game.placeTotem(nickname, position);
        }
    }
    /**
     * Enqueues an inbound connection hook or session recovery signal inside the active game status machine.
     * <p>
     * Uses synchronous monitoring to safely register the player profile and assign color configurations.
     * This is especially critical during crash recovery staging sequences (such as {@code WaitingRoomAfterCrashState})
     * to prevent race conditions when compiling reconnected user counts.
     * </p>
     *
     * @param nickname   the unique textual username signature of the participant joining or returning
     * @param totemColor the aesthetic color identifier configuration selected for score tracking
     * @throws WrongStateException if connection requests hit the core model when the room lobby parameters are sealed
     */
    public void onPlayerConnection(String nickname, String totemColor) throws WrongStateException {
        synchronized (game) {
            game.onPlayerConnection(nickname, totemColor);
        }
    }

    public Game getGame() {
        return game;
    }
}