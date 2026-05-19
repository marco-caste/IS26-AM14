package it.polimi.ingsw.am14.view;

import it.polimi.ingsw.am14.view.viewEvents.ViewEvent;
import it.polimi.ingsw.am14.clientModelController.ClientModel;

import java.io.IOException;

public interface ViewType extends ModelObserver {
    void run() throws IOException;
    void update(ClientModel model, ViewEvent event);
    void disconnect();
    void renderLogin();
    void renderPlayerJoin();
    void renderGameStart();
    void renderTotemPlaced();
    void renderCardPicked();
    void renderEndRound();
    void renderEndGame();
    void renderDisconnection();
    void renderReconnection();
    void renderGameRestarted();
    void renderException();
    void handleNetworkError(String errorMessage);
}
