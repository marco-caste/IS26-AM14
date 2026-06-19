package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

/**
 * View event representing the end of the game, containing a message to be displayed.
 */
public class EndGameViewEvent implements ViewEvent {
    /**
     * The message to be displayed at the end of the game.
     */
    private final String message;

    /**
     * Constructs an EndGameViewEvent with the specified message.
     *
     * @param message the message to be displayed at the end of the game
     */
    public EndGameViewEvent(String message) {
        this.message = message;
    }

    /**
     * {@inheritDoc}
     * <p>
     *     For this class, enders the end game message using the provided view.
     * </p>
     *
     * @param view the view to render the end game message on
     */
    @Override
    public void render(ViewType view) {
        view.renderEndGame(message);
    }
}
