package it.polimi.ingsw.am14.network;

import it.polimi.ingsw.am14.clientModelController.ClientController;
import it.polimi.ingsw.am14.network.rmi.RmiClientLauncher;
import it.polimi.ingsw.am14.network.rmi.client.RmiClient;
import it.polimi.ingsw.am14.network.socket.SocketClientLauncher;
import it.polimi.ingsw.am14.network.socket.client.VirtualServerSocket;

public abstract class EntryPointFactory {
    /**
     * Establishes a connection to the server using the specified connection parameters.
     *
     * @param connectionMode the network protocol to use ("socket" or "rmi")
     * @param serverIP the IP address of the server
     * @param serverPort the port number of the server
     * @param playerNickname the nickname of the player connecting
     * @return a VirtualServer instance representing the connected server
     * @throws IllegalArgumentException if an invalid connection mode is provided
     * @throws RuntimeException if connection fails
     */
    public static VirtualServer connect(String connectionMode, String serverIP, int serverPort, String playerNickname, ClientController controller) {
        switch (connectionMode.toLowerCase()) {
            case "socket" -> {
                try {
                    // Create and launch Socket client with explicit parameters
                    VirtualServerSocket server = SocketClientLauncher.launch(
                            serverIP,
                            serverPort,
                            controller
                    );
                    server.connectUser(playerNickname);
                    return server;
                } catch (Exception e) {
                    throw new RuntimeException("Socket connection failed: " + e.getMessage());
                }
            }
            case "rmi" -> {
                try {
                    RmiClient server = RmiClientLauncher.launch(controller);
                    server.connect(playerNickname, serverIP, serverPort);
                    return server;
                } catch (Exception e) {
                    throw new RuntimeException("RMI connection failed: " + e.getMessage());
                }
            }
            default -> throw new IllegalArgumentException("Invalid connection mode: '" + connectionMode + "'. Use 'socket' or 'rmi'.");
        }
    }
}