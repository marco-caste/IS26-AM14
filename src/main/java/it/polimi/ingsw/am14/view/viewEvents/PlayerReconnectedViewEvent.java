package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

public class PlayerReconnectedViewEvent implements ViewEvent{
    private final String message;

    public PlayerReconnectedViewEvent(String message) {
        this.message = message;
    }

    @Override
    public void render(ViewType view) {
        view.renderReconnection(message);
    }
}
