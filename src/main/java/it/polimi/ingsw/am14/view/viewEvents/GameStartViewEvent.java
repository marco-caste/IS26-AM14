package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.GUIView;
import it.polimi.ingsw.am14.view.TUIView;
import it.polimi.ingsw.am14.view.ViewType;

public class GameStartViewEvent implements ViewEvent{
    @Override
    public void render(ViewType view) {
        try {
            GUIView guiView = (GUIView) view;
            guiView.gameCreation();  // TODO: PRIVATO
        } catch (ClassCastException e) {
            TUIView tuiView = (TUIView) view;
        }
    }
}
