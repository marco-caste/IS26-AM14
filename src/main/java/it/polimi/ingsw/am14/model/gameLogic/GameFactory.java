package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.deckCards.*;
import it.polimi.ingsw.am14.exceptions.IllegalSetupException;

/**
 * <p>
 *     Factory class for creating and setting up a new Game instance.
 * </p>
 *
 * <p>
 *     The {@code GameFactory} class provides a static method {@link #setup(int, int)} that initializes a new {@link Game}
 *     object with the specified number of players. It performs the initial setup phases of the game according to the rules,
 *     including populating the board with cards and preparing the game state for play.
 * </p>
 *
 * <p>
 *     Responsibilities:
 *     <ul>
 *         <li>Validate the number of players.</li>
 *         <li>Create a new {@code Game} instance.</li>
 *         <li>Perform setup phases 3, 4, 5, and 6a of the game.</li>
 *         <li>Return a fully initialized {@code Game} object ready for play.</li>
 *     </ul>
 * </p>
 *
 * @see Game
 */
public abstract class GameFactory {


    /**
     * <p>
     *     Calls Game constructor, performs the first 6 phases of game setup according to the rules
     * </p>
     * @param numPlayers the number of players expected to play the game
     * @return a Game object with numPlayers players
     * @throws IllegalSetupException if numPlayers is not between 2 and 5
     */
    public static Game setup(int numPlayers, int gameIdCounter) throws IllegalSetupException {

        if (numPlayers > 5 || numPlayers < 2) {
            throw new IllegalSetupException("[GameFactory] A game with that number of players [" + numPlayers + "] is not possible");
        }
        // create gameObjects:
        Game newGame = new Game(numPlayers, gameIdCounter);
        DeckCard d;

        Board currentBoard = newGame.getBoard();

        // setup: phases 3 and 4
        for (int i = 0; i < numPlayers + 1; ) {
            d = currentBoard.getTribeDeck().deckPop();
            i += d.setupPhaseFour(currentBoard);
        }

        // setup: phase 5
        currentBoard.pickDeckableAndAddToTop(numPlayers - currentBoard.topDeckableRowSize());

        // setup: phase 6a
        for (int i = 0; i < currentBoard.getBuildingDeck().buildingDeckSize(1); i++) {
            currentBoard.addToTopBuildingRow(
                    currentBoard.getBuildingDeck().getBuilding(1, i)
            );
        }

        // setup: phase 8 - to be continued in WaitingRoomState.onPlayerConnection() and WaitingRoomState.nextState()
        return newGame;

        // setup: phase 10 - handled in TurnOrderCard builder
    }
}