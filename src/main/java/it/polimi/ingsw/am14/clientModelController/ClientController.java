package it.polimi.ingsw.am14.clientModelController;

import it.polimi.ingsw.am14.network.VirtualServer;
import it.polimi.ingsw.am14.network.messages.events.ExceptionEvent;

public class ClientController {
    private final ClientModel model;
    private final VirtualServer server;

    public ClientController(ClientModel model, VirtualServer server) {
        this.model = model;
        this.server = server;
    }

    public void pickCard(int cardId) {
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

        //TODO: State check

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
        //TODO: State check

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

    //TODO: connect?

    public void createGame(int numPlayers, String totemColor) {
        //Check numPlayers
        if (numPlayers < 2 || numPlayers > 5) {
            handleException("Invalid number of players");
            return;
        }

        //Check TotemColor
        if (!totemColor.equalsIgnoreCase("red") && !totemColor.equalsIgnoreCase("blue") &&
            !totemColor.equalsIgnoreCase("green") && !totemColor.equalsIgnoreCase("yellow") &&
            !totemColor.equalsIgnoreCase("purple")) {
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
        //Check TotemColor
        if (!totemColor.equalsIgnoreCase("red") && !totemColor.equalsIgnoreCase("blue") &&
            !totemColor.equalsIgnoreCase("green") && !totemColor.equalsIgnoreCase("yellow") &&
            !totemColor.equalsIgnoreCase("purple")) {
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
