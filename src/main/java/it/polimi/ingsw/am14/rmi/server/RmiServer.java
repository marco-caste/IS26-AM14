package it.polimi.ingsw.am14.rmi.server;

import it.polimi.ingsw.am14.NetworkLayer;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;
import it.polimi.ingsw.am14.rmi.client.VirtualServerRmi;

import java.util.*;

public class RmiServer implements VirtualServerRmi {
    private final LobbyController lobbyController;
    private final NetworkLayer networkLayer;

    public RmiServer(LobbyController lobbyController, NetworkLayer networkLayer) {
        this.lobbyController = lobbyController;
        this.networkLayer = networkLayer;
    }

    @Override
    public void connect(VirtualClientRmi client, String nickname) throws java.rmi.RemoteException {
        try {
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
                            client.update(event);
                        }
//                        if (handler.hasDisconnected())
//                            break;
                    }
                }
            }).start();

            lobbyController.connectUser(nickname);
        } catch (Exception e) {
            client.reportError(new ExceptionEvent(e.getMessage()));
        }
    }

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
                networkLayer.getClientBy(nickname).reportError(new ExceptionEvent(e.getMessage()));
            }
        }).start();
    }

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
                networkLayer.getClientBy(nickname).reportError(new ExceptionEvent(e.getMessage()));
            }
        }).start();
    }

    @Override
    public void placeTotem(String nickname, char position) {
        new Thread(() -> {
            try {
                lobbyController.placeTotem(nickname, position);
            } catch (Exception e) {
                networkLayer.getClientBy(nickname).reportError(new ExceptionEvent(e.getMessage()));
            }
        }).start();
    }

    @Override
    public void pickCard(String nickname, int cardId, int row) {
        new Thread(() -> {
            try {
                lobbyController.pickCard(nickname, cardId, row);
            } catch (Exception e) {
                networkLayer.getClientBy(nickname).reportError(new ExceptionEvent(e.getMessage()));
            }
        }).start();
    }
}
