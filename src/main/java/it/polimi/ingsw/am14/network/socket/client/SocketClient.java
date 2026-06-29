package it.polimi.ingsw.am14.network.socket.client;

import it.polimi.ingsw.am14.clientModelController.ClientController;
import it.polimi.ingsw.am14.network.messages.events.Event;
import it.polimi.ingsw.am14.utils.Codec;
import it.polimi.ingsw.am14.utils.JsonCodec;

import java.io.*;
import java.net.Socket;
/**
 * Socket client implementation that listens for incoming messages and pings from the server.
 * It manages dedicated threads to asynchronously process main game events and connection heartbeats,
 * forwarding them to the {@link ClientController}.
 */
public class SocketClient {
    private final Socket socket;
    private Socket pingSocket;
    private final BufferedReader input;
    private BufferedReader pingInput;
    private final ClientController controller;
    private final Codec jsonWriter;
    private static int Thread_number = 0;

    /**
     * Constructs a new {@code SocketClient}.
     *
     * @param input      the {@link BufferedReader} used to read incoming messages from the server.
     * @param socket     the primary {@link Socket} connection to the server.
     * @param controller the {@link ClientController} responsible for handling client-side game logic and updates.
     */
    public SocketClient(BufferedReader input, Socket socket, ClientController controller) {
        this.input = input;
        this.socket = socket;
        this.controller = controller;
        this.jsonWriter = new JsonCodec();
    }

    /**
     * Starts the main listener thread for the client.
     * It continuously reads incoming messages from the server and handles them asynchronously.
     */
    public void run() {
        Thread_number++;
        Thread thread = new Thread(() -> {
            try {
                runVirtualServer(); // listener
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }, "ClientThread-listener" + Thread_number);
        thread.start();
        controller.addThread(thread);
    }

    /**
     * Internal listener loop that reads serialized strings from the input stream,
     * parses them into {@link Event} objects using the JSON codec, and forwards them to the controller.
     *
     * @throws IOException if an error occurs while reading from the stream or if the connection drops.
     */
    private void runVirtualServer() throws IOException {
        String line;
        try {
            while ((line = input.readLine()) != null) {
                Event event = jsonWriter.parseEvent(line);
                controller.update(event);
            }
        } catch (IOException e) {
            socket.close();
        }
    }

    /**
     * Establishes a secondary socket connection on the server's adjacent port specifically for ping messages.
     * Starts a dedicated listener thread to process incoming pings and keep the connection alive.
     *
     * @return a {@link BufferedWriter} associated with the ping socket's output stream, or {@code null} if the connection fails.
     */
    public BufferedWriter setPingSocket() {
        try {
            pingSocket = new Socket(socket.getInetAddress(), socket.getPort()+1);
            pingInput = new BufferedReader(new InputStreamReader(pingSocket.getInputStream()));
            Thread thread = new Thread(() -> {
                try {
                    runPingClient();
                } catch (IOException e) {
                    try {
                        pingSocket.close();
                    } catch (IOException _) {
                    }
                }
            }, "ClientThread-pingListener-" + Thread_number);
            thread.start();
            controller.addThread(thread);
            return new BufferedWriter(new OutputStreamWriter(pingSocket.getOutputStream()));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Internal listener loop for the secondary ping socket.
     * Continuously reads incoming ping requests and notifies the controller to update the last seen timestamp.
     *
     * @throws IOException if an error occurs while reading from the ping stream.
     */
    private void runPingClient() throws IOException {
        while (pingInput.readLine() != null) {
            controller.updateLastPing();
        }
    }

    public void close() {
        try {
            socket.close();
            if (pingSocket != null) {
                pingSocket.close();
            }
        } catch (IOException _) {
        }
    }
}
