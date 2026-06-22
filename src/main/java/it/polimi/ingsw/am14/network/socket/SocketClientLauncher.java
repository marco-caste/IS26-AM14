package it.polimi.ingsw.am14.network.socket;

import it.polimi.ingsw.am14.clientModelController.ClientController;
import it.polimi.ingsw.am14.network.socket.client.VirtualServerSocket;
import it.polimi.ingsw.am14.network.socket.client.SocketClient;
import it.polimi.ingsw.am14.network.socket.client.SocketServerHandler;

import java.io.*;
import java.net.Socket;

public class SocketClientLauncher {

    /**
     * Establishes a connection to the server using the specified IP and port, and initializes the client-server communication.
     *
     * @param serverIP the IP address of the server to connect to
     * @param serverPort the port number of the server to connect to
     * @param controller the ClientController instance that will handle client-side logic and interactions
     * @return a VirtualServerSocket instance representing the connected server for socket communication
     * @throws IOException if an I/O error occurs when creating the socket or during communication setup
     */
    public static VirtualServerSocket launch(String serverIP, int serverPort, ClientController controller) throws IOException {

        Socket socket = new Socket(serverIP, serverPort);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        SocketClient client = new SocketClient(in, socket, controller);
        SocketServerHandler serverHandler = new SocketServerHandler(out, client);

        client.run();

        return serverHandler;
    }
}

