package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.events.EndRoundEvent;
import it.polimi.ingsw.am14.model.boardComponents.Board;

class EndRoundState extends GameState {

    /**
     * <p>
     *     changes the current State of the game:
     *     - sets context.currentState to a new PositionState
     * </p>
     * @param context not null
     */
    @Override
    protected void nextState(Game context) {
        context.setCurrentState(new PositionState());
    }

    /**
     * <p>
     *     ends the currentRound and prepares for the following one:
     *     - resolves the bonus/malus of TurnOrderCard
     *     - queues the events in the Board and solves their effects
     *     - moves deckCards from topRow to bottomRow, removing the pre-existing instance of the latter
     *     - adds 1 to currentRound
     *     - if next round is the last (= 10), puts all the remaining cards in the deck on the board,
     *          otherwise puts the correct amount according to rules
     *     - carries out the age change if conditions are met
     * </p>
     * @param context not null
     */
    @Override
    protected void endRound(Game context) {
        Board currentBoard = context.getBoard();

        currentBoard.getTurnOrderCard().resolveBonusOrMalus();

        // ENDROUND PHASES 1 AND 2:
        // puts the event on queue to be resolved and discards characters
        currentBoard.idleEventsAndDiscardCharactersFromBottom();

        for (int i = currentBoard.toResolveOtherEventsRowSize() - 1; i >= 0; i--) {
            currentBoard.getFromResolveOtherEventsRow(i).applyEffect(null, context);
        }
        for (int i = 0; i < currentBoard.toResolveSustenanceEventsRowSize(); i++) {
            currentBoard.getFromResolveSustenanceEventRow(i).applyEffect(null, context);
        }
        currentBoard.discardEvents();

        // ENDROUND PHASE 3
        currentBoard.switchDeckablesFromTop();

        // ENDROUND PHASE 4
        context.incrementCurrentRoundBy(1);

        if (context.getCurrentRound() == 10) {
            currentBoard.pickDeckableAndAddToTop(currentBoard.getTribeDeck().deckSize() - 4);
            // there is no need to check the age change at final turn, so the method returns after calling nextState

            this.nextState(context);
            return;
        } else {
            currentBoard.pickDeckableAndAddToTop(context.getNumPlayers());
        }

        // ENDROUND - NEW AGE PHASE 1
        if (currentBoard.getTribeDeck().deckPeek().getAge() > context.getCurrentAge()) {
            context.incrementCurrentAgeBy(1);
            currentBoard.discardBuildingFromBottom();

            // ENDROUND - NEW AGE PHASE 2
            currentBoard.switchBuildingsFromTop();

            // ENDROUND - NEW AGE PHASE 3
            currentBoard.pickBuildingAndAddToTop(context.getCurrentAge());
        }

        // observer pattern: sends event to event buffer
        context.notifyObservers(
                new EndRoundEvent(context.getBoard(), context.getPlayers(), context.getGameId())
        );

        this.nextState(context);
    }
}
