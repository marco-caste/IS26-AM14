package it.polimi.ingsw.am14;

import it.polimi.ingsw.am14.rmi.RmiClientLauncher;
import it.polimi.ingsw.am14.rmi.client.RmiClient;
import it.polimi.ingsw.am14.socket.SocketClientLauncher;
import it.polimi.ingsw.am14.socket.client.VirtualServerSocket;
import it.polimi.ingsw.am14.view.ClientModel;
import org.jline.reader.*;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class TUIView implements ViewType {

    VirtualServer server;
    String chosenNickname = null;
    Terminal terminal;
    LineReader lineReader;
    PrintWriter writer;
    private static final String[] colors = {"white", "purple", "yellow", "red", "blue"};

    @Override
    public void run() throws IOException {
        // simplest completer that tabs in the commands;
        // TODO: implementare un completer più avanzato (vedi documentazione JLine)
        Completer stringsCompleter = new StringsCompleter("help", "exit", "connect");

        try {

            terminal = TerminalBuilder.builder()
                    .system(true)
                    .provider("ffm")
                    .build();

            lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(stringsCompleter)
                    .build();

            writer = terminal.writer();

            printGameTitle();
            writer.println("Type 'help' for commands info.");
            terminal.flush();

            String input;
            while ((input = lineReader.readLine(
                    (chosenNickname == null ? "" : chosenNickname) +
                    "> ")) != null) {
                String[] tokens = input.trim().split("\\s+");

                switch (tokens[0].toLowerCase()) {
                    case "help" -> {
                        writer.println("Available commands:");
                        writer.println("  exit                                          - Exit the application");
                        writer.println("  help                                          - Show this list");
                        writer.println("  connect [socket|rmi]                          - Specify the networking technology to use");
                        writer.println("  create [nickname] [numPlayers] [totemColor]   - Create a new game");
                        writer.println("  join [nickname] [gameId] [totemColor]         - Join an existing game");
                        terminal.flush();
                    }
                    case "connect" -> {
                        try {
                            this.connect(tokens);
                        } catch (IllegalArgumentException e) {
                            writer.println(e.getMessage());
                        }
                        terminal.flush();
                    }
                    case "exit" -> {
                        terminal.flush();
                        terminal.close();
                    }
                    case "create" -> {
                        try {
                            this.createGame(tokens);
                        } catch (RuntimeException e) {
                            writer.println(e.getMessage());
                        }
                        terminal.flush();
                    }
                    case "join" -> {
                        try {
                            this.joinGame(tokens);
                        } catch (RuntimeException e) {
                            writer.println(e.getMessage());
                        }
                    }
                    default -> {
                        writer.println("Invalid option. Please type 'help' for a list of available commands.");
                        terminal.flush();
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException("Error initializing terminal: " + e.getMessage());
        } catch (EndOfFileException | UserInterruptException e) {
            throw new RuntimeException("Exiting...");
        }
    }

    @Override
    public void update(ClientModel model) {
        // This method will be called when the model updates. It will display the event message received from the server.
        // it will also display the updated representation of the game state.

        // for now, it simply displays the message received from the server, but in the future it will also display the updated game state.
//        writer.println(model.getMessage());
//        terminal.flush();

        // TODO: potrebbe essere comodo un buffer in ricezione per evitare race conditions su writer (e quindi terminal)
        // in questo modo, quando arriva un evento, lo aggiungo al buffer e poi lo stampo da lì, così evito di avere più thread che scrivono contemporaneamente su terminal.
        // e evito che venga esposto il prompt "nickname> " in mezzo a messaggi di aggiornamento del modello.

        // SOLUZIONE: reader.printAbove(model.getMessage());
        // allows to print a received message above the current prompt, avoiding the race conditions on writer (?)
        lineReader.printAbove("[Server]: " + model.getMessage());
    }

    private void connect(String[] args) throws IllegalArgumentException {
        if (args[1].equalsIgnoreCase("socket")) {
            VirtualServerSocket socketServerEntryPoint;
            writer.println("please specify the server IP and port");
            terminal.flush();
            String IP = lineReader.readLine("IP: ");
            String port = lineReader.readLine("Port: ");
            writer.println("Launching Socket Client...");
            terminal.flush();
            try {
                socketServerEntryPoint = SocketClientLauncher.launch(new String[]{IP, port}, this);
            } catch (IOException e) {
                writer.println("Failed to connect to server: " + e.getMessage());
                terminal.flush();
                return;
            }
            writer.println("Socket Client launched successfully!");
            writer.println("Please enter your nickname");
            terminal.flush();
            chosenNickname = lineReader.readLine("Nickname: ");
            writer.println("Connecting to server...");
            terminal.flush();
            socketServerEntryPoint.connectUser(chosenNickname);
            // no need to wait for a response from the server up to this date (29/4),
            // it does not yet send any ConnectionReceived event.
            writer.println("Connected to server successfully!");
            writer.println("Launching TUI...");
            terminal.flush();
            this.server = socketServerEntryPoint;
        }
        else if (args[1].equalsIgnoreCase("rmi")) {
            RmiClient serverEntryPoint;
            writer.println("Launching RMI Client...");
            terminal.flush();
            try {
                serverEntryPoint = (RmiClient) RmiClientLauncher.launch(this);
            } catch (Exception e) {
                writer.println("Failed to connect to server: " + e.getMessage());
                terminal.flush();
                return;
            }
            writer.println("RMI Client launched successfully!");
            writer.println("Please enter your nickname");
            terminal.flush();
            chosenNickname = lineReader.readLine("Nickname: ");
            String IP = lineReader.readLine("IP: ");
            String port = lineReader.readLine("Port: ");
            writer.println("Connecting to server...");
            terminal.flush();
            try {
                serverEntryPoint.connect(serverEntryPoint, chosenNickname, IP, Integer.parseInt(port));
            } catch (Exception e) {
                writer.println("Failed to connect user: " + e.getMessage());
                terminal.flush();
                return;
            }
            writer.println("Connected to server successfully!");
            writer.println("Launching TUI...");
            terminal.flush();
            this.server = serverEntryPoint;
        }
        else
            throw new IllegalArgumentException("Invalid connection type. Please specify 'socket' or 'rmi'.");
    }

    private void createGame(String[] args) throws IllegalArgumentException {
        if (server == null) {
            throw new IllegalArgumentException("You must connect to a server before creating a game.");
        }
        if (!chosenNickname.equals(args[1])) {
            // avoids coherence issues between the nickname used to connect to the server and the one used to create the game
            // TODO: è possibile che diventi necessario un controllo lato server
            // TODO 2: suspicious: è strano dover specificare il nickname anche nella create, quando già lo si è specificato nella connect.
            throw new IllegalArgumentException("You must use the same nickname as written on the prompt.");
        }
        if (!List.of(colors).contains(args[3].toLowerCase())) {
            // avoids sending an invalid totem color to the server,
            // which would cause the server to reject the game creation request and send an error message back to the client
            // there is a check on server side only for colors already selected by other players (necessarily on there)
            // TL;DR: preemptive check for speed reasons
            throw new IllegalArgumentException("Invalid totem color. Available colors: white, purple, yellow, red, blue.");
        }
        try {
            server.createGame(args[1], Integer.parseInt(args[2]), args[3].toLowerCase());
        } catch (Exception e) {
            throw new RuntimeException("[Client] A network error occurred while sending your request\nDetails: " + e.getMessage());
        }
        writer.println("Game creation request sent successfully!");
        terminal.flush();
    }

    private void joinGame(String[] args) throws IllegalArgumentException {
        if (server == null) {
            throw new IllegalArgumentException("You must connect to a server before joining a game.");
        }
        if (!chosenNickname.equals(args[1])) {
            throw new IllegalArgumentException("You must use the same nickname as written on the prompt.");
        }
        if (!List.of(colors).contains(args[3].toLowerCase())) {
            throw new IllegalArgumentException("Invalid totem color. Available colors: white, purple, yellow, red, blue.");
        }
        try {
            server.joinGame(args[1], Integer.parseInt(args[2]), args[3].toLowerCase());
        } catch (Exception e) {
            throw new RuntimeException("[Client] A network error occurred while sending your request\nDetails: " + e.getMessage());
        }
        writer.println("Game join request sent successfully!");
        terminal.flush();
    }

    private void printGameTitle() {
        String asciiArtTitle =
                "      ___       __   __         ___    ___  __  \n" +
                "|  | |__  |    /  ` /  \\  |\\/| |__      |  /  \\ \n" +
                "|/\\| |___ |___ \\__, \\__/  |  | |___     |  \\__/ \n" +
                "                                                \n" +
                "███╗   ███╗███████╗███████╗ ██████╗ ███████╗\n" +
                "████╗ ████║██╔════╝██╔════╝██╔═══██╗██╔════╝\n" +
                "██╔████╔██║█████╗  ███████╗██║   ██║███████╗\n" +
                "██║╚██╔╝██║██╔══╝  ╚════██║██║   ██║╚════██║\n" +
                "██║ ╚═╝ ██║███████╗███████║╚██████╔╝███████║\n" +
                "╚═╝     ╚═╝╚══════╝╚══════╝ ╚═════╝ ╚══════╝\n" +
                "                                            ";

        int width = terminal.getWidth();
        String[] lines = asciiArtTitle.split("\n");
        for (String line : lines) {
            AttributedString attributedLine = new AttributedString(
                    line, AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
            // lo spazio rimanente viene diviso a metà per centrare il testo.
            int padding = (width - attributedLine.length()) / 2;
            writer.print(" ".repeat(Math.max(0, padding)));
            attributedLine.println(terminal);
        }
        // oss: se avviene un resize della finestra, il titolo non viene centrato
        terminal.flush();
    }
}
