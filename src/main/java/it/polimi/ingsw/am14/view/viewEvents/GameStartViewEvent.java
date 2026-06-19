package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

/**
 * View event representing the start of the game, containing a message to be displayed.
 */
public class GameStartViewEvent implements ViewEvent{
    /**
     * The message to be displayed when the game starts.
     */
    private final String message;

    /**
     * Constructs a GameStartViewEvent with the specified message.
     *
     * @param message the message to be displayed when the game starts
     */
    public GameStartViewEvent(String message) {
        this.message = message;
    }

    /**
     * {@inheritDoc}
     * <p>
     *     For this class, renders the game start message using the provided view.
     * </p>
     *
     * @param view the view to render the game start message on
     */
    @Override
    public void render(ViewType view) {
        view.renderGameStart(message);
    }
}
