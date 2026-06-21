package it.polimi.ingsw.am14.network.rmi.server;

import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.network.messages.events.Event;
import it.polimi.ingsw.am14.network.messages.events.ExceptionEvent;
import it.polimi.ingsw.am14.network.EventBuffer;
import it.polimi.ingsw.am14.exceptions.IllegalNicknameException;
import it.polimi.ingsw.am14.network.rmi.client.VirtualServerRmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
/**
 * RMI server implementation handling connections and inbound requests from remote clients.
 * Forwards respective actions to the {@link LobbyController} and manages dedicated asynchronous threads
 * to safely deliver outbound events to clients via the {@link NetworkLayer}.
 */
public class RmiServer extends UnicastRemoteObject implements VirtualServerRmi {
    private final LobbyController lobbyController;
    private final NetworkLayer networkLayer;

    /**
     * Constructs a new {@code RmiServer}.
     *
     * @param lobbyController the controller handling game and lobby synchronizations.
     * @param networkLayer    the networking layer managing active clients, threads, and buffers.
     * @throws RemoteException if an RMI error prevents the object from being exported.
     */
    public RmiServer(LobbyController lobbyController, NetworkLayer networkLayer) throws RemoteException {
        super();
        this.lobbyController = lobbyController;
        this.networkLayer = networkLayer;
    }

    /**
     * Connects a remote client and initializes a buffer flushing thread to send pending events asynchronously.
     * Also handles connection rejection by notifying the client of duplicate nicknames.
     *
     * @param client   the remote reference to the {@link VirtualClientRmi} representing the user.
     * @param nickname the selected username for the player.
     */

    @Override
    public void connect(VirtualClientRmi client, String nickname) {
        new Thread(() -> {
            try {
                networkLayer.updateNicknameMap(nickname, client);
                networkLayer.updateBufferMap(client);

                // New thread in order to let Rmi Server handle multiple clients at the same time for the output flow
                Thread thread = new Thread(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            EventBuffer buffer = networkLayer.getBuffer(client);
                            if (buffer == null) {
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
                                        try {
                                            client.update(event);
                                        } catch (RemoteException e) {
                                            Thread.currentThread().interrupt();
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception _) {
                    }
                }, "RMIServerBuffer-" + nickname);
                thread.start();

                networkLayer.updateClientThreads(client, thread);
                lobbyController.connectUser(nickname);
                lobbyController.getGameIdByNickname(nickname).ifPresentOrElse(
                        (id) -> networkLayer.updateClients(id, client),
                        () -> { }
                );
            } catch (IllegalNicknameException e) {
                try {
                    client.reportError(new ExceptionEvent("Nickname already in use"));
                } catch (RemoteException _) {
                }
            } catch (Exception _) {
                }
        }, "RMIConnect-" + nickname).start();
    }
    /**
     * Spawns a localized thread to process a new game creation request.
     *
     * @param nickname   the nickname of the user starting the game.
     * @param numPlayers the chosen number of players.
     * @param totemColor the initial assigned totem color.
     */
    @Override
    public void createGame(String nickname, int numPlayers, String totemColor) {
        new Thread(() -> {
            try {
                lobbyController.createGame(nickname, numPlayers, totemColor);
                lobbyController.getGameIdByNickname(nickname).ifPresentOrElse(
                        (id) -> {
                            networkLayer.updateClients(id, networkLayer.getClientBy(nickname));
                        },
                        () -> { }
                );
            } catch (Exception e) {
                try {
                    VirtualClientRmi clientRmi = (VirtualClientRmi) networkLayer.getClientBy(nickname);
                    if (clientRmi != null) {
                        clientRmi.reportError(new ExceptionEvent(e.getMessage()));
                    }
                } catch (RemoteException ex) {
                    System.err.println("[RMI Server] Failed to send error to client " + nickname + ": " + ex.getMessage());
                } catch (Exception ex) {
                    System.err.println("[RMI Server] Unexpected error in createGame for " + nickname + ": " + ex.getMessage());
                }
            }
        }, "RMICreate-" + nickname).start();
    }
    /**
     * Spawns a localized thread to process joining an existing game.
     *
     * @param nickname   the nickname of the joining player.
     * @param gameId     the unique identifier for the targeted game.
     * @param totemColor the newly joined player's requested totem color.
     */
    @Override
    public void joinGame(String nickname, int gameId, String totemColor) {
        new Thread(() -> {
            try {
                lobbyController.joinGame(nickname, gameId, totemColor);
                lobbyController.getGameIdByNickname(nickname).ifPresentOrElse(
                        (id) -> {
                            networkLayer.updateClients(id, networkLayer.getClientBy(nickname));
                        },
                        () -> { }
                );
            } catch (Exception e) {
                try {
                    VirtualClientRmi clientRmi = (VirtualClientRmi) networkLayer.getClientBy(nickname);
                    if (clientRmi != null) {
                        clientRmi.reportError(new ExceptionEvent(e.getMessage()));
                    }
                } catch (RemoteException ex) {
                    System.err.println("[RMI Server] Failed to send error to client " + nickname + ": " + ex.getMessage());
                } catch (Exception ex) {
                    System.err.println("[RMI Server] Unexpected error in joinGame for " + nickname + ": " + ex.getMessage());
                }
            }
        }, "RMIJoin-" + nickname).start();
    }

