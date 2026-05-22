package it.polimi.ingsw.am14.view;

import it.polimi.ingsw.am14.view.viewEvents.ViewEvent;
import it.polimi.ingsw.am14.clientModelController.ClientModel;

import java.io.IOException;

public interface ViewType extends ModelObserver {
    void run() throws IOException;
    void update(ClientModel model, ViewEvent event);
    void renderLogin(String message);
    void renderPlayerJoin(String message);
    void renderGameStart(String message);
    void renderTotemPlaced(String message);
    void renderCardPicked(String message);
    void renderEndRound(String message);
    void renderEndGame(String message);
    void renderDisconnection(String message);
    void renderReconnection(String message);
    void renderGameRestarted(String message);
    void renderException(String message);
    void handleNetworkError(String errorMessage);
}
