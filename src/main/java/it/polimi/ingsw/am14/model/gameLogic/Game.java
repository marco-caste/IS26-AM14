package it.polimi.ingsw.am14.model.gameLogic;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.network.Observer;
import it.polimi.ingsw.am14.network.messages.events.Event;
import it.polimi.ingsw.am14.exceptions.*;
import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.Card;

import java.sql.SQLException;
import java.util.*;

/**
 * <p>
 *     Represents a game instance, managing the game state, players, board, and game progression.
 *     The game follows a state machine pattern, where the current state determines the allowed actions and transitions.
 * </p>
 * <p>
 *     This class also works as an interface between the Model, of which it is the most important piece, and the Controller.
 *     It implements the Observable interface, allowing observers (e.g., network components) to be notified of game events.
 * </p>
 */
public class Game implements Observable {
    /**
     * The current state of the game, which determines the allowed actions and transitions.
     */
    private GameState currentState;
    /**
     * The previously saved state of the game, used for restoring the game after a crash or shutdown.
     */
    @JsonIgnore
    private GameState previouslySavedState;
    /**
     * The list of players participating in the game.
     */
    private final List<Player> players;
    /**
     * The game board, which contains the game components and manages the game logic.
     */
    private final Board board;
    // TODO: GIUSTO?
    /**
     * The index of the current player in the players list.
     */
    private int currentPlayerIndex;
    /**
     * The total number of players in the game.
     */
    private final int numPlayers;
    /**
     * The current round of the game, starting from 1, all the way up to 10.
     */
    private int currentRound;
    /**
     * The current age of the game, starting from 1, all the way up to 3.
     */
    private int currentAge;
    /**
     * The unique identifier for this game instance.
     */
    private final int gameId;

    /**
     * The list of observers that are notified of game events.
     */
    @JsonIgnore
    private final List<Observer> observers = new ArrayList<>();


    /**
     * <p>
     *     Creates a new game with the specified number of players and game ID.
     *     The game starts in the {@link WaitingRoomState} state, with an empty list of players, the current player index set to 0, the current round and age set to 1, and a new {@link Board} instance initialized for the specified number of players.
     * @param numPlayers between 2 and 5
     * @param gameId unique identifier for this game instance, provided by the {@link LobbyController}.
     */
    public Game(int numPlayers, int gameId) {
        this.gameId             = gameId;
        this.currentState       = new WaitingRoomState();
        this.players            = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.numPlayers         = numPlayers;
        this.currentRound       = 1;
        this.currentAge         = 1;
        this.board              = new Board(numPlayers);
    }

    // called if the server shuts down, gets the arguments from the latest JSON snapshot
    /**
     * <p>
     *     Creates a new game instance from the provided parameters, typically used for restoring a game after a server crash or shutdown.
     *     The game state is initialized to {@link WaitingRoomAfterCrashState}, and the previously saved state is set to the provided last current state.
     * </p>
     * @param lastCurrentState the last saved state of the game before the crash
     * @param lastPlayers the list of players in the game before the crash
     * @param lastBoard the game board state before the crash
     * @param lastCurrentPlayerIndex the index of the current player before the crash
     * @param numPlayers the total number of players in the game
     * @param currentRound the current round of the game before the crash
     * @param currentAge the current age of the game before the crash
     * @param gameId unique identifier for this game instance.
     */
    @JsonCreator
    public Game(
            @JsonProperty("currentState") GameState lastCurrentState,
            @JsonProperty("players") List<Player> lastPlayers,
            @JsonProperty("board") Board lastBoard,
            @JsonProperty("currentPlayerIdx") int lastCurrentPlayerIndex,
            @JsonProperty("numPlayers") int numPlayers,
            @JsonProperty("currentRound") int currentRound,
            @JsonProperty("currentAge") int currentAge,
            @JsonProperty("gameId") int gameId) {
        // TODO: ?
        this.currentState       = new WaitingRoomAfterCrashState();
//        this.currentState       = lastCurrentState;
        this.previouslySavedState = lastCurrentState;
        this.players            = lastPlayers;
        this.board              = lastBoard;
        this.currentPlayerIndex = lastCurrentPlayerIndex;
        this.numPlayers         = numPlayers;
        this.currentRound       = currentRound;
        this.currentAge         = currentAge;
        this.gameId             = gameId;
    }

    /**
     * <p>
     *     performs the place totem action given the current state of the game.
     * </p>
     * @param nickname the nickname of the player placing the totem
     * @param position the track position where the totem is to be placed
     * @throws WrongStateException if {@code this.currentState.placeTotem()} throws it
     * @throws WrongTurnException if {@code this.currentState.placeTotem()} throws it
     * @throws UnknownPositionException if {@code this.currentState.placeTotem()} throws it
     */
    public void placeTotem(String nickname, char position) throws WrongStateException, WrongTurnException, UnknownPositionException, IllegalPlacementException, UnknownNicknameException, SQLException, IllegalPickException, ForcedPickException {
        // Find player by nickname
        Player player = findPlayerByNickname(nickname);
        this.currentState.placeTotem(this, player, position);
    }

