package it.polimi.ingsw.am14.network.rmi.client;

import it.polimi.ingsw.am14.clientModelController.ClientController;
import it.polimi.ingsw.am14.network.VirtualServer;
import it.polimi.ingsw.am14.network.messages.events.Event;
import it.polimi.ingsw.am14.network.messages.events.ExceptionEvent;
import it.polimi.ingsw.am14.network.rmi.server.VirtualClientRmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static it.polimi.ingsw.am14.utils.Constants.*;
/**
 * RMI implementation of the client node.
 * Acts as an intermediary, receiving network calls from the RMI server
 * and dispatching them to the {@link ClientController}, while also exposing methods
 * for the client application to forward actions to the remote server.
 */
public class RmiClient extends UnicastRemoteObject implements VirtualClientRmi, VirtualServer {

    private final ClientController controller;
    private VirtualServerRmi server;

    /**
     * Constructs a new {@code RmiClient}.
     *
     * @param controller the {@link ClientController} responsible for handling client-side game logic.
     * @throws RemoteException if the Remote object cannot be exported.
     */

    public RmiClient(ClientController controller) throws RemoteException {
        super();
        this.controller = controller;
    }

    /**
     * Receives an update event from the server and forwards it to the client controller in a new thread.
     *
     * @param update the {@link Event} object containing the update details.
     */

    @Override
    public void update(Event update) {
        new Thread(() -> {
            controller.update(update);
        }).start();
    }

    /**
     * Receives an error event from the server and forwards it to the client controller in a new thread.
     *
     * @param error the {@link ExceptionEvent} containing the error details.
     */

    @Override
    public void reportError(ExceptionEvent error) {
        new Thread(() -> {
            controller.update(error);
        }).start();
    }

    /**
     * Acknowledges a ping from the server to keep the connection alive.
     * Updates the last ping timestamp in the client controller.
     *
     * @throws RemoteException if a communication-related error occurs.
     */

    @Override
    public void pingClient() throws RemoteException {
        controller.updateLastPing();
    }

    /**
     * Connects to the RMI server using the provided IP address and port.
     *
     * @param nickname  the chosen nickname of the client.
     * @param ipAddress the IP address of the RMI registry.
     * @param port      the port of the RMI registry.
     * @throws RemoteException if a connection error occurs.
     */

    // METHODS EXPOSED TO THE VIEW
    public void connect(String nickname, String ipAddress, int port) throws RemoteException {
        Registry registry = LocateRegistry.getRegistry(ipAddress, port);
        try {
            server = (VirtualServerRmi) registry.lookup(serverName);
            server.connect(this, nickname);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Forwards a request to the server to create a new game.
     *
     * @param nickname   the nickname of the creating player.
     * @param numPlayers the number of players for the requested game.
     * @param totemColor the desired totem color.
     * @throws RemoteException if a communication-related error occurs.
     */
    @Override
    public void createGame(String nickname, int numPlayers, String totemColor) throws RemoteException {
        server.createGame(nickname, numPlayers, totemColor);
    }

    /**
     * Forwards a request to the server to join an existing game.
     *
     * @param nickname   the nickname of the joining player.
     * @param gameId     the ID of the game to join.
     * @param totemColor the desired totem color.
     * @throws RemoteException if a communication-related error occurs.
     */
    @Override
    public void joinGame(String nickname, int gameId, String totemColor) throws RemoteException {
        server.joinGame(nickname, gameId, totemColor);
    }

    /**
     * Forwards a request to the server to place a totem on the starting card.
     *
     * @param nickname the nickname of the player.
     * @param position the chosen position side for the totem.
     * @throws RemoteException if a communication-related error occurs.
     */
    @Override
    public void placeTotem(String nickname, char position) throws RemoteException {
        server.placeTotem(nickname, position);
    }

    /**
     * Forwards a request to the server to pick a card.
     *
     * @param nickname the nickname of the player.
     * @param cardId   the unique identifier of the card to pick.
     * @param row      the index of the row where the card is positioned.
     * @throws RemoteException if a communication-related error occurs.
     */
    @Override
    public void pickCard(String nickname, int cardId, int row) throws RemoteException {
        server.pickCard(nickname, cardId, row);
    }

    /**
     * Starts a background thread that periodically sends ping requests to the server
     * to keep the connection alive.
     *
     * @param nickname the nickname of the client sending the ping.
     * @return the {@link Thread} handling the ping routine.
     */

    @Override
    public Thread pingServer(String nickname){
        Thread thread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(PING_INTERVAL);
                    server.pingServer(nickname);
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }, "RMIClient-Pinger");
        thread.start();
        return thread;
    }
}
