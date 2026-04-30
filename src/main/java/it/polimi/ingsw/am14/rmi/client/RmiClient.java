package it.polimi.ingsw.am14.rmi.client;

import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;
import it.polimi.ingsw.am14.rmi.server.VirtualClientRmi;
import it.polimi.ingsw.am14.view.ClientModel;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiClient extends UnicastRemoteObject implements VirtualClientRmi {

    private final ClientModel model;
    private static final String serverName = "AM14MesosServer";

    public RmiClient(ClientModel model) throws RemoteException {
        this.model = model;
    }

    public void connect(String ipAddress, String nickname, int port) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(ipAddress, port);
        VirtualServerRmi server = (VirtualServerRmi) registry.lookup(serverName);
        server.connect(this, nickname);
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
}
