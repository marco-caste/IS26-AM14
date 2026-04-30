package it.polimi.ingsw.am14.rmi.client;

import it.polimi.ingsw.am14.VirtualServer;
import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;
import it.polimi.ingsw.am14.rmi.server.RmiServer;
import it.polimi.ingsw.am14.rmi.server.VirtualClientRmi;
import it.polimi.ingsw.am14.view.ClientModel;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiClient extends UnicastRemoteObject implements VirtualClientRmi, VirtualServer {

    private final ClientModel model;
    private static final String serverName = "AM14MesosServer";
    private VirtualServerRmi server;

    public RmiClient(ClientModel model) throws RemoteException {
        this.model = model;
    }

    @Override
    public void update(Event update) {
        new Thread(() -> {
            update.effect(model);
        }).start();
    }

    @Override
    public void reportError(ExceptionEvent error) {
        new Thread(() -> {
            error.effect(model);
        }).start();
    }

    // METHODS EXPOSED TO THE VIEW

    public void connect(VirtualClientRmi client, String nickname, String ipAddress, int port) throws RemoteException {
        Registry registry = LocateRegistry.getRegistry(ipAddress, port);
        try {
            this.server = (VirtualServerRmi) registry.lookup(serverName);
        } catch (NotBoundException e) {
            throw new RuntimeException(e.getMessage());
        }
        server.connect(this, nickname);
    }

    /**
     * @param nickname
     * @param numPlayers
     * @param totemColor
     * @throws RemoteException
     */
    @Override
    public void createGame(String nickname, int numPlayers, String totemColor) throws RemoteException {
        server.createGame(nickname, numPlayers, totemColor);
    }

    /**
     * @param nickname
     * @param gameId
     * @param totemColor
     * @throws RemoteException
     */
    @Override
    public void joinGame(String nickname, int gameId, String totemColor) throws RemoteException {
        server.joinGame(nickname, gameId, totemColor);
    }

    /**
     * @param nickname
     * @param position
     * @throws RemoteException
     */
    @Override
    public void placeTotem(String nickname, char position) throws RemoteException {
        server.placeTotem(nickname, position);
    }

    /**
     * @param nickname
     * @param cardId
     * @param row
     * @throws RemoteException
     */
    @Override
    public void pickCard(String nickname, int cardId, int row) throws RemoteException {
        server.pickCard(nickname, cardId, row);
    }
}
