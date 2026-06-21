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
 * Launcher used to create a new RMI client instance.
 */
public class RmiClientLauncher {
    /**
     * Creates and returns a new unbound RMI client.
     * The actual connection to the server will happen later.
     *
     * @param controller the client controller to be associated with the RMI client
     * @return a new unbound instance of {@link RmiClient}
     * @throws IOException       if an I/O error occurs during the creation
     * @throws NotBoundException if the requested name is not currently bound in the registry
     * @throws RemoteException   if a remote communication-related exception occurs
     */
    public static RmiClient launch(ClientController controller) throws IOException, NotBoundException, RemoteException {

        // builds an unbounded RMI client, the connection will happen later
        return new RmiClient(controller);
    }
}
