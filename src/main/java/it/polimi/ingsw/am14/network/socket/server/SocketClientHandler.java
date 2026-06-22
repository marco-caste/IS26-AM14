package it.polimi.ingsw.am14.network.socket.server;

import it.polimi.ingsw.am14.network.messages.requests.ClientRequest;
import it.polimi.ingsw.am14.network.messages.events.Event;
import it.polimi.ingsw.am14.network.messages.events.ExceptionEvent;
import it.polimi.ingsw.am14.utils.Codec;
import it.polimi.ingsw.am14.utils.JsonCodec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
/**
 * Handles server-side communication for a single connected socket client.
 * Implements {@link VirtualClientSocket} to deserialize incoming requests and forward them to the server,
 * while serializing server events and errors to be sent back to the client.
 */
public class SocketClientHandler implements VirtualClientSocket{
    private final SocketClientHandlersManager server;
    private final BufferedReader input;
    private final PrintWriter output;
    private final Socket socket;
    private final Codec jsonCodec;
    private BufferedReader pingInput;
    private PrintWriter pingOutput;
    private Socket pingSocket;
    private String nickname;
    /**
     * Constructs a new {@code SocketClientHandler}.
     *
     * @param server the {@link SocketClientHandlersManager} responsible for processing the incoming requests.
     * @param input  the {@link BufferedReader} for reading serialized requests from the client.
     * @param output the {@link PrintWriter} for sending serialized events back to the client.
     * @param socket the primary {@link Socket} connection defining the client session.
     */
    public SocketClientHandler(SocketClientHandlersManager server, BufferedReader input, PrintWriter output, Socket socket) {
        this.server = server;
        this.input = input;
        this.output = output;
        this.socket = socket;
        this.jsonCodec = new JsonCodec();
    }
    /**
     * Starts the main listener loop that reads incoming data from the primary socket,
     * parses the JSON messages into {@link ClientRequest} objects, and forwards them to the server manager.
     *
     * @throws IOException if an error occurs while reading or if the socket is closed.
     */
    public void runVirtualView() throws IOException {
        String line;

        while ((line = input.readLine()) != null) {
            if (socket.isClosed()) {
                throw new IOException("Socket closed while reading");
            }
            
            ClientRequest request = jsonCodec.parseClientRequest(line);
            this.server.receiveRequest(request, this);
        }
    }

    /**
     * Sets up the secondary socket connection specifically used for network heartbeats (pings).
     *
     * @param pingInput  the {@link BufferedReader} for reading ping messages from the client.
     * @param pingOutput the {@link PrintWriter} for sending ping responses back.
     * @param pingSocket the secondary {@link Socket} used for keep-alive validation.
     * @param nickname   the nickname of the currently connected user.
     */
    public void setPingSocket(BufferedReader pingInput, PrintWriter pingOutput, Socket pingSocket, String nickname) {
        this.pingInput = pingInput;
        this.pingOutput = pingOutput;
        this.pingSocket = pingSocket;
        this.nickname = nickname;
    }
    /**
     * Starts the listener loop for the secondary ping socket.
     * Continuously reads incoming ping requests and notifies the server manager to update the client's last seen timestamp.
     *
     * @throws IOException if an error occurs while listening for pings or if the socket drops.
     */
    public void runPingServer() throws IOException {
        //our ping consists in sending the client's name to the server
        while (pingInput.readLine() != null) {
            if (pingSocket.isClosed()) {
                throw new IOException("Socket closed while reading");
            }
            this.server.pingServer(nickname);
        }
    }

    public String getNickname() {
        return nickname;
    }

    /**
     * Serializes an {@link Event} into a JSON string and sends it to the client over the primary socket.
     *
     * @param event the game or lobby event to be dispatched.
     */

    @Override
    public void update(Event event){
        if (!socket.isClosed()) {
            String eventString = jsonCodec.writeEvent(event);
            output.println(eventString);
            output.flush();
        }
    }

    /**
     * Serializes an {@link ExceptionEvent} into a JSON string and sends it to the client to report an error.
     *
     * @param error the exception event detailing the error that occurred on the server.
     */

    @Override
    public void reportError(ExceptionEvent error){
        if (!socket.isClosed()) {
            String errorString = jsonCodec.writeEvent(error);
            output.println(errorString);
            output.flush();
        }
    }
    /**
     * Sends a ping confirmation string to the client using the secondary ping socket,
     * ensuring the client acknowledges the server is still reachable.
     */
    @Override
    public void pingClient() {
        if (pingSocket != null && !pingSocket.isClosed()) {
            pingOutput.println("ping");
            pingOutput.flush();
        }
    }
    /**
     * Gracefully closes all underlying Input/Output streams and active socket connections handling this client.
     * Prevents resource leaks on disconnection.
     */
    public void closeConnection() {
        try {
            input.close();
        } catch (Exception _) {
        }
        try {
            output.close();
        } catch (Exception _) {
        }
        try {
            if (pingInput != null) {
                pingInput.close();
            }
        } catch (Exception _) {
        }
        try {
            if (pingOutput != null) {
                pingOutput.close();
            }
        } catch (Exception _) {
        }
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException _) {
        }
        try {
            if (pingSocket != null && !pingSocket.isClosed()) {
                pingSocket.close();
            }
        } catch (IOException _) {
        }
    }
}
