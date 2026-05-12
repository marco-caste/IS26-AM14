package it.polimi.ingsw.am14.view;

import it.polimi.ingsw.am14.clientModel.ClientModel;

import java.io.IOException;

public interface ViewType extends ModelObserver {
    void run() throws IOException;
    void update(ClientModel model);
    void disconnect();
}
