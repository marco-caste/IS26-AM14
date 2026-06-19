package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

/**
 * View event representing a player joining the game, containing a message to be displayed.
 */
public class JoinedGameViewEvent implements ViewEvent{
    /**
     * The message to be displayed when a player joins the game.
     */
    private final String message;

    /**
     * Constructs a JoinedGameViewEvent with the specified message.
     *
     * @param message the message to be displayed when a player joins the game
     */
    public JoinedGameViewEvent(String message) {
        this.message = message;
    }

    /**
     * {@inheritDoc}
     * <p>
     *     For this class, renders the player join message using the provided view.
     * </p>
     *
     * @param view the view to render the player join message on
     */
    @Override
    public void render(ViewType view) {
        view.renderPlayerJoin(message);
    }
}
