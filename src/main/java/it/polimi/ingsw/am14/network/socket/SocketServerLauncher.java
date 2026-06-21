package it.polimi.ingsw.am14.network.socket;

import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.network.socket.server.SocketClientHandlersManager;

import java.io.IOException;
import java.net.ServerSocket;
/**
 * Utility class responsible for initializing and launching the socket-based server.
 * It sets up the server sockets for both primary connections and secondary ping connections,
 * and delegates connection handling to a dedicated manager on separate threads.
 */
public class SocketServerLauncher {

    /**
     * Launches the socket server on the port specified in the arguments.
     * Creates two {@link ServerSocket} instances (one for main communication, one for pings on the adjacent port),
     * instantiates the {@link SocketClientHandlersManager}, and starts the listening network threads.
     *
     * @param args             the command-line arguments, where the first element is expected to be the base port number.
     * @param technicalObjects an array of required dependencies containing the {@link LobbyController} at index 0
     *                         and the {@link NetworkLayer} at index 1.
     */

    public static void launch(String[] args, Object[] technicalObjects) {
        try {
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
            ServerSocket serverPingSocket = new ServerSocket(Integer.parseInt(args[0])+1);
            
            SocketClientHandlersManager manager = new SocketClientHandlersManager(
                    serverSocket,
                    serverPingSocket,
                    (LobbyController) technicalObjects[0],
                    (NetworkLayer) technicalObjects[1]);
            System.err.println("[Server] Socket server launched and listening on port: " + args[0]);
            
            new Thread(() -> {
                try {
                    manager.runServer();
                } catch (IOException e) {
                    System.err.println("[Server] Error in main server thread: " + e.getMessage());
                }
            }, "SocketServer-main").start();
            
            new Thread(() -> {
                try {
                    manager.runPingServer();
                } catch (IOException e) {
                    System.err.println("[Server] Error in ping server thread: " + e.getMessage());
                }
            }, "SocketServer-ping").start();
        } catch (IOException e) {
            System.err.println("[Server] Fatal Error launching Socket server: " + e.getMessage());
        }
    }
}

