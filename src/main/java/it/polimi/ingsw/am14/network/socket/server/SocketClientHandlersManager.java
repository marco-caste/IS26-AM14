package it.polimi.ingsw.am14.network.socket.server;

import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.network.messages.requests.ClientRequest;
import it.polimi.ingsw.am14.network.messages.events.Event;
import it.polimi.ingsw.am14.network.messages.events.ExceptionEvent;
import it.polimi.ingsw.am14.utils.JsonCodec;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class SocketClientHandlersManager {

    private final ServerSocket listenSocket;
    private final ServerSocket listenPingSocket;
    private final LobbyController controller;
    private final NetworkLayer networkLayer;
    private final JsonCodec jsonCodec = new JsonCodec();

    private static int Thread_number = 0;

    private final Set<SocketClientHandler> waitingClients;

    // waitingClientsMethods
    public void addWaitingClient(SocketClientHandler handler) {
        synchronized (waitingClients) {
            waitingClients.add(handler);
        }
    }
    public void removeWaitingClient(SocketClientHandler handler) {
        synchronized (waitingClients) {
            waitingClients.remove(handler);
        }
    }
    public boolean waitingClientsContains(SocketClientHandler handler) {
        synchronized (waitingClients) {
            return waitingClients.contains(handler);
        }
    }

    public SocketClientHandlersManager(ServerSocket listenSocket, ServerSocket listenPingSocket, LobbyController controller, NetworkLayer networkLayer) {
        this.listenSocket = listenSocket;
        this.listenPingSocket = listenPingSocket;
        this.waitingClients = new HashSet<>();
        this.networkLayer = networkLayer;
        this.controller = controller;
    }

    public void runServer() throws IOException {
        Socket clientSocket;
        while ((clientSocket = this.listenSocket.accept()) != null) {
            try {
                InputStreamReader socketRx = new InputStreamReader(clientSocket.getInputStream());
                OutputStreamWriter socketTx = new OutputStreamWriter(clientSocket.getOutputStream());

                SocketClientHandler handler = new SocketClientHandler(
                        this,
                        new BufferedReader(socketRx),
                        new PrintWriter(socketTx),
                        clientSocket
                );

                addWaitingClient(handler);


                networkLayer.updateBufferMap(handler);

                // whenever a new client connects, two threads are created:
                // one listens for the client's requests
                // the other finds the correct time to flush the update queue to the client.
                Thread thread1 = new Thread(() -> {
                    try {
                        handler.runVirtualView();
                    } catch (IOException e) {
                        //TODO: sistemare
                        System.err.println("[Server] Connection lost for client handler: " + e.getMessage());
                        cleanupClient(handler);
                    } catch (Exception e) {
                        //TODO: sistemare
                        System.err.println("[Server] Connection lost or error in listener thread: " + e.getMessage());
                        cleanupClient(handler);
                    }
                }, "ServerThread-Listener" + Thread_number);
                thread1.start();

                Thread thread2 = new Thread(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            synchronized (networkLayer.getBuffer(handler)) {
                                if (Thread.currentThread().isInterrupted()) {
                                    return;
                                }
                                while (networkLayer.getBuffer(handler).isEmpty() 
                                       && !Thread.currentThread().isInterrupted()) {
                                    try {
                                        networkLayer.getBuffer(handler).wait();    // lazy wait
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        return;
                                    }
                                }
                                
                                if (!networkLayer.getBuffer(handler).isEmpty() 
                                    && !Thread.currentThread().isInterrupted()) {
                                    List<Event> eventsToSend = networkLayer.getBuffer(handler).flushBuffer();
                                    for (Event event : eventsToSend) {
                                        handler.update(event);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("[Server] Error in buffering thread: " + e.getMessage());
                    }
                }, "ServerThread-Buffering" + Thread_number);
                thread2.start();
                Thread_number++;

                networkLayer.updateClientThreads(handler, thread1);
                networkLayer.updateClientThreads(handler, thread2);
                
            } catch (IOException e) {
                //TODO: da sistemare
                System.err.println("[Server] Error setting up client handler: " + e.getMessage());
                try {
                    clientSocket.close();
                } catch (IOException ex) {
                    System.err.println("[Server] Error closing socket: " + ex.getMessage());
                }
            }
        }
    }

    public void runPingServer() throws IOException {
        Socket clientPingSocket;
        while ((clientPingSocket = this.listenPingSocket.accept()) != null) {
            try {
                InputStreamReader socketRx = new InputStreamReader(clientPingSocket.getInputStream());
                OutputStreamWriter socketTx = new OutputStreamWriter(clientPingSocket.getOutputStream());
                BufferedReader pingInput = new BufferedReader(socketRx);
                PrintWriter pingOutput= new PrintWriter(socketTx);

                String firstLine = pingInput.readLine();
                ClientRequest request = jsonCodec.parseClientRequest(firstLine);
                String nickname = request.getNickname();
                //TODO: Bisogna effettuare un controllo che non sia stato ancora preso?
                SocketClientHandler handler = (SocketClientHandler) networkLayer.getClientBy(nickname);
                handler.setPingSocket(pingInput, pingOutput, clientPingSocket, nickname);

                Thread pingThread = new Thread(() -> {
                    try {
                        handler.runPingServer();
                    } catch (Exception e) {
                        //TODO:
                    }
                }, "ServerThreadPing-" + nickname);
                pingThread.start();
                networkLayer.updateClientThreads(handler, pingThread);
                pingServer(nickname); //TODO: è ridondante?
            }
            catch (Exception e) {
                //TODO:
            }
        }
    }

    public void receiveRequest(ClientRequest request, SocketClientHandler sender){
        try {
            // if sender is still in waiting room, the only possible request is to connect to the server (meaning: to declare a nickname).
            if (waitingClientsContains(sender)) {
                networkLayer.updateNicknameMap(request.getNickname(), sender);
            }

            request.dispatchRequest(this.controller);

            removeWaitingClient(sender);

            this.controller.getGameIdByNickname(request.getNickname()).ifPresentOrElse(
                    (id) -> networkLayer.updateClients(id, sender),
                    () -> { }
            );
        }
        catch (Exception e){
            sender.reportError(new ExceptionEvent(e.getMessage()));
        }
    }

    public void pingServer(String nickname){
        controller.handlePingRequest(nickname);
    }

    private void cleanupClient(SocketClientHandler handler) {
        try {
            networkLayer.notifyBufferForClient(handler);
            networkLayer.removeThreadsByClient(handler);
            networkLayer.removeBuffer(handler);
            removeWaitingClient(handler);
            handler.closeConnection();
            System.err.println("[Server] Client handler cleaned up successfully");
        } catch (Exception e) {
            System.err.println("[Server] Error during client cleanup: " + e.getMessage());
        }
    }
}