    /**
     * <p>
     *     performs the pick card action given the current state of the game.
     * </p>
     * @param nickname the nickname of the player picking the card
     * @param cardId the id of the card to be picked
     * @param row the row from which to pick, between 0 and 3
     * @throws WrongStateException if {@code this.currentState.pickCard()} throws it
     * @throws WrongTurnException if {@code this.currentState.pickCard()} throws it
     * @throws IllegalPickException if {@code this.currentState.pickCard()} throws it or card not found
     * @throws ForcedPickException if {@code this.currentState.pickCard()} throws it
     * @throws UnknownNicknameException if the nickname is not found
     */
    public void pickCard(String nickname, int cardId, int row) throws WrongStateException, WrongTurnException, IllegalPickException, ForcedPickException, UnknownNicknameException, SQLException {
        // Find player by nickname
        Player player = findPlayerByNickname(nickname);
        
        // Create Optional<Card>: if cardId is -1, it's empty (skip), otherwise find card by id in the specified row
        Optional<Card> card = cardId == -1 
                ? Optional.empty() 
                : findCardById(cardId, row);

        this.currentState.pickCard(this, player, card, row);
    }

    private Player findPlayerByNickname(String nickname) throws UnknownNicknameException {
        return players.stream()
                .filter(p -> p.getNickname().equals(nickname))
                .findFirst()
                .orElseThrow(() -> new UnknownNicknameException("Player not found: " + nickname));
    }

    private Optional<Card> findCardById(int cardId, int row) throws IllegalPickException {
        switch (row) {
            case 0: // TOP_DECKABLE_ROW
                return Optional.of(board.getTopDeckableRow().stream()
                        .filter(card -> card.getId() == cardId)
                        .findFirst()
                        .orElseThrow(() -> new IllegalPickException("The selected card is not available")));
            case 1: // TOP_BUILDING_ROW
                return Optional.of(board.getTopBuildingRow().stream()
                        .filter(card -> card.getId() == cardId)
                        .findFirst()
                        .orElseThrow(() -> new IllegalPickException("The selected card is not available")));
            case 2: // BOTTOM_DECKABLE_ROW
                return Optional.of(board.getBottomDeckableRow().stream()
                        .filter(card -> card.getId() == cardId)
                        .findFirst()
                        .orElseThrow(() -> new IllegalPickException("The selected card is not available")));
            case 3: // BOTTOM_BUILDING_ROW
                return Optional.of(board.getBottomBuildingRow().stream()
                        .filter(card -> card.getId() == cardId)
                        .findFirst()
                        .orElseThrow(() -> new IllegalPickException("The selected card is not available")));
            default:
                throw new IllegalPickException("Invalid row: " + row);
        }
    }

    /**
     * connects a player to the game, if the current state allows it.
     * @param nickname the nickname of the player connecting to the game
     * @param totemColor the totem color of the player connecting to the game
     * @throws WrongStateException if this.currentState.onPlayerConnection() throws it
     */
    public void onPlayerConnection(String nickname, String totemColor) throws WrongStateException {
        this.currentState.onPlayerConnection(this, nickname, totemColor);
    }

    // SETTER:
    public void setCurrentState(GameState currentState) {
        this.currentState = currentState;
    }
    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }
    public void setCurrentAge(int currentAge) {
        this.currentAge = currentAge;
    }
    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    // GETTERS:
    public Board getBoard() {
        return board;
    }
    public GameState getCurrentState() {
        return currentState;
    }
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
    public int getGameId() {
        return gameId;
    }
    public GameState getPreviouslySavedState() {
        return previouslySavedState;
    }
    public String previouslySavedStateName() {return previouslySavedState.getStateName();} //Only for client update
    /**
     *
     * @return a copy of this.players
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }
    public int getNumPlayers() {
        return numPlayers;
    }
    public int getCurrentRound() {
        return currentRound;
    }
    public int getCurrentAge() {
        return currentAge;
    }

    // ADDERS:

    /**
     * <p>
     *     Adds increment to this.currentPlayerIndex
     * </p>
     * @param increment >= 0
     */
    public void incrementCurrentPlayerIndexBy(int increment) {
        this.currentPlayerIndex += increment;
    }

    /**
     * <p>
     *     Adds increment to this.currentRound
     * </p>
     * @param increment >= 0
     */
    public void incrementCurrentRoundBy(int increment) {
        this.currentRound += increment;
    }

    /**
     * <p>
     *     Adds increment to this.currentAge
     * </p>
     * @param increment >= 0
     */
    public void incrementCurrentAgeBy (int increment) {
        this.currentAge += increment;
    }

    /**
     * <p>
     *     Adds player to this.players
     * </p>
     * @param player the player to add to the game, must not be null
     */
    public void addPlayer(Player player) {
        this.players.add(player);
    }

    // SIZERS:

    /**
     * <p>
     *     Called in setup phases, is relevant while not all players have connected;
     *     has result always equal to getNumPlayers() in non-setup game phases
     * </p>
     * @return this.players.size()
     */
    public int playersSize() {
        return this.players.size();
    }

    // OBSERVER PATTERN towards the network layer:
    /**
     * @param observer the observer to add, must not be null
     */
    @Override
    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    /**
     * @param observer the observer to remove, must not be null
     */
    @Override
    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    /**
     * @param event the event to notify to observers, must not be null
     */
    @Override
    public void notifyObservers(Event event) {
        for (Observer observer : observers) {
            observer.update(event);
        }
    }
}


