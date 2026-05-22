package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

public class JoinedGameViewEvent implements ViewEvent{
    private final String message;

    public JoinedGameViewEvent(String message) {
        this.message = message;
    }

    @Override
    public void render(ViewType view) {
        view.renderPlayerJoin(message);
    }
}
