package it.polimi.ingsw.am14.socket.server;

import it.polimi.ingsw.am14.NetworkLayer;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.events.ClientRequest;
import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ClientHandlersManager {

    private final ServerSocket listenSocket;
    private final LobbyController controller = new LobbyController();
    private final NetworkLayer networkLayer;

    private final Map<Integer, Set<SocketClientHandler>> gameIdToClients;
    private final Set<SocketClientHandler> waitingClients;

    public ClientHandlersManager(ServerSocket listenSocket) {
        this.listenSocket = listenSocket;
        this.waitingClients = new HashSet<>();
        this.gameIdToClients = new HashMap<>();
        this.networkLayer = new NetworkLayer();
    }

    private void runServer() throws IOException {
        Socket clientSocket = null;
        while ((clientSocket = this.listenSocket.accept()) != null) {
            InputStreamReader socketRx = new InputStreamReader(clientSocket.getInputStream());
            OutputStreamWriter socketTx = new OutputStreamWriter(clientSocket.getOutputStream());

            SocketClientHandler handler = new SocketClientHandler(
                    this,
                    new BufferedReader(socketRx),
                    new PrintWriter(socketTx)
            );

            synchronized(this.waitingClients) {
                waitingClients.add(handler);
            }


            // whenever a new client connects, two threads are created:
            // one listens for the client's requests
            // the other finds the correct time to flush the update queue to the client.
            new Thread(() -> {
                try {
                    handler.runVirtualView();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            new Thread(() -> {

                while (true) {
                    synchronized (handler.getEventBuffer()) {       // possibile?
                        while (handler.getEventBuffer().isEmpty()) {
                            try {
                                handler.getEventBuffer().wait();    // lazy wait
                            } catch (InterruptedException e) {}
                        }
                        List<Event> eventsToSend = handler.getEventBuffer().flushBuffer();
                        for (Event event : eventsToSend) {
                            handler.update(event);
                        }
                    }
                    // must break at some point, e.g. when client disconnects.
                }

            }).start();

            // TODO: thread that pings clients to check if they are still alive
        }
    }

    public void receiveRequest(ClientRequest request, SocketClientHandler sender){
        try {
            request.dispatchRequest(this.controller);
            int gameId = this.controller.getGameIdByNickname(request.getNickname()).get();
            networkLayer.updateMap(gameId, sender);
        }
        catch (Exception e){
            sender.reportError(new ExceptionEvent(e.getMessage()));
        }
    }
}
