package it.polimi.ingsw.am14.network;

import it.polimi.ingsw.am14.clientModelController.ClientModel;
import it.polimi.ingsw.am14.network.rmi.RmiClientLauncher;
import it.polimi.ingsw.am14.network.rmi.client.RmiClient;
import it.polimi.ingsw.am14.network.socket.SocketClientLauncher;
import it.polimi.ingsw.am14.network.socket.client.VirtualServerSocket;
import it.polimi.ingsw.am14.view.ViewType;

import java.util.Map;

public abstract class EntryPointFactory {
    /**
     * Establishes a connection to the server using the specified connection parameters.
     * 
     * @param connectionMode the network protocol to use ("socket" or "rmi")
     * @param serverIP the IP address of the server
     * @param serverPort the port number of the server
     * @param playerNickname the nickname of the player connecting
     * @param view the caller view (UI)
     * @return an Object array {VirtualServer, ClientModel}
     * @throws IllegalArgumentException if an invalid connection mode is provided
     * @throws RuntimeException if connection fails
     */
    public static Object [] connect(String connectionMode, String serverIP, int serverPort, String playerNickname, ViewType view) {
        switch (connectionMode.toLowerCase()) {
            case "socket" -> {
                try {
                    // Create and launch Socket client with explicit parameters
                    Map<VirtualServerSocket, ClientModel> result = SocketClientLauncher.launch(
                            serverIP,
                            serverPort,
                            view
                    );
                    VirtualServerSocket server = result.keySet().iterator().next();
                    server.connectUser(playerNickname);
                    return new Object[]{server, result.get(server)};
                } catch (Exception e) {
                    throw new RuntimeException("Socket connection failed: " + e.getMessage());
                }
            }
            case "rmi" -> {
                try {
                    Map<RmiClient, ClientModel> result = RmiClientLauncher.launch(view);
                    RmiClient server = result.keySet().iterator().next();
                    server.connect(playerNickname, serverIP, serverPort);
                    return new Object[]{server, result.get(server)};
                } catch (Exception e) {
                    throw new RuntimeException("RMI connection failed: " + e.getMessage());
                }
            }
            default -> throw new IllegalArgumentException("Invalid connection mode: '" + connectionMode + "'. Use 'socket' or 'rmi'.");
        }
    }
}