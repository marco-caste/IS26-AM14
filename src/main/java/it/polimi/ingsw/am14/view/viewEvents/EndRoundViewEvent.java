package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.GUIView;
import it.polimi.ingsw.am14.view.TUIView;
import it.polimi.ingsw.am14.view.ViewType;

public class EndRoundViewEvent implements ViewEvent {
    @Override
    public void render(ViewType view) {
        try {
            GUIView guiView = (GUIView) view;
            guiView.updateBoard();  // TODO: PRIVATO
            guiView.updateSelectedTribe();  // TODO: PRIVATO
            guiView.updateTop("");  // TODO: PRIVATO, PARAMETRO NICKNAME INUTILIZZATO
            guiView.updatePlayerStats();  // TODO: PRIVATO
            guiView.updateoffersPathBox();  // TODO: PRIVATO
        } catch (ClassCastException e) {
            TUIView tuiView = (TUIView) view;
        }
    }
}
