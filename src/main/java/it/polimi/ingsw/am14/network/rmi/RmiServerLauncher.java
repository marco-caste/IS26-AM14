package it.polimi.ingsw.am14.network.rmi;

import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.network.rmi.client.VirtualServerRmi;
import it.polimi.ingsw.am14.network.rmi.server.RmiServer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static it.polimi.ingsw.am14.utils.Constants.serverName;
/**
 * Launcher class responsible for initializing and starting the RMI server.
 * It sets up the local RMI registry and binds the remote server object to it,
 * making it accessible to remote clients.
 */
public class RmiServerLauncher {

    /**
     * Launches the RMI server by creating an instance of {@link RmiServer} and binding it to the RMI registry.
     *
     * @param args             an array of command-line arguments where the second element ({@code args[1]}) represents the port number for the RMI registry.
     * @param technicalObjects an array containing the core backend dependencies needed by the server, specifically the {@link LobbyController} at index 0 and {@link NetworkLayer} at index 1.
     */
    public static void launch(String[] args, Object[] technicalObjects) {
        try {
            VirtualServerRmi server = new RmiServer(
                    (LobbyController) technicalObjects[0],
                    (NetworkLayer) technicalObjects[1]
            );
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(args[1]));
            registry.rebind(serverName, server);
            System.err.println("[Server] RMI server launched and bound to registry with name " + serverName + " on port " + args[1]);
        } catch (RemoteException e) {
            System.err.println("[Server] Fatal error launching RMI server: " + e.getMessage());
        }

    }
}
