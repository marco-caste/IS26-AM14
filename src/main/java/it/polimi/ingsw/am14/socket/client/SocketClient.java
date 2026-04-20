package it.polimi.ingsw.am14.socket.client;

import it.polimi.ingsw.am14.socket.server.VirtualClientSocket;

import it.polimi.ingsw.am14.events.*;
import it.polimi.ingsw.am14.view.ClientModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class SocketClient {
    private final BufferedReader input;
    private final SocketServerHandler output;
    private final ClientModel model;


    protected SocketClient(BufferedReader input, BufferedWriter output, ClientModel model) {
        this.input = input;
        this.output = new SocketServerHandler(output);
        this.model = model;
    }

    private void run() {
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
            Event event = jsonWriter.writeAsEvent(line);
            event.effect(this.model);
        }
    }

}
