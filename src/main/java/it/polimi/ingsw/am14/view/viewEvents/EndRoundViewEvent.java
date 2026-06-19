package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

/**
 * View event representing the end of a round, containing a message to be displayed.
 */
public class EndRoundViewEvent implements ViewEvent {
    /**
     * The message to be displayed at the end of the round.
     */
    private final String message;

    /**
     * Constructs an EndRoundViewEvent with the specified message.
     *
     * @param message the message to be displayed at the end of the round
     */
    public EndRoundViewEvent(String message) {
        this.message = message;
    }

    /**
     * {@inheritDoc}
     * <p>
     *     For this class, renders the end round message using the provided view.
     * </p>
     *
     * @param view the view to render the end round message on
     */
    @Override
    public void render(ViewType view) {
        view.renderEndRound(message);
    }
}
