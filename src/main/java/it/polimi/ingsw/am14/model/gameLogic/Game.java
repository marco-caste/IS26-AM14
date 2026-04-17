package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.exceptions.*;
import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.Card;

import java.util.*;

public class Game {
    private GameState currentState;
    private final List<Player> players;
    private final Board board;
    private int currentPlayerIndex;
    private final int numPlayers;
    private int currentRound;
    private int currentAge;
    private final String gameId;


    /**
     * <p>
     *     creates game data according to the game logic:
     *     - sets current state as WaitingRoomState
     *     - builds this.players as ArrayList of numPlayers elements
     *     - sets this.currentPlayerIndex as default
     *     - sets this.numPlayers as numPlayers
     *     - sets this.currentAge and this.currentRound as 1
     *     - calls Board constructor
     * </p>
     * @param numPlayers between 2 and 5
     */
    public Game(int numPlayers, String gameId) {
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
    public Game(GameState lastCurrentState,
                   List<Player> lastPlayers,
                   Board lastBoard,
                   int lastCurrentPlayerIndex,
                   int numPlayers,
                   int currentRound,
                   int currentAge,
                   String gameId) {
        this.currentState       = lastCurrentState;
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
     *     calls this.currentState.placeTotem()
     * </p>
     * @param nickname not null
     * @param position any char
     * @throws WrongStateException if this.currentState.placeTotem() throws it
     * @throws WrongTurnException if this.currentState.placeTotem() throws it
     * @throws UnknownPositionException if this.currentState.placeTotem() throws it
     */
    public void placeTotem(String nickname, char position) throws WrongStateException, WrongTurnException, UnknownPositionException, IllegalPlacementException, UnknownNicknameException {
        // Find player by nickname
        Player player = findPlayerByNickname(nickname);
        this.currentState.placeTotem(this, player, position);
    }

    /**
     * <p>
     *     calls this.currentState.pickCard()
     * </p>
     * @param nickname not null
     * @param cardId not null, use -1 for skip
     * @param row between 0 and 3, -1 for skip
     * @throws WrongStateException if this.currentState.pickCard() throws it
     * @throws WrongTurnException if this.currentState.pickCard() throws it
     * @throws IllegalPickException if this.currentState.pickCard() throws it or card not found
     * @throws ForcedPickException if this.currentState.pickCard() throws it
     * @throws UnknownNicknameException if the nickname is not found
     */
    public void pickCard(String nickname, int cardId, int row) throws WrongStateException, WrongTurnException, IllegalPickException, ForcedPickException, UnknownNicknameException {
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
     *
     * @param nickname not null
     * @param totemColor not null
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
    public String getGameId() {
        return gameId;
    }

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
     *     adds increment to this.currentPlayerIndex
     * </p>
     * @param increment >= 0
     */
    public void incrementCurrentPlayerIndexBy(int increment) {
        this.currentPlayerIndex += increment;
    }

    /**
     * <p>
     *     adds increment to this.currentRound
     * </p>
     * @param increment >= 0
     */
    public void incrementCurrentRoundBy(int increment) {
        this.currentRound += increment;
    }

    /**
     * <p>
     *     adds increment to this.currentAge
     * </p>
     * @param increment >= 0
     */
    public void incrementCurrentAgeBy (int increment) {
        this.currentAge += increment;
    }

    /**
     * <p>
     *     adds player to this.players
     * </p>
     * @param player not null
     */
    public void addPlayer(Player player) {
        this.players.add(player);
    }

    // SIZERS:

    /**
     * <p>
     *     called in setup phases, is relevant while not all players have connected;
     *     has result always equal to getNumPlayers() in non-setup game phases
     * </p>
     * @return this.players.size()
     */
    public int playersSize() {
        return this.players.size();
    }

}


