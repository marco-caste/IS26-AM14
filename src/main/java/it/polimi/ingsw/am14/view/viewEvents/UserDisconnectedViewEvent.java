package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

public class UserDisconnectedViewEvent implements ViewEvent{
    private final String message;

    public UserDisconnectedViewEvent(String message) {
        this.message = message;
    }

    @Override
    public void render(ViewType view) {
        view.renderDisconnection(message);
    }
}
