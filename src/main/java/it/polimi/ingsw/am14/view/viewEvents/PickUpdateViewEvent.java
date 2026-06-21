package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

/**
 * View event representing an update when a card is picked, containing a message to be displayed.
 */
public class PickUpdateViewEvent implements ViewEvent{
    /**
     * The message to be displayed when a card is picked.
     */
    private final String message;

    /**
     * Constructs a PickUpdateViewEvent with the specified message.
     *
     * @param message the message to be displayed when a card is picked
     */
    public PickUpdateViewEvent(String message) {
        this.message = message;
    }

    /**
     * {@inheritDoc}
     * <p>
     *     For this class, renders the card picked message using the provided view.
     * </p>
     *
     * @param view the view to render the card picked message on
     */
    @Override
    public void render(ViewType view) {
        view.renderCardPicked(message);
    }
}
