package it.polimi.ingsw.am14.network.rmi;

import it.polimi.ingsw.am14.view.ViewType;
import it.polimi.ingsw.am14.network.rmi.client.RmiClient;
import it.polimi.ingsw.am14.clientModelController.ClientModel;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;

public class RmiClientLauncher {

    public static Map<RmiClient, ClientModel> launch(ViewType view) throws IOException, NotBoundException, RemoteException {

        ClientModel model = new ClientModel();
        model.registerObserver(view);

        RmiClient client = new RmiClient(model);

        // builds an unbounded RMI client, the connection will happen later
        return Map.of(client, model);
    }
}
