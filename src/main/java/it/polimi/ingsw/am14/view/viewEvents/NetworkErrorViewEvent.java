package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

public class NetworkErrorViewEvent implements ViewEvent {
    private final String errorMessage;

    public NetworkErrorViewEvent(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public void render(ViewType view) {
        view.handleNetworkError(errorMessage);
    }
}
