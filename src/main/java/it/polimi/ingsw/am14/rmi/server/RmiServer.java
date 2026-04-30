package it.polimi.ingsw.am14.rmi.server;

import it.polimi.ingsw.am14.NetworkLayer;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;
import it.polimi.ingsw.am14.rmi.client.VirtualServerRmi;

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
        try {
            // TODO: tutto ciò deve essere assegnato ad un ulteriore thread
            networkLayer.updateNicknameMap(nickname, client);

            // New thread in order to let Rmi Server handle multiple clients at the same time for the output flow
            new Thread(() -> {

                long lastPingTime = 0;

                while (true) {
                    synchronized (networkLayer.getBuffer(client)) {
                        while (networkLayer.getBuffer(client).isEmpty()) {
                            // TODO: ping verso il client per capire se è ancora connesso.
//                            handler.update(
//                                    new ServerPing("ping")
//                            );

                            try {
                                networkLayer.getBuffer(client).wait();    // lazy wait
                            } catch (InterruptedException _) {}
                        }
                        List<Event> eventsToSend = networkLayer.getBuffer(client).flushBuffer();
                        for (Event event : eventsToSend) {
                            // TODO: gestione delle RemoteException
                            //==================================================================
                            try {
                                client.update(event);
                            } catch (RemoteException e) {
                                throw new RuntimeException(e.getMessage());
                            }
                            //==================================================================
                        }
//                        if (handler.hasDisconnected())
//                            break;
                    }
                }
            }, "RMIConnect-" + nickname).start();

            lobbyController.connectUser(nickname);
        } catch (Exception e) {
            client.reportError(new ExceptionEvent(e.getMessage()));
        }
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
                // TODO: gestione delle RemoteException
                //======================================================================================================
                try {
                    networkLayer.getClientBy(nickname).reportError(new ExceptionEvent(e.getMessage()));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                //======================================================================================================
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
                // TODO: gestione delle RemoteException
                //======================================================================================================
                try {
                    networkLayer.getClientBy(nickname).reportError(new ExceptionEvent(e.getMessage()));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                //======================================================================================================
            }
        }, "RMIJoin-" + nickname).start();
    }

    @Override
    public void placeTotem(String nickname, char position) throws RemoteException {
        new Thread(() -> {
            try {
                lobbyController.placeTotem(nickname, position);
            } catch (Exception e) {
                // TODO: gestione delle RemoteException
                //======================================================================================================
                try {
                    networkLayer.getClientBy(nickname).reportError(new ExceptionEvent(e.getMessage()));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                //======================================================================================================
            }
        }, "RMIPlace-" + nickname).start();
    }

    @Override
    public void pickCard(String nickname, int cardId, int row) throws RemoteException {
        new Thread(() -> {
            try {
                lobbyController.pickCard(nickname, cardId, row);
            } catch (Exception e) {
                // TODO: gestione delle RemoteException
                //======================================================================================================
                try {
                    networkLayer.getClientBy(nickname).reportError(new ExceptionEvent(e.getMessage()));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                //======================================================================================================
            }
        }, "RMIPick-" + nickname).start();
    }
}
