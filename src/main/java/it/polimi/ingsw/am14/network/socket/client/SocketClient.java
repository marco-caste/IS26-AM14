package it.polimi.ingsw.am14.network.socket.client;

import it.polimi.ingsw.am14.clientModelController.ClientController;
import it.polimi.ingsw.am14.network.messages.events.Event;
import it.polimi.ingsw.am14.utils.JsonCodec;

import java.io.*;
import java.net.Socket;

public class SocketClient {
    private final Socket socket;
    private Socket pingSocket;
    private final BufferedReader input;
    private BufferedReader pingInput;
    private final ClientController controller;
    private final JsonCodec jsonWriter = new JsonCodec();
    private static int Thread_number = 0;


    public SocketClient(BufferedReader input, Socket socket, ClientController controller) {
        this.input = input;
        this.socket = socket;
        this.controller = controller;
    }

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

    private void runPingClient() throws IOException {
        while (pingInput.readLine() != null) {
            controller.updateLastPing();
        }
    }
}