    /**
     * Spawns a localized thread to handle totem placements.
     *
     * @param nickname the nickname of the active player placing their totem.
     * @param position the symbol representing the specific placement side for the starter card.
     */
    @Override
    public void placeTotem(String nickname, char position) {
        new Thread(() -> {
            try {
                lobbyController.placeTotem(nickname, position);
            } catch (Exception e) {
                try {
                    VirtualClientRmi clientRmi = (VirtualClientRmi) networkLayer.getClientBy(nickname);
                    if (clientRmi != null) {
                        clientRmi.reportError(new ExceptionEvent(e.getMessage()));
                    }
                } catch (RemoteException ex) {
                    System.err.println("[RMI Server] Failed to send error to client " + nickname + ": " + ex.getMessage());
                } catch (Exception ex) {
                    System.err.println("[RMI Server] Unexpected error in placeTotem for " + nickname + ": " + ex.getMessage());
                }
            }
        }, "RMIPlace-" + nickname).start();
    }
    /**
     * Spawns a localized thread to process a pick card action request.
     *
     * @param nickname the nickname of the player taking the action.
     * @param cardId   the identifier of the chosen card to be picked.
     * @param row      an integer representing the row structure index containing the card.
     */
    @Override
    public void pickCard(String nickname, int cardId, int row) {
        new Thread(() -> {
            try {
                lobbyController.pickCard(nickname, cardId, row);
            } catch (Exception e) {
                try {
                    VirtualClientRmi clientRmi = (VirtualClientRmi) networkLayer.getClientBy(nickname);
                    if (clientRmi != null) {
                        clientRmi.reportError(new ExceptionEvent(e.getMessage()));
                    }
                } catch (RemoteException ex) {
                    System.err.println("[RMI Server] Failed to send error to client " + nickname + ": " + ex.getMessage());
                } catch (Exception ex) {
                    System.err.println("[RMI Server] Unexpected error in pickCard for " + nickname + ": " + ex.getMessage());
                }
            }
        }, "RMIPick-" + nickname).start();
    }
    /**
     * Spawns a thread to acknowledge a remote client ping, successfully confirming a live state on the server.
     *
     * @param nickname the nickname of the client sending the ping request.
     * @return null, as the connection timeout loop runs on the server controller asynchronously.
     */
    @Override
    public Thread pingServer(String nickname) {
        new Thread(() -> {
            lobbyController.handlePingRequest(nickname);
        }, "RMIPingClient-" + nickname).start();
        return null;
    }
}
