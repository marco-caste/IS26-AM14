package it.polimi.ingsw.am14.network.socket.client;

import it.polimi.ingsw.am14.events.*;
import it.polimi.ingsw.am14.utils.JsonCodec;
import it.polimi.ingsw.am14.clientModel.ClientModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class SocketClient {
    private final BufferedReader input;
    private final SocketServerHandler output;
    private final ClientModel model;
    private final JsonCodec jsonWriter = new JsonCodec();
    private static int Thread_number = 0;


    public SocketClient(BufferedReader input, BufferedWriter output, ClientModel model, SocketServerHandler serverHandler) {
        this.input = input;
        this.output = serverHandler;
        this.model = model;
    }

    public void run() {
        new Thread(() -> {
            try {
                runVirtualServer(); // listener
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "ClientThread-listener" + Thread_number).start();

        Thread_number++;
    }


    private void runVirtualServer() throws IOException {
        String line;

        while ((line = input.readLine()) != null) {
            Event event = jsonWriter.parseEvent(line);
            event.effect(this.model);
        }
    }
}
