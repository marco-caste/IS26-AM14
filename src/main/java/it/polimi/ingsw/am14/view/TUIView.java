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
import org.jline.utils.InfoCmp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.am14.utils.Constants.foodTable;

public class TUIView implements ViewType {

    // TODO: ACCESSI A MODELREFERENCE GENERANO RACE CONDITION
    // non è presente un meccanismo di sincronizzazione tra view e client-model per la renderizzazione.
    private ClientModel modelReference = null;
    private ClientController controller;
    private String chosenNickname = null;
    private Terminal terminal;
    private LineReader lineReader;
    private PrintWriter writer;
    private static Map<Integer, String> cardDescriptions;
    private List<String> gameHistory;

    @Override
    public void run() throws IOException {
        this.controller = new ClientController();
        JsonCodec codec = new JsonCodec();
        cardDescriptions = codec.readCardDescriptions("src/main/resources/CardDescriptions.json");
        gameHistory = new ArrayList<>();

        try {

            terminal = TerminalBuilder.builder()
                    .system(true)
                    .ffm(true)
                    .build();

            lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .build();

            writer = terminal.writer();

            printGameTitle();

            String input;
            while ((input = lineReader.readLine(
                    (chosenNickname == null ? "" : chosenNickname) +
                    "> ")) != null) {
                String[] tokens = input.trim().split("\\s+");

                switch (tokens[0].toLowerCase()) {
                    case "help" -> {
                        lineReader.printAbove(
                                // TODO: COLORI
                                """
                                        Available commands:
                                          exit\t- Exit the application
                                          help\t- Show this list
                                          connect [socket|rmi]\t- Specify the networking technology to use
                                          create [numPlayers] [totemColor]\t- Create a new game
                                          join [gameId] [totemColor]\t- Join an existing game
                                          pick [cardID]\t- Pick the specified card
                                          place [position]\t- Place the totem on the specified position
                                          show [nickname|cardId|board|turnOrderCard|gameHistory] <nickname> | <cardId>\t- Show information about the current game
                                          \tshow nickname <nickname>\t- Show information about the specified player
                                          \tshow cardId <cardId>\t- Show information about the specified card
                                          \tshow board\t- Show the current state of the board
                                          \tshow turnOrderCard\t- Show the current turn order card
                                          \tshow gameHistory\t- Show the history of the latest game
                                        """
                        );
                    }
                    case "connect" -> {
                        try {
                            this.connect(tokens);
                        } catch (IllegalArgumentException e) {
                            lineReader.printAbove(e.getMessage());
                        }
                    }
                    case "exit" -> {
                        terminal.flush();
                        terminal.close();
                    }
                    case "create" -> {
                        try {
                            this.createGame(tokens);
                        } catch (RuntimeException e) {
                            lineReader.printAbove(e.getMessage());
                        }
                    }
                    case "join" -> {
                        try {
                            this.joinGame(tokens);
                        } catch (RuntimeException e) {
                            lineReader.printAbove(e.getMessage());
                        }
                    }
                    case "pick" -> {
                        try {
                            this.pickCard(tokens);
                        } catch (IllegalArgumentException e) {
                            lineReader.printAbove(e.getMessage());
                        }
                    }
                    case "place" -> {
                        try {
                            this.placeTotem(tokens);
                        } catch (IllegalArgumentException e) {
                            lineReader.printAbove(e.getMessage());
                        }
                    }
                    case "show" -> {
                        try {
                            this.show(tokens);
                        } catch (IllegalArgumentException | IllegalStateException e) {
                            lineReader.printAbove(e.getMessage());
                        }
                    }
                    default -> {
                        lineReader.printAbove("Invalid option. Please type 'help' for a list of available commands.");
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

    // TODO: SINCRONIZZARE SU LINEREADER

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
        gameHistory.add(message);
        printBoard();
        lineReader.printAbove("[Server]: " + message);
    }

    @Override
    public void renderTotemPlaced(String message) {
        gameHistory.add(message);
        printBoard();
        lineReader.printAbove("[Server]: " + message);
    }

    @Override
    public void renderCardPicked(String message) {
        gameHistory.add(message);
        printBoard();
        lineReader.printAbove("[Server]: " + message);
    }

    @Override
    public void renderEndRound(String message) {
        gameHistory.add(message);
        printBoard();
        lineReader.printAbove("[Server]: " + message);
    }

    @Override
    public void renderEndGame(String message) {
        gameHistory.add(message);
        lineReader.printAbove("[Server]: " + message);
        StringBuilder scoreBoard = new StringBuilder();
        for (String player : modelReference.getFinalRank()) {
            scoreBoard.append(player).append(": ").append(modelReference.getPlayerByNickname(player).getNumPrestigePoints()).append(" points\n");
        }
        lineReader.printAbove(scoreBoard.toString());
    }

    @Override
    public void renderDisconnection(String message) {
        this.modelReference = null;
        gameHistory.clear();
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.flush();
        lineReader.printAbove("[Server]: " + message);
        lineReader.printAbove("Type join to enter a game or create to start a new one.");
    }

    @Override
    public void renderReconnection(String message) {
        gameHistory.add(message);
        this.chosenNickname = modelReference.getHomePlayerNickname();
        lineReader.printAbove("[Server]: " + message);
    }

    @Override
    public void renderGameRestarted(String message) {
        gameHistory.add(message);
        printBoard();
        lineReader.printAbove("[Server]: " + message);
    }

    @Override
    public void renderException(String message) {
        lineReader.printAbove("[Server]: " + message);
    }

    @Override
    public void handleNetworkError(String errorMessage) {
        lineReader.printAbove("[Network Error]: " + errorMessage + " Returning to main menu...");
        this.chosenNickname = "";
        this.modelReference = null;
        printGameTitle();
    }

    private void show(String[] args) throws IllegalArgumentException, IllegalStateException{
        if (args.length < 2) {
            throw new IllegalArgumentException("Invalid command format. Usage: show [nickname|cardId|turnOrderCard|board|gameHistory] <nickname> | <cardId>");
        }

        if (modelReference == null) {
            throw new IllegalStateException("Please wait for the game to start.");
        }

        switch (args[1].toLowerCase()) {
            case "nickname" -> {
                if (args.length < 3) {
                    throw new IllegalArgumentException("Invalid command format. Usage: show nickname [nickname]");
                }
                lineReader.printAbove(modelReference.getPlayerByNickname(args[2]).toString());
            }
            case "cardid" -> {
                if (args.length < 3) {
                    throw new IllegalArgumentException("Invalid command format. Usage: show cardId [cardId]");
                }
                lineReader.printAbove(cardDescriptions.get(Integer.parseInt(args[2])));
            }
            case "board" -> {
                printBoard();
            }
            case "turnordercard" -> {
                printTurnOrderCard();
            }
            case "gamehistory" -> {
                if (gameHistory.isEmpty()) {
                    lineReader.printAbove("No game history available.");
                } else {
                    StringBuilder historyBuilder = new StringBuilder();
                    for (String event : gameHistory) {
                        historyBuilder.append(event).append("\n");
                    }
                    lineReader.printAbove(historyBuilder.toString());
                }
            }
            default -> throw new IllegalArgumentException("Invalid show option. Available options: nickname, cardId, turnOrderCard, offersPath.");
        }
    }

    private void pickCard(String[] args) throws IllegalArgumentException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Invalid command format. Usage: pick [cardID]");
        }
        try {
            controller.pickCard(Integer.parseInt(args[1]));
        } catch (Exception e) {
            throw new RuntimeException("[Client] A network error occurred while sending your request\nDetails: " + e.getMessage());
        }
        lineReader.printAbove("Card pick request sent successfully!");
    }

    private void placeTotem(String[] args) throws IllegalArgumentException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Invalid command format. Usage: place [position]");
        }
        try {
            controller.placeTotem(Character.toUpperCase(args[1].charAt(0)));
        } catch (Exception e) {
            throw new RuntimeException("[Client] A network error occurred while sending your request\nDetails: " + e.getMessage());
        }
        lineReader.printAbove("Totem placement request sent successfully!");
    }

