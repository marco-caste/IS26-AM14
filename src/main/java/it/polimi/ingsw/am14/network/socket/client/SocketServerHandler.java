package it.polimi.ingsw.am14.network.socket.client;

import it.polimi.ingsw.am14.network.messages.requests.*;
import it.polimi.ingsw.am14.utils.JsonCodec;

import java.io.BufferedWriter;
import java.io.PrintWriter;

import static it.polimi.ingsw.am14.utils.Constants.PING_INTERVAL;
/**
 * Handles outgoing socket communication from the client to the server.
 * Implements {@link VirtualServerSocket} to act as a proxy, serializing client actions
 * into JSON requests and dispatching them through the output streams.
 */
public class SocketServerHandler implements VirtualServerSocket{

    private final PrintWriter output;
    private PrintWriter pingOutput;
    private final JsonCodec jsonWriter = new JsonCodec();
    private final SocketClient client;
    /**
     * Constructs a new {@code SocketServerHandler}.
     *
     * @param output the {@link BufferedWriter} used to send data over the primary socket to the server.
     * @param client the referenced {@link SocketClient} responsible for receiving messages and spawning the ping socket.
     */
    public SocketServerHandler(BufferedWriter output, SocketClient client) {
        this.output = new PrintWriter(output);
        this.client = client;
    }

    /**
     * Sends a request to connect the user to the server using the provided nickname.
     *
     * @param nickname the requested username for the new connection.
     */
    @Override
    public void connectUser(String nickname) {
        ConnectClientRequest request = new ConnectClientRequest(nickname);
        String message = jsonWriter.writeClientRequest(request);
        synchronized (output) {     // tecnicamente non serve, println e flush si sincronizzano su un lock interno
            output.println(message);
            output.flush();
        }
    }
    /**
     * Sends a request to create a new game lobby.
     *
     * @param nickname   the nickname of the player creating the game.
     * @param numPlayers the chosen total number of players for this match.
     * @param totemColor the initial totem color selected by the creator.
     */
    @Override
    public void createGame(String nickname, int numPlayers, String totemColor){
        CreateGameClientRequest request = new CreateGameClientRequest(nickname, numPlayers, totemColor);
        String message = jsonWriter.writeClientRequest(request);
        synchronized (output) {
            output.println(message);
            output.flush();
        }
    }
    /**
     * Sends a request to join an existing game lobby.
     *
     * @param nickname   the nickname of the joining player.
     * @param gameId     the unique identifier of the game to join.
     * @param totemColor the totem color requested by the joining player.
     */

    @Override
    public void joinGame(String nickname, int gameId, String totemColor){
        JoinGameClientRequest request = new JoinGameClientRequest(nickname, totemColor, gameId);
        String message = jsonWriter.writeClientRequest(request);
        synchronized (output) {
            output.println(message);
            output.flush();
        }
    }
    /**
     * Sends a request to place the player's initial totem card.
     *
     * @param nickname the nickname of the player taking the action.
     * @param position a character indicating the position (e.g., side) to place the totem.
     */

    @Override
    public void placeTotem(String nickname, char position){
        PlaceTotemClientRequest request = new PlaceTotemClientRequest(nickname, position);
        String message = jsonWriter.writeClientRequest(request);
        synchronized (output) {
            output.println(message);
            output.flush();
        }
    }
    /**
     * Sends a request to pick a card from the shared pool.
     *
     * @param nickname the nickname of the player picking the card.
     * @param cardId   the unique identifier of the chosen card.
     * @param row      the index of the row corresponding to the card's location in the layout.
     */

    @Override
    public void pickCard(String nickname, int cardId, int row){
        PickCardClientRequest request = new PickCardClientRequest(nickname, cardId, row);
        String message = jsonWriter.writeClientRequest(request);
        synchronized (output) {
            output.println(message);
            output.flush();
        }
    }

    /**
     * Establishes a secondary connection dedicated to network heartbeats and spawns a thread
     * that periodically sends ping requests to keep the server connection alive.
     *
     * @param nickname the nickname of the user asserting connection liveness.
     * @return the background {@link Thread} executing the periodic ping, or {@code null} if the socket could not be set up.
     */

    @Override
    public Thread pingServer(String nickname) {
        BufferedWriter output = client.setPingSocket();
        if (output!=null) {
            pingOutput = new PrintWriter(output);
            Thread thread = new Thread(() -> {
                try {
                    while (true) {
                        Thread.sleep(PING_INTERVAL);

                        PingClientRequest request = new PingClientRequest(nickname);
                        String message = jsonWriter.writeClientRequest(request);
                        synchronized (output) {
                            pingOutput.println(message);
                            pingOutput.flush();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "SocketClient-pinger-" + nickname);
            thread.start();
            return thread;
        }
        return null;
    }
}
