package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

/**
 * View event representing a player reconnection, containing a message to be displayed.
 */
public class PlayerReconnectedViewEvent implements ViewEvent{
    /**
     * The message to be displayed when a player reconnects to the game.
     */
    private final String message;

    /**
     * Constructs a PlayerReconnectedViewEvent with the specified message.
     *
     * @param message the message to be displayed when a player reconnects to the game
     */
    public PlayerReconnectedViewEvent(String message) {
        this.message = message;
    }

    /**
     * {@inheritDoc}
     * <p>
     *     For this class, renders the player reconnection message using the provided view.
     * </p>
     *
     * @param view the view to render the player reconnection message on
     */
    @Override
    public void render(ViewType view) {
        view.renderReconnection(message);
    }
}
