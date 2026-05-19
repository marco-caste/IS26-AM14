package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.GUIView;
import it.polimi.ingsw.am14.view.TUIView;
import it.polimi.ingsw.am14.view.ViewType;

public class EndGameViewEvent implements ViewEvent{
    @Override
    public void render(ViewType view) {
        try {
            GUIView guiView = (GUIView) view;
            guiView.showEndGameScene();
        } catch (ClassCastException e) {
            TUIView tuiView = (TUIView) view;
        }
    }
}
