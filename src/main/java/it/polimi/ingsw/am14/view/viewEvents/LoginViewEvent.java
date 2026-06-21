package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

/**
 * View event representing a login action, containing a message to be displayed.
 */
public class LoginViewEvent implements ViewEvent {
    /**
     * The message to be displayed during the login process.
     */
    private final String message;

    /**
     * Constructs a LoginViewEvent with the specified message.
     *
     * @param message the message to be displayed during the login process
     */
    public LoginViewEvent(String message) {
        this.message = message;
    }

    /**
     * {@inheritDoc}
     * <p>
     *     For this class, renders the login message using the provided view.
     * </p>
     *
     * @param view the view to render the login message on
     */
    @Override
    public void render(ViewType view) {
        view.renderLogin(message);
    }
}
