package it.polimi.ingsw.am14;

import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.network.rmi.RmiServerLauncher;
import it.polimi.ingsw.am14.network.socket.SocketServerLauncher;

public class ServerLauncher {
//    args[0] = socket port
//    args[1] = rmi port
    public static void main(String[] args) {

        /*
        if (there is save data) {
            prima opzione - ricostruiamo QUI tutti gli oggetti (Game, ecc)
            seconda opzione - ricostruiamo SOLO il LobbyController dal parser, a cui è poi delegata la
                ricostruzione della partita e di tutto il resto.
        }
        else
        */

        // costruzione degli oggetti tecnici
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
