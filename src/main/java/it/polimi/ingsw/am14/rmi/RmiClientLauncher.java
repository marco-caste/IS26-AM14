package it.polimi.ingsw.am14.rmi;

import it.polimi.ingsw.am14.ViewType;
import it.polimi.ingsw.am14.VirtualServer;
import it.polimi.ingsw.am14.rmi.client.RmiClient;
import it.polimi.ingsw.am14.rmi.client.VirtualServerRmi;
import it.polimi.ingsw.am14.view.ClientModel;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiClientLauncher {

    // Configurazione RMI - Valori statici
    private static final String SERVER_NAME = "AM14MesosServer";

    public static VirtualServer launch(ViewType view) throws IOException, NotBoundException, RemoteException {

        ClientModel model = new ClientModel();
        model.registerObserver(view);

        return new RmiClient(model);

        // builds an unbounded RMI client, the connection will happen later
    }
}
