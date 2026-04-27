package it.polimi.ingsw.am14.rmi.server;

import it.polimi.ingsw.am14.NetworkLayer;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.rmi.client.VirtualServerRmi;

public class RmiClientHandlerManager implements VirtualServerRmi {

    private final LobbyController lobbyController;
    private final NetworkLayer networkLayer;

    public RmiClientHandlerManager(LobbyController lobbyController, NetworkLayer networkLayer) {
        this.lobbyController = lobbyController;
        this.networkLayer = networkLayer;
    }

    @Override
    public void connectUser(String nickname) {

    }

    @Override
    public void createGame(String nickname, int numPlayers, String totemColor) {

    }

    @Override
    public void joinGame(String nickname, int gameId, String totemColor) {

    }

    @Override
    public void placeTotem(String nickname, char position) {

    }

    @Override
    public void pickCard(String nickname, int cardId, int row) {

    }
}
