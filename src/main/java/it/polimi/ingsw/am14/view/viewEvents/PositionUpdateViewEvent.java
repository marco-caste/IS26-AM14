package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

public class PositionUpdateViewEvent implements ViewEvent{
    private final String message;

    public PositionUpdateViewEvent(String message) {
        this.message = message;
    }

    @Override
    public void render(ViewType view) {
        view.renderTotemPlaced(message);
    }
}
