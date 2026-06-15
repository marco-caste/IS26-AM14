package it.polimi.ingsw.am14.network.rmi.client;

import it.polimi.ingsw.am14.clientModelController.ClientController;
import it.polimi.ingsw.am14.network.VirtualServer;
import it.polimi.ingsw.am14.network.messages.events.Event;
import it.polimi.ingsw.am14.network.messages.events.ExceptionEvent;
import it.polimi.ingsw.am14.network.rmi.server.VirtualClientRmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static it.polimi.ingsw.am14.utils.Constants.*;

public class RmiClient extends UnicastRemoteObject implements VirtualClientRmi, VirtualServer {

    private final ClientController controller;
    private VirtualServerRmi server;

    public RmiClient(ClientController controller) throws RemoteException {
        super();
        this.controller = controller;
    }

    @Override
    public void update(Event update) {
        new Thread(() -> {
            controller.update(update);
        }).start();
    }

    @Override
    public void reportError(ExceptionEvent error) {
        new Thread(() -> {
            controller.update(error);
        }).start();
    }

    @Override
    public void pingClient() throws RemoteException {
        controller.updateLastPing();
    }

    // METHODS EXPOSED TO THE VIEW
    public void connect(String nickname, String ipAddress, int port) throws RemoteException {
        Registry registry = LocateRegistry.getRegistry(ipAddress, port);
        try {
            server = (VirtualServerRmi) registry.lookup(serverName);
            server.connect(this, nickname);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
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

    @Override
    public Thread pingServer(String nickname){
        Thread thread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(PING_INTERVAL);
                    server.pingServer(nickname);
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }, "RMIClient-Pinger");
        thread.start();
        return thread;
    }
}
