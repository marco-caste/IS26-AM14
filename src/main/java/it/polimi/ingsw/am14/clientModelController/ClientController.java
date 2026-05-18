package it.polimi.ingsw.am14.clientModelController;

import it.polimi.ingsw.am14.network.EntryPointFactory;
import it.polimi.ingsw.am14.network.VirtualServer;
import it.polimi.ingsw.am14.network.messages.events.ExceptionEvent;
import it.polimi.ingsw.am14.view.ViewType;

import java.util.List;

import static it.polimi.ingsw.am14.utils.Constants.TotemColors;

public class ClientController {
    private ClientModel model;
    private VirtualServer server;

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
                handleNetworkError(e);
                return;
            }
        }

        //cardId check
        if (cardId > 116) {
            handleException("Invalid card ID");
            return;
        }

        //Not event check
        if (cardId <= 95 && cardId >= 84) {
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
            handleNetworkError(e);
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
            handleNetworkError(e);
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
                    view
            );
            this.server = (VirtualServer) result[0];
            this.model = (ClientModel) result[1];
        } catch (Exception e) {
            handleNetworkError(e);
        }
    }

    public void disconnect() {
        if (this.server == null) {
            handleException("You are not connected");
            return;
        }

        this.server = null;
        this.model = null;
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
            handleNetworkError(e);
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
            handleNetworkError(e);
        }
    }

    private void handleException(String description) {
        ExceptionEvent event = new ExceptionEvent(description);
        event.effect(model);
    }

    private void handleNetworkError(Exception e) {
        //TODO: siamo sicuri?
        System.err.println("[Client] A network error occurred while sending your request\nDetails: " + e.getMessage());
    }
}
