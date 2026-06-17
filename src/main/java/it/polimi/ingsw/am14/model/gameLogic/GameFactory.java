package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.deckCards.*;
import it.polimi.ingsw.am14.exceptions.IllegalSetupException;

public abstract class GameFactory {


    /**
     * <p>
     *     calls Game constructor, performs the first 6 phases of game setup according to the rules
     * </p>
     * @param numPlayers any integer
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