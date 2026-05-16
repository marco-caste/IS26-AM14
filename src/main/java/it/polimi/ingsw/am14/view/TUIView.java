package it.polimi.ingsw.am14.view;

import it.polimi.ingsw.am14.network.EntryPointFactory;
import it.polimi.ingsw.am14.network.VirtualServer;
import it.polimi.ingsw.am14.clientModelController.ClientModel;
import org.jline.reader.*;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static it.polimi.ingsw.am14.utils.Constants.TotemColors;

public class TUIView implements ViewType {

    private ClientModel modelReference = null;
    private VirtualServer server;
    private String chosenNickname = null;
    private Terminal terminal;
    private LineReader lineReader;
    private PrintWriter writer;

    @Override
    public void run() throws IOException {
        // simplest completer that tabs in the commands;
        // TODO: implementare un completer più avanzato (vedi documentazione JLine)
        Completer stringsCompleter = new StringsCompleter("help", "exit", "connect");

        try {

            terminal = TerminalBuilder.builder()
                    .system(true)
                    .ffm(true)
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
                        writer.println("  exit\n\n\t- Exit the application");
                        writer.println("  help\n\n\t- Show this list");
                        writer.println("  connect [socket|rmi]\n\n\t- Specify the networking technology to use");
                        writer.println("  create [nickname] [numPlayers] [totemColor]\n\n\t- Create a new game");
                        writer.println("  join [nickname] [gameId] [totemColor]\n\n\t- Join an existing game");
                        writer.println("  pick [nickname] [cardID] [row]\n\n\t- Pick the specified card");
                        writer.println("  place [nickname] [position]\n\n\t- Place the totem on the specified position");
                        writer.println("  show [nickname|cardId|turnOrderCard|offersPath] <nickname> | <cardId row>\n\n\t- Show the current game state");
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
                            terminal.flush();
                        }
                    }
                    case "join" -> {
                        try {
                            this.joinGame(tokens);
                        } catch (RuntimeException e) {
                            writer.println(e.getMessage());
                            terminal.flush();
                        }
                    }
                    case "pick" -> {
                        try {
                            this.pickCard(tokens);
                        } catch (IllegalArgumentException e) {
                            writer.println(e.getMessage());
                            terminal.flush();
                        }
                    }
                    case "place" -> {
                        try {
                            this.placeTotem(tokens);
                        } catch (IllegalArgumentException e) {
                            writer.println(e.getMessage());
                            terminal.flush();
                        }
                    }
                    case "show" -> {
                        try {
                            this.show(tokens);
                        } catch (IllegalArgumentException e) {
                            writer.println(e.getMessage());
                            terminal.flush();
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
    public void update(ClientModel model, String eventChoice) {

        if (model != null && this.modelReference == null) {
            this.modelReference = model;
            this.chosenNickname = model.getHomePlayerNickname();
            if (chosenNickname.equalsIgnoreCase("marcob")) {
                lineReader.printAbove("Porterai minecreft?");
                return;
            }
            try {
                // starts sending pings to the server to keep the connection alive and detect disconnections
                this.server.pingServer(chosenNickname);
            } catch (Exception e) {
                lineReader.printAbove("[Client] A network error occurred while sending your request\nDetails: " + e.getMessage());
                return;
            }
        }
        // SOLUZIONE: reader.printAbove(model.getDescription());
        // allows to print a received message above the current prompt, avoiding the race conditions on writer (?)
        lineReader.printAbove("[Server]: " + this.modelReference.getMessage() + "\nBoard:\n" + this.modelReference.getBoard().toString());
    }

    @Override
    public void disconnect() {
        server = null;
        // TODO: thread.interrupt sul pinger.
        this.update(null, "");
    }

    private void show(String[] args) throws IllegalArgumentException{
        if (args.length < 2) {
            throw new IllegalArgumentException("Invalid command format. Usage: show [nickname|cardId|turnOrderCard|offersPath] <nickname> | <cardId row>");
        }

//        template: show [nickname|cardId|turnOrderCard|offersPath] <nickname> | <cardId row>

        switch (args[1]) {
            case "nickname" -> {
                if (args.length < 3) {
                    throw new IllegalArgumentException("Invalid command format. Usage: show nickname [nickname]");
                }
                writer.println(modelReference.getPlayerByNickname(args[2]).toString());
                terminal.flush();
            }
            case "cardId" -> {
                if (args.length < 4) {
                    throw new IllegalArgumentException("Invalid command format. Usage: show cardId [cardId] [row]");
                }
                writer.println("fetches a card description from a json file");
            }
            case "turnOrderCard" -> {
                writer.println("fetches the turnOrderCard from client model");
            }
            case "offersPath" -> {
                writer.println("fetches the offers path from client model");
            }
            default -> throw new IllegalArgumentException("Invalid show option. Available options: nickname, cardId, turnOrderCard, offersPath.");
        }

        /*


        use a alternate_ca_mode for showing:
        - on the left, the 4 rows in the board filled with card ids
        - on the right, the details queried by user
            - stats of a player
            - details of a card
            - details of the offers path
            - the turn order card
         */
    }

    private void pickCard(String[] args) throws IllegalArgumentException {
        if (args.length < 4) {
            throw new IllegalArgumentException("Invalid command format. Usage: pick [nickname] [cardID] [row]");
        }
        if (server == null) {
            throw new IllegalArgumentException("You must connect to a server before picking a card.");
        }
        if (!chosenNickname.equals(args[1])) {
            throw new IllegalArgumentException("You must use the same nickname as written on the prompt.");
        }
        try {
            server.pickCard(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
        } catch (Exception e) {
            throw new RuntimeException("[Client] A network error occurred while sending your request\nDetails: " + e.getMessage());
        }
        writer.println("Card pick request sent successfully!");
        terminal.flush();
    }

    private void placeTotem(String[] args) throws IllegalArgumentException {
        if (args.length < 3) {
            throw new IllegalArgumentException("Invalid command format. Usage: place [nickname] [position]");
        }
        if (server == null) {
            throw new IllegalArgumentException("You must connect to a server before placing the totem.");
        }
        if (!chosenNickname.equals(args[1])) {
            throw new IllegalArgumentException("You must use the same nickname as written on the prompt.");
        }
        try {
            server.placeTotem(args[1], args[2].charAt(0));
        } catch (Exception e) {
            throw new RuntimeException("[Client] A network error occurred while sending your request\nDetails: " + e.getMessage());
        }
        writer.println("Totem placement request sent successfully!");
        terminal.flush();
    }

    // TODO: RIVEDERE PROMPT
    private void connect(String[] args) throws IllegalArgumentException {

        if (args.length < 2) {
            throw new IllegalArgumentException("Invalid command format. Usage: connect [socket|rmi]");
        }
        writer.println("please specify the server IP, port and your nickname");
        terminal.flush();
        String IP = lineReader.readLine("IP: ");
        String port = lineReader.readLine("Port: ");
        String nick = lineReader.readLine("Nickname: ").trim();
        this.server = EntryPointFactory.connect(new String[]{args[1], IP, port, nick}, this);
        writer.println("Client launched successfully!");
        writer.println("Connecting to server...");
        terminal.flush();
        // no need to wait for a response from the server up to this date (29/4),
        // it does not yet send any LoginSuccessful event.
        writer.println("Connected to server successfully!");
        writer.println("Launching TUI...");
        terminal.flush();
    }

    private void createGame(String[] args) throws IllegalArgumentException {
        if (args.length < 4) {
            throw new IllegalArgumentException("Invalid command format. Usage: create [nickname] [numPlayers] [totemColor]");
        }
        if (server == null) {
            throw new IllegalArgumentException("You must connect to a server before creating a game.");
        }
        if (!chosenNickname.equals(args[1])) {
            // avoids coherence issues between the nickname used to connect to the server and the one used to create the game
            // TODO: è possibile che diventi necessario un controllo lato server
            // TODO 2: suspicious: è strano dover specificare il nickname anche nella create, quando già lo si è specificato nella connect.
            throw new IllegalArgumentException("You must use the same nickname as written on the prompt.");
        }
        if (!List.of(TotemColors).contains(args[3].toLowerCase())) {
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
        if (args.length < 4) {
            throw new IllegalArgumentException("Invalid command format. Usage: join [nickname] [gameId] [totemColor]");
        }
        if (server == null) {
            throw new IllegalArgumentException("You must connect to a server before joining a game.");
        }
        if (!chosenNickname.equals(args[1])) {
            throw new IllegalArgumentException("You must use the same nickname as written on the prompt.");
        }
        if (!List.of(TotemColors).contains(args[3].toLowerCase())) {
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

    private void printBoard() {

    }
}
