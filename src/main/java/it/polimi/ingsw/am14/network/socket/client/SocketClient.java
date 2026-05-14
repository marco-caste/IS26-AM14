package it.polimi.ingsw.am14.network.socket.client;

import it.polimi.ingsw.am14.events.*;
import it.polimi.ingsw.am14.utils.JsonCodec;
import it.polimi.ingsw.am14.clientModel.ClientModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class SocketClient {
    private final Socket socket;
    private final BufferedReader input;
    private final SocketServerHandler output;
    private final ClientModel model;
    private final JsonCodec jsonWriter = new JsonCodec();
    private static int Thread_number = 0;


    public SocketClient(BufferedReader input, BufferedWriter output, ClientModel model, SocketServerHandler serverHandler, Socket socket) {
        this.input = input;
        this.output = serverHandler;
        this.model = model;
        this.socket = socket;
    }

    public void run() {
        new Thread(() -> {
            try {
                runVirtualServer(); // listener
            } catch (SocketTimeoutException e) {
                model.setMessage("[Client] Network error: server is not responding. try to reconnect later.\nShutting down connection...");
                //model.disconnect();
                try {
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }, "ClientThread-listener" + Thread_number).start();

        Thread_number++;
    }


    private void runVirtualServer() throws SocketTimeoutException, IOException {
        String line;

        while ((line = input.readLine()) != null) {
            Event event = jsonWriter.parseEvent(line);
            if (!event.getDescription().equals("ping")) {
                event.effect(this.model);
            }
        }
    }
}
