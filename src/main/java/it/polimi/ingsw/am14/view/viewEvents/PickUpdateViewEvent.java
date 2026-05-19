package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

public class PickUpdateViewEvent implements ViewEvent{
    private final String message;

    public PickUpdateViewEvent(String message) {
        this.message = message;
    }

    @Override
    public void render(ViewType view) {
        view.renderCardPicked(message);
    }
}
