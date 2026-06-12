package it.polimi.ingsw.am14.clientModelController;

import it.polimi.ingsw.am14.network.EntryPointFactory;
import it.polimi.ingsw.am14.network.VirtualServer;
import it.polimi.ingsw.am14.network.messages.events.Event;
import it.polimi.ingsw.am14.network.messages.events.LoginSuccesfulEvent;
import it.polimi.ingsw.am14.view.ViewType;
import it.polimi.ingsw.am14.view.viewEvents.ExceptionViewEvent;
import it.polimi.ingsw.am14.view.viewEvents.NetworkErrorViewEvent;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.am14.utils.Constants.PING_TIMEOUT;
import static it.polimi.ingsw.am14.utils.Constants.TotemColors;

public class ClientController {
    private ClientModel model;
    private VirtualServer server;
    private long lastPingReceived;
    List<Thread> threads = new ArrayList<>();

    public void pickCard(int cardId) {
        //Model check
        if (server == null) {
            handleException("You are not connected to a server");
            return;
        }

        //State check
        if (!model.checkPickState()) {
            handleException("You can't pick a card right now");
            return;
        }

        //Skip check
        if (cardId == -1) {
            try {
                server.pickCard(model.getHomePlayerNickname(), cardId, 4);
                return;
            } catch (Exception e) {
                handleNetworkError("A network error occurred while skipping the turn");
                return;
            }
        }

        //cardId check
        if (cardId > 116) {
            handleException("Invalid card Id");
            return;
        }

        //Not event check
        if (cardId >= 84 && cardId <= 95) {
            handleException("You can't pick an event card");
            return;
        }

        //Turn check
        if (!model.checkPickTurn()) {
            handleException("It's not your turn to pick a card");
            return;
        }

        //Card check in specific rows
        int row = model.findRowFromCardId(cardId);
        if (!model.checkRow(row)) {
            handleException("You can't pick this card");
            return;
        }

        try {
            server.pickCard(model.getHomePlayerNickname(), cardId, row);
        } catch (Exception e) {
            handleNetworkError("A network error occurred while picking the card");
        }
    }

    public void placeTotem(char position) {
        //Model check
        if (server == null) {
            handleException("You are not connected to a server");
            return;
        }

        //State check
        if (!model.checkPlaceState()) {
            handleException("You can't place a totem right now");
            return;
        }

        //Turn check
        if (!model.checkPlaceTurn()) {
            handleException("It's not your turn to place a totem");
            return;
        }

        //Valid position check
        if (!model.checkTotemPosition(position)) {
            handleException("Invalid position");
            return;
        }

        try {
            server.placeTotem(model.getHomePlayerNickname(), position);
        } catch (Exception e) {
            handleNetworkError("A network error occurred while placing the totem");
        }
    }

    public void connect(String connectionMode, String serverIP, int serverPort, String playerNickname, ViewType view) {
        // Only one connection
        if (this.server != null) {
            handleException("You are already connected");
            return;
        }

        // Validate server port
        if (serverPort <= 0 || serverPort > 65535) {
            handleException("Invalid port number. Port must be between 1 and 65535");
            return;
        }

        try {
            Object[] result = EntryPointFactory.connect(
                    connectionMode,
                    serverIP,
                    serverPort,
                    playerNickname.toLowerCase(),
                    view,
                    this
            );
            this.server = (VirtualServer) result[0];
            this.model = (ClientModel) result[1];
        } catch (Exception e) {
            handleNetworkError("A network error occurred while connecting to the server");
        }
    }

    public void disconnect() {
        if (this.server == null) {
            handleException("You are not connected to a server");
            return;
        }
        this.server = null;
        //this.model = null;
        handleNetworkError("Server is not responding. Try to reconnect later. Shutting down connection...");
    }

    public void createGame(int numPlayers, String totemColor) {
        //Model check
        if (server == null) {
            handleException("You are not connected to a server");
            return;
        }

        //Check if the user is already connected to a game
        if (model.getGameId() >= 0) {
            handleException("You are already connected to a game");
            return;
        }

        //Check numPlayers
        if (numPlayers < 2 || numPlayers > 5) {
            handleException("Invalid number of players");
            return;
        }

        //Check TotemColor
        if (!List.of(TotemColors).contains(totemColor.toLowerCase())) {
            handleException("Invalid totem color");
            return;
        }

        try {
            server.createGame(model.getHomePlayerNickname(), numPlayers, totemColor);
        } catch (Exception e) {
            handleNetworkError("A network error occurred while creating the game");
        }
    }

    public void joinGame(int gameId, String totemColor) {
        //Model check
        if (server == null) {
            handleException("You are not connected to a server");
            return;
        }

        //Check if the user is already connected to a game
        if (model.getGameId() >= 0) {
            handleException("You are already connected to a game");
            return;
        }

        //Check TotemColor
        if (!List.of(TotemColors).contains(totemColor.toLowerCase())) {
            handleException("Invalid totem color");
            return;
        }

        //Check gameId
        if (gameId < 0) {
            handleException("Invalid game ID");
            return;
        }

        try {
            server.joinGame(model.getHomePlayerNickname(), gameId, totemColor);
        } catch (Exception e) {
            handleNetworkError("A network error occurred while joining the game");
        }
    }

    public void updateLastPing() {
        lastPingReceived = System.currentTimeMillis();
    }

    public void checkPing(){
        Thread thread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(PING_TIMEOUT);
                    long now = System.currentTimeMillis();
                    if (now - lastPingReceived > PING_TIMEOUT) {
                        disconnect();
                        threads.forEach(Thread::interrupt);
                        threads = new ArrayList<>();
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Client-PingReceiver");
        thread.start();
    }

    public void addThread(Thread thread) {
        threads.add(thread);
    }

    public void initializePing(String playerNickname) {
        try {
            addThread(server.pingServer(playerNickname));
            updateLastPing();
            checkPing();
        } catch (Exception e) {
            handleNetworkError("A network error occurred while initializing ping");
        }
    }

    public void update(Event event) {
        event.effect(model);
        event.onEventProcessed(this);
    }

    private void handleException(String description) {
        ExceptionViewEvent event = new ExceptionViewEvent(description);
        model.notifyObservers(event);
    }

    private void handleNetworkError(String errorMessage) {
        model.notifyObservers(new NetworkErrorViewEvent(errorMessage));
    }
}
