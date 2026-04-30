package it.polimi.ingsw.am14.rmi;

import it.polimi.ingsw.am14.ViewType;
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
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 1099;
    private static final String SERVER_NAME = "AM14MesosServer";
    
    private static RmiClient lastClient = null;  // ⚠️ Workaround temporaneo


    public static VirtualServerRmi launch(ViewType view) 
        throws IOException, NotBoundException, RemoteException {

        ClientModel model = new ClientModel();
        model.registerObserver(view);
        RmiClient client = new RmiClient(model);
        lastClient = client;  // ⚠️ Salva per uso successivo

        Registry registry = LocateRegistry.getRegistry(DEFAULT_HOST, DEFAULT_PORT);
        VirtualServerRmi server = (VirtualServerRmi) registry.lookup(SERVER_NAME);

        return server;
    }
    
    // ⚠️ Metodo temporaneo per recuperare il client RMI appena creato
    public static RmiClient getLastClient() {
        return lastClient;
    }
}
