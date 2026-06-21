package it.polimi.ingsw.am14.launcher;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * The {@code LauncherDispatcher} class is responsible for dispatching the application launch
 * based on the provided command-line arguments. It determines whether to start the client or server
 * mode and invokes the corresponding launcher.
 */
public class LauncherDispatcher {

    /**
     * The main entry point of the application. It parses the command-line arguments to determine
     * the mode (client or server) and invokes the appropriate launcher.
     *
     * @param args command-line arguments where the first argument specifies the mode (client/server)
     *             and subsequent arguments are passed to the respective launcher.
     * @throws IOException        if an I/O error occurs during launching.
     * @throws URISyntaxException if a URI syntax error occurs during launching.
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String mode = args[0].toLowerCase();
        String[] launcherArgs = Arrays.copyOfRange(args, 1, args.length);

        switch (mode) {
            case "client" -> ClientLauncher.startClient(launcherArgs);
            case "server" -> ServerLauncher.startServer(launcherArgs);
            default -> {
                System.err.println("Unknown mode: " + args[0]);
                printUsage();
                System.exit(1);
            }
        }
    }

    private static void printUsage() {
        System.out.println("""
                Usage:
                  java -jar AM14.jar client [client arguments]
                  java -jar AM14.jar server [server arguments]
                """);
    }
}
