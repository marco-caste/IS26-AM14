package it.polimi.ingsw.am14.launcher;

import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.network.rmi.RmiServerLauncher;
import it.polimi.ingsw.am14.network.socket.SocketServerLauncher;
import it.polimi.ingsw.am14.utils.Codec;
import it.polimi.ingsw.am14.utils.JsonCodec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static it.polimi.ingsw.am14.utils.Constants.PING_INTERVAL;
import static it.polimi.ingsw.am14.utils.Constants.PING_TIMEOUT;

/**
 * The {@code ServerLauncher} class is responsible for starting the server application.
 * It initializes the necessary components, including the network layer and lobby controller,
 * and launches separate threads for handling socket connections, RMI connections, pinging clients,
 * and checking for client timeouts.
 */
public class ServerLauncher {
    /**
     * The method that starts the server. Launches 4 Threads:
     * <ul>
     *     <li>The socket server Thread, on which socket connections will be handled</li>
     *     <li>The RMI server Thread, on which RMI connections will be handled</li>
     *     <li>The ping Thread, which will send a ping to all clients every {@code PING_INTERVAL} milliseconds</li>
     *     <li>The timeout Thread, which will check if any client has not responded to a ping within {@code PING_TIMEOUT} milliseconds</li>
     * </ul>
     *
     *
     * @param args command line arguments, where args[0] is the socket port, args[1] is the RMI port and args[2] is the save file path. The latter must be wrapped in quotes, if it contains spaces.
     */
    public static void startServer(String[] args) throws IOException {

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
            lobbyController = new LobbyController(networkLayer, parameter);
        }


        Object[] technicalObjects = {
                lobbyController,
                networkLayer
        };

        new Thread(() -> SocketServerLauncher.launch(args, technicalObjects),
                "SocketServer-launcher").start();

        new Thread(() -> RmiServerLauncher.launch(args, technicalObjects),
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
