package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.network.Observer;
import it.polimi.ingsw.am14.network.messages.events.Event;

/**
 * <p>
 *     Interface representing an observable entity in the game. An observable entity can be observed by one or more observers, which are notified of changes in the entity's state.
 *     This interface defines methods for adding and removing observers, as well as notifying them of events.
 * </p>
 */
public interface Observable {

    /**
     * <p>
     *     Adds an observer to this observable entity. The observer will be notified of changes in the entity's state.
     * </p>
     * @param observer the observer to be added
     */
    void addObserver(Observer observer);

    /**
     * <p>
     *     Removes an observer from this observable entity. The observer will no longer be notified of changes in the entity's state.
     * </p>
     * @param observer the observer to be removed
     */
    void removeObserver(Observer observer);

    /**
     * <p>
     *     Notifies all registered observers of an event. This method is called when the state of the observable entity changes, and it informs all observers of the change.
     * </p>
     * @param event the event to be sent to the observers
     */
    void notifyObservers(Event event);
}
