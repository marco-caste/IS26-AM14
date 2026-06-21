package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.network.messages.events.EndRoundEvent;
import it.polimi.ingsw.am14.model.boardComponents.Board;

/**
 * <p>
 *     This class represents the state of the game when a round ends.
 *     It handles the end-of-round logic, including resolving bonuses/maluses, discarding characters, switching cards, and managing age transitions.
 * </p>
 */
class EndRoundState extends GameState {

    /**
     * {@inheritDoc}
     * <p>
     *     for this state, the next state is always {@link PositionState}
     * @param context not null
     */
    @Override
    protected void nextState(Game context) {
        context.setCurrentState(new PositionState());
    }

    /**
     * {@inheritDoc}
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
            context.notifyObservers(
                    new EndRoundEvent(context.getBoard(), context.getPlayers(), context.getGameId(), context.getCurrentAge())
            );

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
                new EndRoundEvent(context.getBoard(), context.getPlayers(), context.getGameId(), context.getCurrentAge())
        );

        this.nextState(context);
    }
}
