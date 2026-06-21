package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

/**
 * View event representing a network error, containing an error message to be displayed.
 */
public class NetworkErrorViewEvent implements ViewEvent {
    /**
     * The error message to be displayed when a network error occurs.
     */
    private final String errorMessage;

    /**
     * Constructs a NetworkErrorViewEvent with the specified error message.
     *
     * @param errorMessage the error message to be displayed when a network error occurs
     */
    public NetworkErrorViewEvent(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * {@inheritDoc}
     * <p>
     *     For this class, renders the network error message using the provided view.
     * </p>
     *
     * @param view the view to render the network error message on
     */
    @Override
    public void render(ViewType view) {
        view.handleNetworkError(errorMessage);
    }
}
