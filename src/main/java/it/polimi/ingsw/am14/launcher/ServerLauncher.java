package it.polimi.ingsw.am14.launcher;

import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.network.rmi.RmiServerLauncher;
import it.polimi.ingsw.am14.network.socket.SocketServerLauncher;
import it.polimi.ingsw.am14.utils.Codec;
import it.polimi.ingsw.am14.utils.JsonCodec;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static it.polimi.ingsw.am14.utils.Constants.PING_INTERVAL;
import static it.polimi.ingsw.am14.utils.Constants.PING_TIMEOUT;

/**
 * ServerLauncher is the main class that starts the server for the game.
 * It initializes the network layer, lobby controller, and handles saving/loading game state.
 */
public class ServerLauncher {
    // TODO: NEL README SPECIFICARE I PARAMETRI DA RIGA DI COMANDO E COME VANNO IMPOSTATI.
    // COMANDI PER COSTRUIRE I JAR E AVVIARE IL SERVER:
    // .\mvnw package -Pserver
    // java -jar target/AM14-Server.jar par1 par2 par3
    /**
     * The main method that starts the server. Launches 4 Threads:
     * <li>The socket server Thread, on which socket connections will be handled</li>
     * <li>The RMI server Thread, on which RMI connections will be handled</li>
     * <li>The ping Thread, which will send a ping to all clients every {@code PING_INTERVAL} milliseconds</li>
     * <li>The timeout Thread, which will check if any client has not responded to a ping within {@code PING_TIMEOUT} milliseconds</li>
     *
     * @param args command line arguments, where args[0] is the socket port, args[1] is the RMI port and args[2] is the save file path. The latter must be wrapped in quotes, if it contains spaces.
     */
    public static void main(String[] args) throws IOException, URISyntaxException {

        // technical objects building
        NetworkLayer networkLayer = new NetworkLayer();
        LobbyController lobbyController;

        Path savePath = args.length > 2
                ? Path.of(args[2] + "/Saves")
                : Path.of("Saves");

        Files.createDirectories(savePath);
        savePath = savePath.resolve("save.json");

        if (Files.exists(savePath)) {
            Codec jsonCodec = new JsonCodec();
            lobbyController = jsonCodec.readSaveData(savePath.toString());
            lobbyController.setNetworkLayer(networkLayer);
            lobbyController.registerObserversAfterReconnection(networkLayer);
            try {
                Files.delete(savePath);
            } catch (IOException e) {
                throw new RuntimeException("Error while deleting save data: " + e.getMessage());
            }
        } else {
            String parameter = savePath.getParent().toString();
            System.out.println("Given to the lobbycontroller: " + parameter);
            lobbyController = new LobbyController(networkLayer, parameter);
        }


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

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(PING_INTERVAL);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e.getMessage());
                }
                networkLayer.pingAllClients();
            }
        }, "Server-pinger").start();

        LobbyController finalLobbyController = lobbyController;
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(PING_TIMEOUT);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e.getMessage());
                }
                finalLobbyController.handlePingTimeOut();
            }
        },
                "TimeOut-Handler").start();
    }
}
