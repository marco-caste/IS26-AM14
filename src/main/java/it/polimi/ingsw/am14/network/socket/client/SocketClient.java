package it.polimi.ingsw.am14.network.socket.client;

import it.polimi.ingsw.am14.network.messages.events.Event;
import it.polimi.ingsw.am14.network.messages.events.LoginSuccesfulEvent;
import it.polimi.ingsw.am14.utils.JsonCodec;
import it.polimi.ingsw.am14.clientModelController.ClientModel;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

import static it.polimi.ingsw.am14.utils.Constants.PING_TIMEOUT;

public class SocketClient {
    private final Socket socket;
    private Socket pingSocket;
    private final BufferedReader input;
    private BufferedReader pingInput;
    private final SocketServerHandler handler;
    private final ClientModel model;
    private final JsonCodec jsonWriter = new JsonCodec();
    private static int Thread_number = 0;


    public SocketClient(BufferedReader input, ClientModel model, SocketServerHandler serverHandler, Socket socket) {
        this.input = input;
        this.handler = serverHandler;
        this.model = model;
        this.socket = socket;
    }

    public void run() {
        new Thread(() -> {
            try {
                runVirtualServer(); // listener
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }, "ClientThread-listener" + Thread_number).start();

        Thread_number++;
    }


    private void runVirtualServer() throws IOException {
        String line;

        try {
            while ((line = input.readLine()) != null) {
                Event event = jsonWriter.parseEvent(line);
                event.effect(this.model);

                try {
                    LoginSuccesfulEvent loginEvent = (LoginSuccesfulEvent) event;
                    pingSocket = new Socket(socket.getInetAddress(), socket.getPort()+1);
                    pingSocket.setSoTimeout(PING_TIMEOUT);
                    pingInput = new BufferedReader(new InputStreamReader(pingSocket.getInputStream()));
                    handler.setPingOutput(new BufferedWriter(new OutputStreamWriter(pingSocket.getOutputStream())));
                    loginEvent.startPinging(handler);
                } catch (ClassCastException e) {
                    // not a LoginSuccesfulEvent, do nothing
                }

                new Thread(() -> {
                    try {
                        runPingClient();
                    } catch (SocketTimeoutException e) {
                        model.setMessage("[Client] Network error: server is not responding. try to reconnect later.\n[Client] Shutting down connection...");
                        model.disconnect();
                        try {
                            socket.close();
                            pingSocket.close();
                            // TODO: ClientThread-pingListener.interrupt() + ClientThread-listener.interrupt()?
                            // intuizione: se il client percepisce disconnessione, interrompe i thread in lettura (ping + listener)
                        } catch (IOException ex) {
                            model.setMessage("[Client] Network error: failed to close the connection. try to reconnect later.");
                            model.notifyObservers("");
                        }
                    } catch (IOException e) {
                        model.setMessage("[Client] Network error: failed to read from the server. try to reconnect later.\n[Client] Shutting down connection...");
                        model.disconnect();
                    }
                }, "ClientThread-pingListener-" + Thread_number).start();
            }
        } catch (IOException e) {
            // do nothing: thread pinger will handle disconnection and notify the client.
        }
    }

    private void runPingClient() throws IOException {
        while (pingInput.readLine() != null) {
            // do nothing, just wait for the timeout
        }
    }
}
