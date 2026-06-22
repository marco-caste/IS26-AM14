package it.polimi.ingsw.am14.network.rmi;

import it.polimi.ingsw.am14.clientModelController.ClientController;
import it.polimi.ingsw.am14.network.rmi.client.RmiClient;

import java.io.IOException;
import java.rmi.RemoteException;

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
     * @throws RemoteException   if a remote communication-related exception occurs
     */
    public static RmiClient launch(ClientController controller) throws IOException, RemoteException {

        // builds an unbounded RMI client, the connection will happen later
        return new RmiClient(controller);
    }
}
