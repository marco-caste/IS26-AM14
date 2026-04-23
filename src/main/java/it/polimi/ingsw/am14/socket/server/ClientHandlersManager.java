package it.polimi.ingsw.am14.socket.server;

import it.polimi.ingsw.am14.NetworkLayer;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.events.ClientRequest;
import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;
import it.polimi.ingsw.am14.events.ServerPing;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ClientHandlersManager {

    private final ServerSocket listenSocket;
    private final LobbyController controller = new LobbyController();
    private final NetworkLayer networkLayer;

    private final Set<SocketClientHandler> waitingClients;

    public ClientHandlersManager(ServerSocket listenSocket) {
        this.listenSocket = listenSocket;
        this.waitingClients = new HashSet<>();
        this.networkLayer = new NetworkLayer();
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

                long lastPingTime = 0;

                while (true) {
                    synchronized (handler) {
                        while (handler.getEventBuffer().isEmpty()) {
                            // TODO: ping verso il client per capire se è ancora connesso.
                            handler.update(
                                    new ServerPing("ping")
                            );

                            try {
                                handler.wait();    // lazy wait
                            } catch (InterruptedException _) {}
                        }
                        List<Event> eventsToSend = handler.getEventBuffer().flushBuffer();
                        for (Event event : eventsToSend) {
                            handler.update(event);
                        }
//                        if (handler.hasDisconnected())
//                            break;
                    }
                }

            }).start();
        }
    }

    public void receiveRequest(ClientRequest request, SocketClientHandler sender){
        try {
            request.dispatchRequest(this.controller);
            int gameId = 0;
            try {
                gameId = this.controller.getGameIdByNickname(request.getNickname()).get();
            } catch (NoSuchElementException e) {
                throw new RuntimeException("[ClientHandlersManager] Nickname not found in any game");
            }
            networkLayer.updateMap(gameId, sender, request.getNickname());
            waitingClients.remove(sender);
        }
        catch (Exception e){
            sender.reportError(new ExceptionEvent(e.getMessage()));
        }
    }
}
