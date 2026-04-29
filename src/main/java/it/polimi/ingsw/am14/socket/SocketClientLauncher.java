package it.polimi.ingsw.am14.socket;

import it.polimi.ingsw.am14.TUIView;
import it.polimi.ingsw.am14.ViewType;
import it.polimi.ingsw.am14.socket.client.SocketClient;
import it.polimi.ingsw.am14.socket.client.SocketServerHandler;
import it.polimi.ingsw.am14.view.ClientModel;
import it.polimi.ingsw.am14.view.ClientView;

import java.io.*;
import java.net.Socket;

public class SocketClientLauncher {

    public static SocketServerHandler launch(String[] args, ViewType view) throws IOException {
        String IP = args[0];
        int port = Integer.parseInt(args[1]);
        Socket socket = new Socket(IP, port);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        SocketServerHandler serverHandler = new SocketServerHandler(out);

        ClientModel model = new ClientModel();
        model.registerObserver(view);

        SocketClient client = new SocketClient(in, out, model, serverHandler);

        client.run();

        return serverHandler;
    }
}

