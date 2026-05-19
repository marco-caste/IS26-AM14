package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

public class EndRoundViewEvent implements ViewEvent {
    private final String message;

    public EndRoundViewEvent(String message) {
        this.message = message;
    }

    @Override
    public void render(ViewType view) {
        view.renderEndRound(message);
    }
}
