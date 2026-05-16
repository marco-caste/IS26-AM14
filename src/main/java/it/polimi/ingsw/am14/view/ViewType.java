package it.polimi.ingsw.am14.view;

import it.polimi.ingsw.am14.clientModelController.ClientModel;

import java.io.IOException;

public interface ViewType extends ModelObserver {
    void run() throws IOException;
    void update(ClientModel model,String eventChoice);
    void disconnect();
}
