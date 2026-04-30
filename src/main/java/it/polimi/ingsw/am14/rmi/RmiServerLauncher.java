package it.polimi.ingsw.am14.rmi;

import it.polimi.ingsw.am14.NetworkLayer;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.rmi.client.VirtualServerRmi;
import it.polimi.ingsw.am14.rmi.server.RmiServer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiServerLauncher {
    // TODO: nome server
    private static final String serverName = "AM14MesosServer";

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
