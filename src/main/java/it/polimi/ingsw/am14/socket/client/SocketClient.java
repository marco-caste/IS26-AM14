package it.polimi.ingsw.am14.socket.client;

import it.polimi.ingsw.am14.socket.server.VirtualClientSocket;

import it.polimi.ingsw.am14.events.*;
import it.polimi.ingsw.am14.utils.JsonCodec;
import it.polimi.ingsw.am14.view.ClientModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class SocketClient {
    private final BufferedReader input;
    private final SocketServerHandler output;
    private final ClientModel model;
    private final JsonCodec jsonWriter = new JsonCodec();


    public SocketClient(BufferedReader input, BufferedWriter output, ClientModel model) {
        this.input = input;
        this.output = new SocketServerHandler(output);
        this.model = model;
    }

    public void run() {
        new Thread(() -> {
            try {
                runVirtualServer();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }


    private void runVirtualServer() throws IOException {
        String line;

        while ((line = input.readLine()) != null) {
            Event event = jsonWriter.parseEvent(line);
            event.effect(this.model);
        }
    }

}
