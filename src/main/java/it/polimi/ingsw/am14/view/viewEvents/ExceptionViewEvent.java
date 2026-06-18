package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

/**
 * <p>
 *     View event representing an exception that occurred during the game. This event contains a message describing the exception, which can be rendered on the user interface to inform the player about the issue.
 * </p>
 */
public class ExceptionViewEvent implements ViewEvent {
    /**
     * The message describing the exception that occurred.
     */
    String exceptionMessage;

    /**
     * Constructs an ExceptionViewEvent with the specified exception message.
     *
     * @param exceptionMessage the message describing the exception that occurred
     */
    public ExceptionViewEvent(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    /**
     * {@inheritDoc}
     * <p>
     *     For this class, renders the exception message using the provided view.
     * </p>
     *
     * @param view the view to render the exception message on
     */
    @Override
    public void render(ViewType view) {
        view.renderException(exceptionMessage);
    }
}
