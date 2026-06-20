package it.polimi.ingsw.am14.network.socket.server;

import it.polimi.ingsw.am14.network.EventBuffer;
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
/**
 * Manages the lifecycle of multiple client socket connections on the server side.
 * It is responsible for accepting new inbound sockets, matching primary connections with their ping counterparts,
 * spawning dedicated listener and buffering threads for each client, and routing incoming requests to the {@link LobbyController}.
 */
public class SocketClientHandlersManager {

    private final ServerSocket listenSocket;
    private final ServerSocket listenPingSocket;
    private final LobbyController controller;
    private final NetworkLayer networkLayer;
    private final JsonCodec jsonCodec = new JsonCodec();

    private static int Thread_number = 0;

    private final Set<SocketClientHandler> waitingClients;

    /**
     * Adds a newly connected client handler to the waiting room in a thread-safe manner.
     *
     * @param handler the {@link SocketClientHandler} to add.
     */

    // waitingClientsMethods
    public void addWaitingClient(SocketClientHandler handler) {
        synchronized (waitingClients) {
            waitingClients.add(handler);
        }
    }
    /**
     * Removes a client handler from the waiting room in a thread-safe manner.
     *
     * @param handler the {@link SocketClientHandler} to remove.
     */

    public void removeWaitingClient(SocketClientHandler handler) {
        synchronized (waitingClients) {
            waitingClients.remove(handler);
        }
    }
    /**
     * Checks if a client handler is currently in the waiting room in a thread-safe manner.
     *
     * @param handler the {@link SocketClientHandler} to check.
     * @return {@code true} if the client is waiting, {@code false} otherwise.
     */

    public boolean waitingClientsContains(SocketClientHandler handler) {
        synchronized (waitingClients) {
            return waitingClients.contains(handler);
        }
    }
    /**
     * Constructs a new {@code SocketClientHandlersManager}.
     *
     * @param listenSocket     the {@link ServerSocket} used to accept incoming primary data connections.
     * @param listenPingSocket the {@link ServerSocket} used to accept incoming ping connections.
     * @param controller       the primary {@link LobbyController} that dictates the game and server logic.
     * @param networkLayer     the underlying {@link NetworkLayer} used to map users to their handlers and buffers.
     */
    public SocketClientHandlersManager(ServerSocket listenSocket, ServerSocket listenPingSocket, LobbyController controller, NetworkLayer networkLayer) {
        this.listenSocket = listenSocket;
        this.listenPingSocket = listenPingSocket;
        this.waitingClients = new HashSet<>();
        this.networkLayer = networkLayer;
        this.controller = controller;
    }
    /**
     * Starts the main server loop to accept incoming client connections on the primary port.
     * For each connection, it instantiates a handler, places it in the waiting area, and spawns two threads:
     * one for interpreting client requests and another for asynchronously buffering and pushing updates to the client.
     *
     * @throws IOException if the server socket encounters a critical error during acceptance.
     */
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
                    } catch (Exception e) {
                        System.err.println("[Server] Client disconnected: " + e.getMessage());
                        handler.closeConnection();
                    }
                }, "ServerThread-Listener" + Thread_number);
                thread1.start();

                Thread thread2 = new Thread(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            EventBuffer buffer = networkLayer.getBuffer(handler);
                            if (buffer == null) {
                                System.err.println("[Server] Buffer null for client, buffering thread exiting");
                                return;
                            }
                            synchronized (buffer) {
                                if (Thread.currentThread().isInterrupted()) {
                                    return;
                                }
                                while (buffer.isEmpty()
                                       && !Thread.currentThread().isInterrupted()) {
                                    try {
                                        buffer.wait();
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        return;
                                    }
                                }
                                if (!buffer.isEmpty()
                                    && !Thread.currentThread().isInterrupted()) {
                                    List<Event> eventsToSend = buffer.flushBuffer();
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
                System.err.println("[Server] Error setting up client handler: " + e.getMessage());
                try {
                    clientSocket.close();
                } catch (IOException ex) {
                    System.err.println("[Server] Error closing socket: " + ex.getMessage());
                }
            }
        }
    }
    /**
     * Starts the background loop to accept incoming secondary ping connections on the designated ping port.
     * Extracts the requested nickname from the first message to associate this ping socket with an already connected handler,
     * then spawns a dedicated listener thread to maintain the online status of the client.
     *
     * @throws IOException if the ping server socket encounters an error during acceptance.
     */
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
                        System.err.println("[Server] Error closing socket: " + e.getMessage());
                    }
                }, "ServerThreadPing-" + nickname);
                pingThread.start();
                networkLayer.updateClientThreads(handler, pingThread);
                pingServer(nickname);
            }
            catch (Exception e) {
                System.err.println("[Server] Error setting up ping handler: " + e.getMessage());
                try {
                    clientPingSocket.close();
                } catch (IOException ex) {
                    System.err.println("[Server] Error closing ping socket: " + ex.getMessage());
                }
            }
        }
    }
    /**
     * Processes incoming client requests forwarded by individual client handlers.
     * Automatically registers the nickname via the network layer if the client just connected,
     * dispatches the request to the game controller, and handles updating user-to-game mappings.
     *
     * @param request the deserialized {@link ClientRequest} specifying the action to perform.
     * @param sender  the {@link SocketClientHandler} instance representing the client making the request.
     */
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
    /**
     * Notifies the controller that a heartbeat ping was received from a specific user,
     * maintaining their active status on the server.
     *
     * @param nickname the nickname of the user who sent the ping.
     */
    public void pingServer(String nickname){
        controller.handlePingRequest(nickname);
    }
}
