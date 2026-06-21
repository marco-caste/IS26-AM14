package it.polimi.ingsw.am14.network.rmi;

import it.polimi.ingsw.am14.clientModelController.ClientController;
import it.polimi.ingsw.am14.view.ViewType;
import it.polimi.ingsw.am14.network.rmi.client.RmiClient;
import it.polimi.ingsw.am14.clientModelController.ClientModel;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;

public class RmiClientLauncher {

    public static RmiClient launch(ClientController controller) throws IOException, NotBoundException, RemoteException {

        // builds an unbounded RMI client, the connection will happen later
        return new RmiClient(controller);
    }
}
