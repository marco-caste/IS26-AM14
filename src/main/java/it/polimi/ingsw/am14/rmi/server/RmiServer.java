package it.polimi.ingsw.am14.rmi.server;

import it.polimi.ingsw.am14.NetworkLayer;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.exceptions.*;
import it.polimi.ingsw.am14.rmi.client.VirtualServerRmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

public class RmiServer extends UnicastRemoteObject implements VirtualServerRmi {

    private final LobbyController lobbyController;
    private final NetworkLayer networkLayer;

    public RmiServer(LobbyController lobbyController, NetworkLayer networkLayer) throws RemoteException {
        this.lobbyController = lobbyController;
        this.networkLayer = networkLayer;
    }

    @Override
    public void connect(VirtualClientRmi client) throws RemoteException {
        new Thread(() -> networkLayer.addWaitingClient(client)).start();
    }

    @Override
    public void connectUser(String nickname) {
        new Thread(() -> {
            try {
                lobbyController.connectUser(nickname);
            } catch (IllegalNicknameException e) {
                throw new RuntimeException(e.getMessage());
            }
        }).start();
    }

    @Override
    public void createGame(String nickname, int numPlayers, String totemColor) {
        new Thread(() -> {
            try {
                lobbyController.createGame(nickname, numPlayers, totemColor);
            } catch (IllegalSetupException | WrongStateException | IllegalNicknameException e) {
                throw new RuntimeException(e.getMessage());
            }
        }).start();
    }

    @Override
    public void joinGame(String nickname, int gameId, String totemColor) {
        new Thread(() -> {
            try {
                lobbyController.joinGame(nickname, gameId, totemColor);
            } catch (WrongStateException | IllegalGameIdException e) {
                throw new RuntimeException(e.getMessage());
            }
        }).start();
    }

    @Override
    public void placeTotem(String nickname, char position) {
        new Thread(() -> {
                try {
                    lobbyController.placeTotem(nickname, position);
                } catch (IllegalNicknameException | WrongStateException | WrongTurnException | UnknownPositionException | IllegalPlacementException | UnknownNicknameException e) {
                    throw new RuntimeException(e.getMessage());
                }
        }).start();
    }

    @Override
    public void pickCard(String nickname, int cardId, int row) {
        new Thread(() -> {
            try {
                lobbyController.pickCard(nickname, cardId, row);
            } catch (IllegalNicknameException | WrongStateException | WrongTurnException | UnknownPositionException | IllegalPickException | ForcedPickException | IllegalPlacementException | UnknownNicknameException |
                     SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }).start();
    }

}
