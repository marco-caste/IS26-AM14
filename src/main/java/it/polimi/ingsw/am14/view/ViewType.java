package it.polimi.ingsw.am14.view;

import it.polimi.ingsw.am14.clientModel.ClientModel;
import it.polimi.ingsw.am14.view.viewEvents.ViewEvent;

import java.io.IOException;

public interface ViewType extends ModelObserver {
    void run() throws IOException;
    // TODO: ANTIPATTERN degli eventi, INSERIRE METODI QUI
    void update(ClientModel model, ViewEvent event);
    void disconnect();
}
