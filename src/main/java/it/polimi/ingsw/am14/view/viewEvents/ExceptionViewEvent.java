package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.GUIView;
import it.polimi.ingsw.am14.view.TUIView;
import it.polimi.ingsw.am14.view.ViewType;

public class ExceptionViewEvent implements ViewEvent {
    String exceptionMessage;

    public ExceptionViewEvent(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public void render(ViewType view) {
        view.renderException();
    }
}
