package it.polimi.ingsw.am14.socket;

import it.polimi.ingsw.am14.socket.server.ClientHandlersManager;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerMain {
    public static final int port = 12345;

    public static void main() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            ClientHandlersManager manager = new ClientHandlersManager(serverSocket);
            manager.runServer();
        } catch (IOException e) {
            System.err.println("Errore avvio server: " + e.getMessage());
        }
    }
}

