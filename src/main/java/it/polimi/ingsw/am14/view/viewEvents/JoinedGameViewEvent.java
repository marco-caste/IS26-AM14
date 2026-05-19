package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

public class JoinedGameViewEvent implements ViewEvent{
    @Override
    public void render(ViewType view) {
        view.renderPlayerJoin();
    }
}
