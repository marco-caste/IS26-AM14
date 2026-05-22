package it.polimi.ingsw.am14.network.socket;

import it.polimi.ingsw.am14.clientModelController.ClientController;
import it.polimi.ingsw.am14.network.socket.client.VirtualServerSocket;
import it.polimi.ingsw.am14.view.ViewType;
import it.polimi.ingsw.am14.network.socket.client.SocketClient;
import it.polimi.ingsw.am14.network.socket.client.SocketServerHandler;
import it.polimi.ingsw.am14.clientModelController.ClientModel;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class SocketClientLauncher {

    /**
     * Launches a Socket client with explicit connection parameters.
     * 
     * @param serverIP the IP address of the server
     * @param serverPort the port number of the server
     * @param view the caller view (UI)
     * @return a Map containing VirtualServerSocket (SocketServerHandler) and ClientModel
     * @throws IOException if connection fails
     */
    public static Map<VirtualServerSocket, ClientModel> launch(String serverIP, int serverPort, ViewType view, ClientController controller) throws IOException {

        Socket socket = new Socket(serverIP, serverPort);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        SocketServerHandler serverHandler = new SocketServerHandler(out);

        ClientModel model = new ClientModel();
        model.registerObserver(view);

        SocketClient client = new SocketClient(in, model, serverHandler, socket, controller);

        client.run();

        return Map.of(serverHandler, model);
    }
}

