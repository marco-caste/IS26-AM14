package it.polimi.ingsw.am14.network.rmi.server;

import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;
import it.polimi.ingsw.am14.exceptions.IllegalNicknameException;
import it.polimi.ingsw.am14.network.rmi.client.VirtualServerRmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class RmiServer extends UnicastRemoteObject implements VirtualServerRmi {
    private final LobbyController lobbyController;
    private final NetworkLayer networkLayer;


    public RmiServer(LobbyController lobbyController, NetworkLayer networkLayer) throws RemoteException {
        super();
        this.lobbyController = lobbyController;
        this.networkLayer = networkLayer;
    }

    @Override
    public void connect(VirtualClientRmi client, String nickname) throws RemoteException {
        new Thread(() -> {
            try {
                networkLayer.updateNicknameMap(nickname, client);
                networkLayer.updateBufferMap(client);

                // New thread in order to let Rmi Server handle multiple clients at the same time for the output flow
                Thread thread = new Thread(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            synchronized (networkLayer.getBuffer(client)) {
                                // Double-check interruption PRIMA di aspettare
                                if (Thread.currentThread().isInterrupted()) {
                                    return;
                                }
                                
                                while (networkLayer.getBuffer(client).isEmpty() 
                                       && !Thread.currentThread().isInterrupted()) {
                                    try {
                                        networkLayer.getBuffer(client).wait();
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        return;
                                    }
                                }
                                
                                if (!networkLayer.getBuffer(client).isEmpty() 
                                    && !Thread.currentThread().isInterrupted()) {
                                    List<Event> eventsToSend = networkLayer.getBuffer(client).flushBuffer();
                                    for (Event event : eventsToSend) {
                                        try {
                                            client.update(event);
                                        } catch (RemoteException e) {
                                            // Client disconnected during event send - do full cleanup
                                            System.err.println("[RMI Server] Client '" + nickname + "' unreachable during update: " + e.getMessage());
                                            System.err.println("[RMI Server] Performing cleanup for '" + nickname + "'");
                                            cleanupRmiClient(nickname, client);
                                            Thread.currentThread().interrupt();
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("[RMI Server] Error in buffering thread for " + nickname + ": " + e.getMessage());
                    }
                }, "RMIServerBuffer-" + nickname);
                thread.start();

                networkLayer.updateClientThreads(client, thread);
                lobbyController.connectUser(nickname);
            } catch (IllegalNicknameException e) {
                System.err.println("[RMI Server] Connection rejected - nickname '" + nickname + "' already in use");
                try {
                    client.reportError(new ExceptionEvent("Nickname already in use"));
                } catch (RemoteException ex) {
                    System.err.println("[RMI Server] Failed to notify client about duplicate nickname: " + ex.getMessage());
                }
            } catch (Exception e) {
                // connectUser() failed
                System.err.println("[RMI Server] Error during client connection for '" + nickname + "': " + e.getMessage());
                cleanupRmiClient(nickname, client);
            }
        }, "RMIConnect-" + nickname).start();
    }

    @Override
    public void createGame(String nickname, int numPlayers, String totemColor) throws RemoteException {
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

    @Override
    public void joinGame(String nickname, int gameId, String totemColor) throws RemoteException {
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

    @Override
    public void placeTotem(String nickname, char position) throws RemoteException {
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

    @Override
    public void pickCard(String nickname, int cardId, int row) throws RemoteException {
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


    @Override
    public void pingServer(String nickname) throws RemoteException {
        new Thread(() -> {
            lobbyController.handlePingRequest(nickname);
        }, "RMIPingClient-" + nickname).start();
    }

    private void cleanupRmiClient(String nickname, VirtualClientRmi client) {
        try {
            networkLayer.notifyBufferForClient(client);
            networkLayer.removeThreadsByClient(client);
            networkLayer.removeBuffer(client);
            networkLayer.removeClientBy(nickname);
            
            System.err.println("[RMI Server] RMI client '" + nickname + "' cleaned up successfully");
        } catch (Exception e) {
            System.err.println("[RMI Server] Error during RMI client cleanup for '" + nickname + "': " + e.getMessage());
        }
    }
}
