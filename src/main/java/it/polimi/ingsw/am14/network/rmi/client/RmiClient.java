package it.polimi.ingsw.am14.network.rmi.client;

import it.polimi.ingsw.am14.network.VirtualServer;
import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;
import it.polimi.ingsw.am14.network.rmi.server.VirtualClientRmi;
import it.polimi.ingsw.am14.clientModel.ClientModel;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static it.polimi.ingsw.am14.utils.Constants.*;

public class RmiClient extends UnicastRemoteObject implements VirtualClientRmi, VirtualServer {

    private final ClientModel model;
    private VirtualServerRmi server;
    private static long lastPingReceived;

    public RmiClient(ClientModel model) throws RemoteException {
        super();
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

    @Override
    public void pingClient() throws RemoteException {
        // TODO: receiving pings from server
        // if timer expires, disconnect from server and notify the view
        // TODO: how to disconnect rmi client from server?
        // current solution: will call again connect, which overwrites the server reference
        lastPingReceived = System.currentTimeMillis();
    }

    // METHODS EXPOSED TO THE VIEW

    public void connect(VirtualClientRmi client, String nickname, String ipAddress, int port) throws RemoteException {
        Registry registry = LocateRegistry.getRegistry(ipAddress, port);
        try {
            this.server = (VirtualServerRmi) registry.lookup(serverName);
            server.connect(this, nickname);
            this.pingServer(nickname);
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
    public void pingServer(String nickname) throws Exception {
        // starts sending pings to the server
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(PING_INTERVAL); // PING_INTERVAL

                    server.pingServer(nickname);
                }
            } catch (InterruptedException | RemoteException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        new Thread (() -> {
            try {
                Thread.sleep(PING_TIMEOUT);

                long now = System.currentTimeMillis();
                if (now - lastPingReceived > PING_TIMEOUT) {
                    // timeout expired, disconnect from server and notify the view
                    model.setMessage("[Client] Network error: server is not responding. Try to reconnect later.\nShutting down connection...");
                    //model.disconnect();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage());
            }
        }).start();
    }
}
