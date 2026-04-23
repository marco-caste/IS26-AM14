package it.polimi.ingsw.am14.socket.server;

import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.events.ClientRequest;
import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;
import it.polimi.ingsw.am14.exceptions.ModelException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientHandlersManager {

    private final ServerSocket listenSocket;
    private final LobbyController controller = new LobbyController();
    private static final List<SocketClientHandler> clients = new ArrayList<>();

    public ClientHandlersManager(ServerSocket listenSocket) {
        this.listenSocket = listenSocket;
    }

    public void runServer() throws IOException {
        Socket clientSocket = null;
        while ((clientSocket = this.listenSocket.accept()) != null) {
            InputStreamReader socketRx = new InputStreamReader(clientSocket.getInputStream());
            OutputStreamWriter socketTx = new OutputStreamWriter(clientSocket.getOutputStream());

            SocketClientHandler handler = new SocketClientHandler(
                    this,
                    new BufferedReader(socketRx),
                    new PrintWriter(socketTx)
            );

            synchronized(clients) {
                clients.add(handler);
            }

            new Thread(() -> {
                try {
                    handler.runVirtualView();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    public void receiveRequest(ClientRequest request, SocketClientHandler sender){
        try {
            request.dispatchRequest(this.controller);
            //TODO broadcast update to all clients: passare il game id nella creazione del clientrequest
        }
        catch (Exception e) {
            sender.reportError(new ExceptionEvent(e.getMessage()));
        }
    }

    public static void broadcastUpdate(Event update) {
        synchronized (clients) {
            for (var client : clients) {
                client.update(update);
            }
        }
    }

}
