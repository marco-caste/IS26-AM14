package it.polimi.ingsw.am14.network.rmi;

import it.polimi.ingsw.am14.view.ViewType;
import it.polimi.ingsw.am14.network.VirtualServer;
import it.polimi.ingsw.am14.network.rmi.client.RmiClient;
import it.polimi.ingsw.am14.clientModel.ClientModel;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

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
