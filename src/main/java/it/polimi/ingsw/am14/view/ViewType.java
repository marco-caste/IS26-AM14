package it.polimi.ingsw.am14.view;

import it.polimi.ingsw.am14.view.viewEvents.ViewEvent;
import it.polimi.ingsw.am14.clientModelController.ClientModel;

import java.io.IOException;

public interface ViewType {
    /**
     * Starts the view and handles user input.
     * @throws IOException if an I/O error occurs
     */
    void run() throws IOException;
    /**
     * Updates the view based on the provided model and event.
     * @param model the client model to update the view with
     * @param event the event that triggered the update
     */
    void update(ClientModel model, ViewEvent event);
    /**
     * Renders a Login message to the user.
     * @param message the message to render
     */
    void renderLogin(String message);
    /**
     * Renders a message indicating that a player has joined the game.
     * @param message the message to render
     */
    void renderPlayerJoin(String message);
    /**
     * Renders a message indicating that the game has started.
     * @param message the message to render
     */
    void renderGameStart(String message);
    /**
     * Renders a message indicating that a player has placed a totem.
     * @param message the message to render
     */
    void renderTotemPlaced(String message);
    /**
     * Renders a message indicating that a player has picked a card.
     * @param message the message to render
     */
    void renderCardPicked(String message);
    /**
     * Renders a message indicating that a player has played a card.
     * @param message the message to render
     */
    void renderEndRound(String message);
    /**
     * Renders a message indicating that the game has ended.
     * @param message the message to render
     */
    void renderEndGame(String message);
    /**
     * Renders a message indicating that a player has disconnected from the game.
     * @param message the message to render
     */
    void renderDisconnection(String message);
    /**
     * Renders a message indicating that a player has reconnected to the game.
     * @param message the message to render
     */
    void renderReconnection(String message);
    /**
     * Renders a message indicating that the game has been restarted.
     * @param message the message to render
     */
    void renderGameRestarted(String message);
    /**
     * Renders an exception message to the user.
     * @param message the exception message to render
     */
    void renderException(String message);
    /**
     * Handles a network error by displaying an error message to the user.
     * @param errorMessage the error message to display
     */
    void handleNetworkError(String errorMessage);
}
