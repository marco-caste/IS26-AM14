package it.polimi.ingsw.am14.socket;

import it.polimi.ingsw.am14.socket.client.SocketClient;
import it.polimi.ingsw.am14.view.ClientModel;
import it.polimi.ingsw.am14.view.ClientView;

import java.io.*;
import java.net.Socket;

public class ClientMain {
    public static final String host = "localhost";
    public static final int port = 12345;

    public static void main() {
        try {
            Socket socket = new Socket(host, port);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            ClientModel model = new ClientModel();
            ClientView view = new ClientView();
            model.registerObserver(view);

            SocketClient client = new SocketClient(in, out, model);
            client.run();
        } catch (IOException e) {
            System.err.println("Errore client: " + e.getMessage());
        }
    }
}

