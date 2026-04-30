package it.polimi.ingsw.am14.socket;

import it.polimi.ingsw.am14.NetworkLayer;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.socket.server.SocketClientHandlersManager;

import java.io.IOException;
import java.net.ServerSocket;

public class SocketServerLauncher {

    public static void launch(String[] args, Object[] technicalObjects) {
        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]))) {
            SocketClientHandlersManager manager = new SocketClientHandlersManager(
                    serverSocket,
                    (LobbyController) technicalObjects[0],
                    (NetworkLayer) technicalObjects[1]);
            System.err.println("[Server] Socket server launched and listening on port: " + args[0]);
            manager.runServer();
        } catch (IOException e) {
            System.err.println("[Server] Fatal Error launching Socket server: " + e.getMessage());
        }
    }
}

