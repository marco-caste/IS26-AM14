package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

/**
 * View event representing a user disconnection, containing a message to be displayed.
 */
public class UserDisconnectedViewEvent implements ViewEvent{
    /**
     * The message to be displayed when a user is disconnected.
     */
    private final String message;

    /**
     * Constructs a UserDisconnectedViewEvent with the specified message.
     *
     * @param message the message to be displayed when a user is disconnected
     */
    public UserDisconnectedViewEvent(String message) {
        this.message = message;
    }

    /**
     * {@inheritDoc}
     * <p>
     *     For this class, renders the disconnection message using the provided view.
     * </p>
     *
     * @param view the view to render the disconnection message on
     */
    @Override
    public void render(ViewType view) {
        view.renderDisconnection(message + ". Returning to lobby...");
    }
}