    private void connect(String[] args) throws IllegalArgumentException {

        if (args.length < 2) {
            throw new IllegalArgumentException("Invalid command format. Usage: connect [socket|rmi]");
        }
        
        // Extract the connection mode from command
        if (!args[1].equalsIgnoreCase("socket") && !args[1].equalsIgnoreCase("rmi")) {
            throw new IllegalArgumentException("Invalid connection mode. Available options: socket, rmi.");
        }

        if (this.controller.isConnected()) {
            throw new IllegalArgumentException("You are already connected to a server.");
        }
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
    }

    private void createGame(String[] args) throws IllegalArgumentException {
        if (args.length < 3) {
            throw new IllegalArgumentException("Invalid command format. Usage: create [numPlayers] [totemColor]");
        }
        try {
            controller.createGame(Integer.parseInt(args[1]), args[2].toLowerCase());
        } catch (Exception e) {
            throw new RuntimeException("[Client] A network error occurred while sending your request\nDetails: " + e.getMessage());
        }
        lineReader.printAbove("Game creation request sent successfully!");
    }

    private void joinGame(String[] args) throws IllegalArgumentException {
        if (args.length < 3) {
            throw new IllegalArgumentException("Invalid command format. Usage: join [gameId] [totemColor]");
        }
        try {
            controller.joinGame(Integer.parseInt(args[1]), args[2].toLowerCase());
        } catch (Exception e) {
            throw new RuntimeException("[Client] A network error occurred while sending your request\nDetails: " + e.getMessage());
        }
        lineReader.printAbove("Game join request sent successfully!");
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

        AttributedStringBuilder attributedTitleBuilder = new AttributedStringBuilder();
        int width = terminal.getWidth();
        String[] lines = asciiArtTitle.split("\n");
        for (String line : lines) {
            AttributedString attributedLine = new AttributedString(
                    line + "\n", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
            // lo spazio rimanente viene diviso a metà per centrare il testo.
            int padding = (width - attributedLine.length()) / 2;
            attributedTitleBuilder.append(" ".repeat(Math.max(0, padding)));
            attributedTitleBuilder.append(attributedLine);
        }
        lineReader.printAbove(attributedTitleBuilder.toAttributedString());
        // TODO: COLORARE DI VERDE PER RENDERE BEN VISIBILE
        lineReader.printAbove("Type 'help' for commands info.");
    }

    private void printBoard() {
        ArrayList<ArrayList<Integer>> currentBoard = modelReference.getBoard();
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
        AttributedStringBuilder turnOrderCardNameBuilder = new AttributedStringBuilder();
        // building the second row:
        line1
                .append("▐▀▀▀▀▀▌ ")
                .append("╔═════╗ ".repeat(offersPath.size()));
        line2
                .append("▐     ▌ ")
                .append("║     ║ ".repeat(offersPath.size()));
        line3.append("▐     ▌ ");
        turnOrderCardNameBuilder.append("▐     ▌ ");
        for (char trackCardName : offersPath) {
            line3.append("║").append(printTrackCard(trackCardName)).append("║ ");
            turnOrderCardNameBuilder.append("║  ").append(trackCardName).append("  ║ ");
        }
        line4
                .append("▐▄▄▄▄▄▌ ")
                .append("╚═════╝ ".repeat(offersPath.size()));
        padding = (width - line1.length()) / 2;
        secondRowBuilder
                .append(" ".repeat(Math.max(0, padding))).append(line1).append(" ".repeat(Math.max(0, padding))).append("\n")
                .append(" ".repeat(Math.max(0, padding))).append(line2).append(" ".repeat(Math.max(0, padding))).append("\n")
                .append(" ".repeat(Math.max(0, padding))).append(line3).append(" ".repeat(Math.max(0, padding))).append("\n")
                .append(" ".repeat(Math.max(0, padding))).append(turnOrderCardNameBuilder).append(" ".repeat(Math.max(0, padding))).append("\n")
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

        +=============+
        |             |
        |             |
        |             |
        |             |
        |             |
        |             |
        |             |
        |             |
        +=============+

         */
    }

    private void printTurnOrderCard() {
        ArrayList<String> turnOrderCard = modelReference.getPlayerOrderInTurnOrderCard();
        int longestNicknameLength = modelReference.getAllPlayers().stream()
                .mapToInt(p -> p.getNickname().length())
                .max()
                .orElse(0);
        int numPlayers = modelReference.getNumPlayers();


        AttributedStringBuilder turnOrderBuilder = new AttributedStringBuilder();
        AttributedString limitLine = new AttributedString(
                "+===" + "=".repeat(longestNicknameLength) + "===+\n",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN)
        );
        AttributedString emptyLine = new AttributedString(
                "|" + " ".repeat(longestNicknameLength + 6) + "|\t",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN)
        );
        turnOrderBuilder.append(limitLine);
        for (int i = 0; i < numPlayers; i++) {
            AttributedString nickname = new AttributedString(
                    turnOrderCard.get(i) == null ? "" : turnOrderCard.get(i),
                    AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN)
            );
            turnOrderBuilder.append(emptyLine);
            turnOrderBuilder.append("\n");
            if (!nickname.isEmpty()) {
                turnOrderBuilder.append("|   ",
                        AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN)
                );
                turnOrderBuilder.append(colorNickname(nickname.toString()));
                turnOrderBuilder.append(" ".repeat(longestNicknameLength - nickname.length()) + "   |\t",
                        AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN)
                );
            }
            else
                turnOrderBuilder.append(emptyLine);
            turnOrderBuilder.append(
                    new AttributedString(
                            "← This player " +
                                    (foodTable.get(numPlayers).get(i) > 0 ?
                                            "gets " + foodTable.get(numPlayers).get(i) + " food" :
                                            "loses " + Math.abs(foodTable.get(numPlayers).get(i)) + " food")
                                    + " at the end of this round.\n",
                            AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE)
                    )
            );
        }
        turnOrderBuilder.append(emptyLine);
        turnOrderBuilder.append("\n");
        turnOrderBuilder.append(limitLine);

        lineReader.printAbove(turnOrderBuilder.toAttributedString());
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

    private AttributedString colorNickname(String chosenNickname) {
        return switch (modelReference.getPlayerByNickname(chosenNickname).getTotemColor()) {
            case "purple" -> new AttributedString(chosenNickname, AttributedStyle.DEFAULT.foreground(147, 68, 97));
            case "yellow" -> new AttributedString(chosenNickname, AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
            case "red" -> new AttributedString(chosenNickname, AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
            case "blue" -> new AttributedString(chosenNickname, AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN));
            default -> new AttributedString(chosenNickname, AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
        };
    }

    private AttributedString printTrackCard(Character trackCardName) {
        return switch(trackCardName) {
            case 'A', 'a' -> new AttributedString(" 1f  ", AttributedStyle.DEFAULT.foreground(237, 153, 107));
            case 'B', 'b' -> new AttributedString(" 1↓  ", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
            case 'C', 'c' -> new AttributedString(" 1↑  ", AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
            case 'D', 'd' -> new AttributedString(" 2↓  ", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
            case 'E', 'e' -> new AttributedStringBuilder().append(new AttributedString("1↓", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)))
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
