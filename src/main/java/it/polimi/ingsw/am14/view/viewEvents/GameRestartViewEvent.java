package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

/**
 * View event representing the restart of the game, containing a message to be displayed.
 */
public class GameRestartViewEvent implements ViewEvent{
    /**
     * The message to be displayed when the game is restarted.
     */
    private final String message;

    /**
     * Constructs a GameRestartViewEvent with the specified message.
     *
     * @param message the message to be displayed when the game is restarted
     */
    public GameRestartViewEvent(String message) {
        this.message = message;
    }

    /**
     * {@inheritDoc}
     * <p>
     *     For this class, renders the game restarted message using the provided view.
     * </p>
     *
     * @param view the view to render the game restarted message on
     */
    @Override
    public void render(ViewType view) {
        view.renderGameRestarted(message);
    }
}
