package it.polimi.ingsw.am14;

import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.rmi.RmiServerLauncher;
import it.polimi.ingsw.am14.socket.SocketServerLauncher;

public class ServerLauncher {
//    args[0] = socket port
//    args[1] = rmi port
    public static void main(String[] args) {
        // TODO: eseguirli su più thread?

        NetworkLayer networkLayer = new NetworkLayer();
        LobbyController lobbyController = new LobbyController(networkLayer);
        Object[] technicalObjects = {
                lobbyController,
                networkLayer
        };

        new Thread(() -> {
            SocketServerLauncher.launch(args, technicalObjects);
        },
                "SocketServer-launcher").start();

        new Thread(() -> {
            RmiServerLauncher.launch(args, technicalObjects);
        },
                "RMIServer-launcher").start();
    }
}
