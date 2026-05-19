package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

public class LoginViewEvent implements ViewEvent {
    private final String message;

    public LoginViewEvent(String message) {
        this.message = message;
    }

    @Override
    public void render(ViewType view) {
        view.renderLogin(message);
    }
}
