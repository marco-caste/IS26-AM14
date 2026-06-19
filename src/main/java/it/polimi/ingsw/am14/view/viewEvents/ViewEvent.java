package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

/**
 * <p>
 *     Interface representing a view event in the game. A view event is an action or change in the game state that needs to be rendered on the user interface.
 *     Each view event must implement the render method, which defines how the event should be displayed on the provided view.
 * </p>
 */
public interface ViewEvent {
    /**
     * <p>
     *     Renders this event on the provided view. The specific rendering behavior will depend on the type of event and the capabilities of the view.
     * </p>
     * @param view the view that will handle this event
     */
    void render(ViewType view);


}
