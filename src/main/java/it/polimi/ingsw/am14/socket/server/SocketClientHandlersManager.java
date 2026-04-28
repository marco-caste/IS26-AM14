package it.polimi.ingsw.am14.socket.server;

import it.polimi.ingsw.am14.EventBuffer;
import it.polimi.ingsw.am14.NetworkLayer;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.events.ClientRequest;
import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class SocketClientHandlersManager {

    private final ServerSocket listenSocket;
    private final LobbyController controller;
    private final NetworkLayer networkLayer;

    private final Set<SocketClientHandler> waitingClients;

    public SocketClientHandlersManager(ServerSocket listenSocket) {
        this.listenSocket = listenSocket;
        this.waitingClients = new HashSet<>();
        // TODO: COSTRUZIONE DA FARE NEL MAIN
        this.networkLayer = new NetworkLayer();
        this.controller = new LobbyController(networkLayer);
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

            networkLayer.updateBufferMap(handler);

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
                    synchronized (networkLayer.getBuffer(handler)) {
                        while (networkLayer.getBuffer(handler).isEmpty()) {
                            // TODO: ping verso il client per capire se è ancora connesso.
//                            handler.update(
//                                    new ServerPing("ping")
//                            );

                            try {
                                networkLayer.getBuffer(handler).wait();    // lazy wait
                            } catch (InterruptedException _) {}
                        }
                        List<Event> eventsToSend = networkLayer.getBuffer(handler).flushBuffer();
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
            // if sender is still in waiting room, the only possible request is to connect to the server (meaning: to declare a nickname).
            if (waitingClients.contains(sender))
                networkLayer.updateNicknameMap(request.getNickname(), sender);

            request.dispatchRequest(this.controller);

            if (waitingClients.contains(sender))
                waitingClients.remove(sender);

            this.controller.getGameIdByNickname(request.getNickname()).ifPresentOrElse(
                    (id) -> {
                        networkLayer.updateClients(id, sender);
                    },
                    () -> { }
            );
        }
        catch (Exception e){
            sender.reportError(new ExceptionEvent(e.getMessage()));
        }
    }
}
