package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

/**
 * View event representing an update when a position is updated, containing a message to be displayed.
 */
public class PositionUpdateViewEvent implements ViewEvent{
    /**
     * The message to be displayed when a position is updated.
     */
    private final String message;

    /**
     * Constructs a PositionUpdateViewEvent with the specified message.
     *
     * @param message the message to be displayed when a position is updated
     */
    public PositionUpdateViewEvent(String message) {
        this.message = message;
    }

    /**
     * {@inheritDoc}
     * <p>
     *     For this class, renders the position updated message using the provided view.
     * </p>
     *
     * @param view the view to render the position updated message on
     */
    @Override
    public void render(ViewType view) {
        view.renderTotemPlaced(message);
    }
}
