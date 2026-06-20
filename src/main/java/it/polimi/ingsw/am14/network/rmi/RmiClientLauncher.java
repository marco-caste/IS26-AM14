package it.polimi.ingsw.am14.network.rmi;

import it.polimi.ingsw.am14.clientModelController.ClientController;
import it.polimi.ingsw.am14.view.ViewType;
import it.polimi.ingsw.am14.network.rmi.client.RmiClient;
import it.polimi.ingsw.am14.clientModelController.ClientModel;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;
/**
 * Launcher class responsible for setting up the RMI client environment.
 * It initializes the core MVC components for the client application before establishing
 * a remote connection to the server.
 */
public class RmiClientLauncher {

    /**
     * Initializes the client model, registers the chosen view as its observer, and instantiates the RMI client.
     * Generates an unbounded RMI client; the actual registry lookup and connection occur at a later stage.
     *
     * @param view       the {@link ViewType} representing the user interface, acting as an observer.
     * @param controller the {@link ClientController} managing client-side application logic.
     * @return a {@link Map} containing the newly created {@link RmiClient} as the key and the associated {@link ClientModel} as the value.
     * @throws IOException       if an I/O error occurs during the instantiation process.
     * @throws NotBoundException if an attempt to look up the remote server fails.
     * @throws RemoteException   if an error occurs in the RMI communication setup.
     */
    public static Map<RmiClient, ClientModel> launch(ViewType view, ClientController controller) throws IOException, NotBoundException, RemoteException {

        ClientModel model = new ClientModel();
        model.registerObserver(view);

        RmiClient client = new RmiClient(controller);

        // builds an unbounded RMI client, the connection will happen later
        return Map.of(client, model);
    }
}
