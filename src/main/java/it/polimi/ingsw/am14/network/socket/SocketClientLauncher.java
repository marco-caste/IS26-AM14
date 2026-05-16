package it.polimi.ingsw.am14.network.socket;

import it.polimi.ingsw.am14.view.ViewType;
import it.polimi.ingsw.am14.network.socket.client.SocketClient;
import it.polimi.ingsw.am14.network.socket.client.SocketServerHandler;
import it.polimi.ingsw.am14.clientModel.ClientModel;

import java.io.*;
import java.net.Socket;

import static it.polimi.ingsw.am14.utils.Constants.PING_TIMEOUT;

public class SocketClientLauncher {

    public static SocketServerHandler launch(String[] args, ViewType view) throws IOException {
        String IP = args[0];
        int port = Integer.parseInt(args[1]);

        /*
        TRY WITH RESOURCES:
        try (Socket socket = new Socket(IP, port)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            SocketServerHandler serverHandler = new SocketServerHandler(out);

            ClientModel model = new ClientModel();
            model.registerObserver(view);

            SocketClient client = new SocketClient(in, out, model, serverHandler);

            client.run();

            return serverHandler;
        }
        // catch?

         */

        Socket socket = new Socket(IP, port);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        SocketServerHandler serverHandler = new SocketServerHandler(out);

        ClientModel model = new ClientModel();
        model.registerObserver(view);

        SocketClient client = new SocketClient(in, model, serverHandler, socket);

        client.run();

        return serverHandler;
    }
}

