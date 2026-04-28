package it.polimi.ingsw.am14.socket;

import it.polimi.ingsw.am14.socket.server.SocketClientHandlersManager;

import java.io.IOException;
import java.net.ServerSocket;

public class SocketServerMain {
    public static final int port = 12345;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            SocketClientHandlersManager manager = new SocketClientHandlersManager(serverSocket);
            manager.runServer();
        } catch (IOException e) {
            System.err.println("Errore avvio server: " + e.getMessage());
        }
    }
}

