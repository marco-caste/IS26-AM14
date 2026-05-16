package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.GUIView;
import it.polimi.ingsw.am14.view.TUIView;
import it.polimi.ingsw.am14.view.ViewType;

public class ExceptionViewEvent implements ViewEvent {

    @Override
    public void render(ViewType view) {
        try {
            GUIView guiView = (GUIView) view;
            guiView.updateTop();  // TODO: PRIVATO, PARAMETRO NICKNAME NON UTILIZZATO
            guiView.updateChoiceScene();  // TODO: PRIVATO
            guiView.updateLogin();  // TODO: PRIVATO
        } catch (ClassCastException e) {
            TUIView tuiView = (TUIView) view;
        }
    }
}
