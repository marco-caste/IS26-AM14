package it.polimi.ingsw.am14;

import it.polimi.ingsw.am14.rmi.RmiClientLauncher;
import it.polimi.ingsw.am14.socket.SocketClientLauncher;
import it.polimi.ingsw.am14.view.ClientModel;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.io.PrintWriter;

public class TUIView implements ViewType {

    VirtualServer server;
    String chosenNickname = null;
    Terminal terminal;
    LineReader lineReader;
    PrintWriter writer;

    @Override
    public void run() throws IOException {
        try {

            terminal = TerminalBuilder.builder()
                    .system(true)
                    .build();

            lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .build();

            writer = terminal.writer();

            writer.println("Welcome to MESOS! Type 'help' for commands info.");
            terminal.flush();

            String input;
            while ((input = lineReader.readLine(
                    (chosenNickname == null ? "" : chosenNickname) +
                    "> ")) != null) {
                String[] tokens = input.trim().split("\\s+");

                switch (tokens[0].toLowerCase()) {
                    case "help" -> {
                        writer.println("Available commands:");
                        writer.println("  exit                  - Exit the application");
                        writer.println("  help                  - Show this list");
                        writer.println("  connect [socket|rmi]  - Specify the networking technology to use");
                        terminal.flush();
                    }
                    case "connect" -> {
                        try {
                            connect(tokens);
                        } catch (IllegalArgumentException e) {
                            writer.println(e.getMessage());
                        }
                        terminal.flush();
                    }
                    case "exit" -> {
                        terminal.flush();
                        terminal.close();
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
        writer.println(model.getMessage());
        terminal.flush();

        // TODO: potrebbe essere comodo un buffer in ricezione per evitare race conditions su writer (e quindi terminal)
        // in questo modo, quando arriva un evento, lo aggiungo al buffer e poi lo stampo da lì, così evito di avere più thread che scrivono contemporaneamente su terminal.
        // e evito che venga esposto il prompt "nickname> " in mezzo a messaggi di aggiornamento del modello.
    }

    private void connect(String[] args) throws IllegalArgumentException {
        if (args[1].equalsIgnoreCase("socket")) {
            writer.println("please specify the server IP and port");
            terminal.flush();
            String IP = lineReader.readLine("IP: ");
            String port = lineReader.readLine("Port: ");
            writer.println("Launching Socket Client...");
            terminal.flush();
            try {
                server = SocketClientLauncher.launch(new String[]{IP, port}, this);
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
            server.connectUser(chosenNickname);
            // no need to wait for a response from the server up to this date (29/4),
            // it does not yet send any ConnectionReceived event.
            writer.println("Connected to server successfully!");
            writer.println("Launching TUI...");
            terminal.flush();
        }
        else if (args[1].equalsIgnoreCase("rmi")) {
            writer.println("Launching RMI Client...");
            terminal.flush();
            RmiClientLauncher.launch(new String[]{});
        }
        else
            throw new IllegalArgumentException("Invalid connection type. Please specify 'socket' or 'rmi'.");
    }
}
