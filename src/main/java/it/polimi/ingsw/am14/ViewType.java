package it.polimi.ingsw.am14;

import it.polimi.ingsw.am14.view.ClientModel;
import it.polimi.ingsw.am14.view.ModelObserver;

import java.io.IOException;

public interface ViewType extends ModelObserver {
    void run() throws IOException;
    void update(ClientModel model);
}
