package it.polimi.ingsw.am14.network;

import it.polimi.ingsw.am14.network.rmi.RmiClientLauncher;
import it.polimi.ingsw.am14.network.rmi.client.RmiClient;
import it.polimi.ingsw.am14.network.socket.SocketClientLauncher;
import it.polimi.ingsw.am14.network.socket.client.VirtualServerSocket;
import it.polimi.ingsw.am14.view.ViewType;

public abstract class EntryPointFactory {
    /**
     * @param args contains the necessary information to connect to the server, in the following format:
     *             args[0] = type (socket or rmi);
     *             args[1] = ip;
     *             args[2] = port;
     *             args[3] = nickname;
     * @param view the caller view.
     * @return a VirtualServer instance, which can be used to send messages to the server.
     */
    public static VirtualServer connect(String[] args, ViewType view) {
        String mode = args[0];
        switch (mode) {
            case "socket" -> {
                try {
                    VirtualServerSocket client = SocketClientLauncher.launch(new String[]{args[1], args[2], args[3]}, view);
                    client.connectUser(args[3]);
                    return client;
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
            case "rmi" -> {
                try {
                    RmiClient client = RmiClientLauncher.launch(view);
                    client.connect(client, args[3], args[1], Integer.parseInt(args[2]));
                    return client;
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
            default -> throw new IllegalArgumentException("Invalid mode: " + mode);
        }
    }
}
