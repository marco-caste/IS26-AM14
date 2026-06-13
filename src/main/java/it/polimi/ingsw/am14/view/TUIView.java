package it.polimi.ingsw.am14.view;

import it.polimi.ingsw.am14.clientModelController.ClientController;
import it.polimi.ingsw.am14.utils.JsonCodec;
import it.polimi.ingsw.am14.view.viewEvents.ViewEvent;
import it.polimi.ingsw.am14.clientModelController.ClientModel;
import org.jline.reader.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TUIView implements ViewType {

    private ClientModel modelReference = null;
    private ClientController controller;
    private String chosenNickname = null;
    private Terminal terminal;
    private LineReader lineReader;
    private PrintWriter writer;
    private static Map<Integer, String> cardDescriptions;

    @Override
    public void run() throws IOException {
        this.controller = new ClientController();
        JsonCodec codec = new JsonCodec();
        cardDescriptions = codec.readCardDescriptions("src/main/resources/CardDescriptions.json");

        try {

            terminal = TerminalBuilder.builder()
                    .system(true)
                    .ffm(true)
                    .build();

            // TODO: completer funziona strano, "context" sembra non funzionare
            lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(new ContextAwareCompleter())
                    .option(LineReader.Option.AUTO_LIST, true) // Automatically list options
                    .option(LineReader.Option.LIST_PACKED, true) // Display completions in a compact form
                    .option(LineReader.Option.AUTO_MENU, true) // Show menu automatically
                    .option(LineReader.Option.MENU_COMPLETE, true) // Cycle through completions
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
                        writer.println("  exit\t- Exit the application");
                        writer.println("  help\t- Show this list");
                        writer.println("  connect [socket|rmi]\t- Specify the networking technology to use");
                        writer.println("  create [nickname] [numPlayers] [totemColor]\t- Create a new game");
                        writer.println("  join [nickname] [gameId] [totemColor]\t- Join an existing game");
                        writer.println("  pick [nickname] [cardID] [row]\t- Pick the specified card");
                        writer.println("  place [nickname] [position]\t- Place the totem on the specified position");
                        writer.println("  show [nickname|cardId|board|offersPath] <nickname> | <cardId>\t- Show the current game state");
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
    public void update(ClientModel model, ViewEvent event) {

        // if no model has yet been assigned -> assign
        if (model != null && this.modelReference == null) {
            this.modelReference = model;
            this.chosenNickname = model.getHomePlayerNickname();
            if (chosenNickname.equalsIgnoreCase("marcob")) {
                lineReader.printAbove("Porterai minecreft?");
            }
        }
        event.render(this);
    }

    @Override
    public void renderLogin(String message) {
        lineReader.printAbove("[Server]: " + message);
    }

    @Override
    public void renderPlayerJoin(String message) {
        lineReader.printAbove("[Server]: " + message);
    }

    @Override
    public void renderGameStart(String message) {
        lineReader.printAbove("[Server]: " + message);
        printBoard();
    }

    @Override
    public void renderTotemPlaced(String message) {
        lineReader.printAbove("[Server]: " + message);
        printBoard();
    }

    @Override
    public void renderCardPicked(String message) {
        lineReader.printAbove("[Server]: " + message);
        printBoard();
    }

    @Override
    public void renderEndRound(String message) {
        lineReader.printAbove("[Server]: " + message);
        printBoard();
    }

    @Override
    public void renderEndGame(String message) {
        lineReader.printAbove("[Server]: " + message);
        StringBuilder scoreBoard = new StringBuilder();
        for (String player : modelReference.getFinalRank()) {
            scoreBoard.append(player).append(": ").append(modelReference.getPlayerByNickname(player).getNumPrestigePoints()).append(" points\n");
        }
        lineReader.printAbove(scoreBoard.toString());
    }

    @Override
    public void renderDisconnection(String message) {
        lineReader.printAbove("[Server]: " + message);
    }

    @Override
    public void renderReconnection(String message) {
        lineReader.printAbove("[Server]: " + message);
    }

    @Override
    public void renderGameRestarted(String message) {
        // TODO: stampare la board
//        printBoard();
        lineReader.printAbove("[Server]: " + message);
    }

    @Override
    public void renderException(String message) {
        lineReader.printAbove("[Server]: " + message);
    }

    @Override
    public void handleNetworkError(String errorMessage) {
        lineReader.printAbove("[Network Error]: " + errorMessage);
    }

    private void show(String[] args) throws IllegalArgumentException{
        if (args.length < 2) {
            throw new IllegalArgumentException("Invalid command format. Usage: show [nickname|cardId|turnOrderCard|offersPath] <nickname> | <cardId>");
        }

        // TODO: check model != null

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
                if (args.length < 3) {
                    throw new IllegalArgumentException("Invalid command format. Usage: show cardId [cardId]");
                }
                writer.println(cardDescriptions.get(Integer.parseInt(args[2])));
                terminal.flush();
            }
            case "board" -> {
                printBoard();
            }
            case "offersPath" -> {
                writer.println("fetches the offers path from client model");
            }
            default -> throw new IllegalArgumentException("Invalid show option. Available options: nickname, cardId, turnOrderCard, offersPath.");
        }

        /*
        TODO: check di fattibilità:
        use alternate_ca_mode for showing the board constantly updated by the server.
         */
    }

    private void pickCard(String[] args) throws IllegalArgumentException {
        if (args.length < 4) {
            throw new IllegalArgumentException("Invalid command format. Usage: pick [nickname] [cardID] [row]");
        }
        if (!chosenNickname.equals(args[1])) {
            throw new IllegalArgumentException("You must use the same nickname as written on the prompt.");
        }
        try {
            controller.pickCard(Integer.parseInt(args[2]));
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
        if (!chosenNickname.equals(args[1])) {
            throw new IllegalArgumentException("You must use the same nickname as written on the prompt.");
        }
        try {
            // TODO: da rifare
            // TODO: toUpperCase su char
            controller.placeTotem(Character.toUpperCase(args[2].charAt(0)));
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
        
        // Extract the connection mode from command
        String connectionMode = args[1];
        
        // Request connection parameters from user
        writer.println("Please specify the server IP, port and your nickname:");
        terminal.flush();
        
        String serverIP = lineReader.readLine("IP: ");
        String portInput = lineReader.readLine("Port: ");
        String playerNickname = lineReader.readLine("Nickname: ").trim();
        
        // Parse port to integer with validation
        int serverPort;
        try {
            serverPort = Integer.parseInt(portInput);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Port must be a valid integer number, got: " + portInput);
        }

        controller.connect(connectionMode, serverIP, serverPort, playerNickname, this);
        
        writer.println("Client launched successfully!");
        writer.println("Connecting to server...");
        terminal.flush();
    }

    private void createGame(String[] args) throws IllegalArgumentException {
        if (args.length < 4) {
            throw new IllegalArgumentException("Invalid command format. Usage: create [nickname] [numPlayers] [totemColor]");
        }
        try {
            //TODO: DA RIFARE
            controller.createGame(Integer.parseInt(args[2]), args[3].toLowerCase());
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
        try {
            //TODO: da rifare
            controller.joinGame(Integer.parseInt(args[2]), args[3].toLowerCase());
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
        // TODO oss: se avviene un resize della finestra, il titolo non viene centrato
        terminal.flush();
    }

    private void printBoard() {
        ArrayList<ArrayList<Integer>> currentBoard = modelReference.getBoard();
        int turnOrderCardIndex = modelReference.getTurnOrderCardIndex();
        ArrayList<Character> offersPath = modelReference.getOffersPath();

        int[] firstRowIndexes = currentBoard.stream()
                .limit(2)
                .flatMap(ArrayList::stream)
                .mapToInt(Integer::intValue)
                .toArray();
        int[] thirdRowIndexes = currentBoard.stream()
                .skip(2)
                .limit(2)
                .flatMap(ArrayList::stream)
                .mapToInt(Integer::intValue)
                .toArray();
        int width = terminal.getWidth();


        AttributedStringBuilder firstRowBuilder = new AttributedStringBuilder();
        AttributedStringBuilder secondRowBuilder = new AttributedStringBuilder();
        AttributedStringBuilder thirdRowBuilder = new AttributedStringBuilder();

        AttributedStringBuilder
                line1 = new AttributedStringBuilder(),
                line2 = new AttributedStringBuilder(),
                line3 = new AttributedStringBuilder(),
                line4 = new AttributedStringBuilder();

        // building the first row:
        for (int firstRowIndex : firstRowIndexes) {
            line1.append(colorCard(firstRowIndex, "┌─────┐ "));
            line2.append(colorCard(firstRowIndex, "│     │ "));
            line3.append(colorCard(firstRowIndex,
                    "│" + (firstRowIndex < 10 ?
                            String.format("  %d  ", firstRowIndex) :
                            String.format(" %-3d ", firstRowIndex)) +
                    "│ "
                    ));
            line4.append(colorCard(firstRowIndex, "└─────┘ "));
        }
        int padding = (width - line1.length()) / 2;
        firstRowBuilder
                .append(" ".repeat(Math.max(0, padding))).append(line1).append(" ".repeat(Math.max(0, padding))).append("\n")
                .append(" ".repeat(Math.max(0, padding))).append(line2).append(" ".repeat(Math.max(0, padding))).append("\n")
                .append(" ".repeat(Math.max(0, padding))).append(line3).append(" ".repeat(Math.max(0, padding))).append("\n")
                .append(" ".repeat(Math.max(0, padding))).append(line2).append(" ".repeat(Math.max(0, padding))).append("\n")
                .append(" ".repeat(Math.max(0, padding))).append(line4).append(" ".repeat(Math.max(0, padding))).append("\n");

        line1 = new AttributedStringBuilder();
        line2 = new AttributedStringBuilder();
        line3 = new AttributedStringBuilder();
        line4 = new AttributedStringBuilder();
        // building the second row:
        line1
                .append("▐▀▀▀▀▀▌ ")
                .append("╔═════╗ ".repeat(offersPath.size()));
        line2
                .append("▐     ▌ ")
                .append("║     ║ ".repeat(offersPath.size()));
        line3.append("▐").append(String.format("  %d  ", turnOrderCardIndex)).append("▌ ");
        for (char trackCardName : offersPath)
            line3.append("║").append(printTrackCard(trackCardName)).append("║ ");
        line4
                .append("▐▄▄▄▄▄▌ ")
                .append("╚═════╝ ".repeat(offersPath.size()));
        padding = (width - line1.length()) / 2;
        secondRowBuilder
                .append(" ".repeat(Math.max(0, padding))).append(line1).append(" ".repeat(Math.max(0, padding))).append("\n")
                .append(" ".repeat(Math.max(0, padding))).append(line2).append(" ".repeat(Math.max(0, padding))).append("\n")
                .append(" ".repeat(Math.max(0, padding))).append(line3).append(" ".repeat(Math.max(0, padding))).append("\n")
                .append(" ".repeat(Math.max(0, padding))).append(line2).append(" ".repeat(Math.max(0, padding))).append("\n")
                .append(" ".repeat(Math.max(0, padding))).append(line4).append(" ".repeat(Math.max(0, padding))).append("\n");

        line1 = new AttributedStringBuilder();
        line2 = new AttributedStringBuilder();
        line3 = new AttributedStringBuilder();
        line4 = new AttributedStringBuilder();
        // building the third row:
        for (int thirdRowIndex : thirdRowIndexes) {
            line1.append(colorCard(thirdRowIndex, "┌─────┐ "));
            line2.append(colorCard(thirdRowIndex, "│     │ "));
            line3.append(colorCard(thirdRowIndex,
                    "│" + (thirdRowIndex < 10 ?
                            String.format("  %d  ", thirdRowIndex) :
                            String.format(" %-3d ", thirdRowIndex)) +
                            "│ "
            ));
            line4.append(colorCard(thirdRowIndex, "└─────┘ "));
        }
        padding = (width - line1.length()) / 2;
        thirdRowBuilder
                .append(" ".repeat(Math.max(0, padding))).append(line1).append(" ".repeat(Math.max(0, padding))).append("\n")
                .append(" ".repeat(Math.max(0, padding))).append(line2).append(" ".repeat(Math.max(0, padding))).append("\n")
                .append(" ".repeat(Math.max(0, padding))).append(line3).append(" ".repeat(Math.max(0, padding))).append("\n")
                .append(" ".repeat(Math.max(0, padding))).append(line2).append(" ".repeat(Math.max(0, padding))).append("\n")
                .append(" ".repeat(Math.max(0, padding))).append(line4).append(" ".repeat(Math.max(0, padding))).append("\n");

        lineReader.printAbove(firstRowBuilder.toAttributedString());
        lineReader.printAbove(secondRowBuilder.toAttributedString());
        lineReader.printAbove(thirdRowBuilder.toAttributedString());

        /*
        OffersPath:
        ╔═════╗
        ║     ║
        ║     ║
        ║     ║
        ╚═════╝
        TurnOrderCard
        ▐▀▀▀▀▀▌
        ▐     ▌
        ▐     ▌
        ▐     ▌
        ▐▄▄▄▄▄▌
        Card:
        ┌─────┐
        │     │
        │     │
        │     │
        └─────┘

         */
    }

    record CardColors(int lower, int upper, AttributedStyle style) {}

    private static final List<CardColors> COLORS = List.of(
            new CardColors(0, 14,  AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)),
            new CardColors(15, 26,  AttributedStyle.DEFAULT.foreground(60, 42, 94)),
            new CardColors(27, 37,  AttributedStyle.DEFAULT.foreground(255, 140, 0)),
            new CardColors(38, 50,  AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW)),
            new CardColors(51, 70,  AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN)),
            new CardColors(71, 83,  AttributedStyle.DEFAULT.foreground(147, 68, 97)),
            new CardColors(84, 95,  AttributedStyle.DEFAULT.foreground(130, 79, 17)),
            new CardColors(96, 116, AttributedStyle.DEFAULT.foreground(0, 100, 0))
    );

    private AttributedString colorCard(int index, String card) {
        AttributedStyle style = COLORS.stream()
                .filter(t -> index <= t.upper() && index >= t.lower())
                .findFirst()
                .map(CardColors::style)
                .orElse(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));

        return new AttributedString(card, style);
    }

    private AttributedString printTrackCard(Character trackCardName) {
        return switch(trackCardName) {
            case 'A', 'a' -> new AttributedString(" 1f  ", AttributedStyle.DEFAULT.foreground(237, 153, 107));
            case 'B', 'b' -> new AttributedString(" 1↓  ", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
            case 'C', 'c' -> new AttributedString(" 1↑  ", AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
            case 'D', 'd' -> new AttributedString(" 2↓  ", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
            case 'E', 'e' -> new AttributedStringBuilder().append(new AttributedString("2↓", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)))
                    .append("/")
                    .append(new AttributedString("1↑", AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN)))
                    .toAttributedString();
            case 'F', 'f' -> new AttributedString(" 2↑  ", AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
            case 'G', 'g' -> new AttributedStringBuilder().append(new AttributedString("2↑", AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN)))
                    .append("/")
                    .append(new AttributedString("1↓", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)))
                    .toAttributedString();
            default -> new AttributedString("     ", AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
        };
    }
}
