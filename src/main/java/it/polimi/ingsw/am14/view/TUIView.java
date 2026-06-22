package it.polimi.ingsw.am14.view;

import it.polimi.ingsw.am14.clientModelController.ClientController;
import it.polimi.ingsw.am14.utils.Codec;
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

/**
 * The TUIView class implements the ViewType interface and provides a text-based user interface for the game. It manages the terminal input and output, renders game events, and interacts with the client model and controller to facilitate user commands and display game information. The class uses the JLine library to handle terminal interactions and supports various commands for connecting to a server, creating or joining games, picking cards, placing totems, and displaying game information.
 * <h2>Supported Commands:</h2>
 * <ul>
 *     <li><b>help</b> - Displays a comprehensive list of all available commands and their usage</li>
 *     <li><b>connect [socket|rmi]</b> - Establishes a connection to the server using the specified protocol.
 *         Prompts for server IP, port, and player nickname. Validates that the client is not already connected</li>
 *     <li><b>exit</b> - Terminates the application and closes the terminal</li>
 *     <li><b>create [numPlayers] [totemColor]</b> - Creates a new game session with the specified number of players
 *         and assigns the caller's totem color</li>
 *     <li><b>join [gameId] [totemColor]</b> - Joins an existing game identified by gameId and assigns the caller's
 *         totem color</li>
 *     <li><b>pick [cardID]</b> - Selects and plays a card identified by its numeric ID during the game phase</li>
 *     <li><b>place [position]</b> - Places the player's totem at the specified board position (case-insensitive)</li>
 *     <li><b>show [option] [args]</b> - Displays game information based on the specified option:
 *         <ul>
 *             <li>show nickname [nickname] - Displays information about a specific player</li>
 *             <li>show cardId [cardId] - Displays the description of a card by its ID</li>
 *             <li>show board - Displays the current state of the game board with all cards and tracks</li>
 *             <li>show turnOrderCard - Displays the turn order card with player order and food values</li>
 *             <li>show gameHistory - Displays the complete history of events from the current game</li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * <p>
 * Each command is case-insensitive. Invalid commands trigger an error message prompting the user to type 'help'
 * for command information. Command-specific exceptions (IllegalArgumentException, RuntimeException) are caught
 * and displayed to the user without terminating the application loop.
 * </p>
 */
public class TUIView implements ViewType {

    /**
     * Reference to the client model
     */
    private ClientModel modelReference = null;
    /**
     * Reference to the client controller
     */
    private ClientController controller;
    /**
     * The nickname chosen by the player, used for command prompts and display purposes
     */
    private String chosenNickname = "";
    /**
     * The terminal instance for handling input and output in the text-based user interface
     */
    private Terminal terminal;
    /**
     * The line reader for reading user input from the terminal
     */
    private LineReader lineReader;
    /**
     * The print writer for outputting text to the terminal
     */
    private PrintWriter writer;
    /**
     * A map containing card descriptions, where the key is the card ID and the value is the card description string
     */
    private static Map<Integer, String> cardDescriptions;
    /**
     * A list to maintain the history of game events, allowing the user to review past actions and messages during the current game session
     */
    private List<String> gameHistory;
    /**
     * An object used as a lock for synchronizing rendering operations to ensure thread safety when multiple threads may be updating the terminal output concurrently
     */
    private final Object renderLock = new Object();

    /**
     * {@inheritDoc}
     * <p>
     * This method initializes the terminal and line reader, prints the game title, and enters an interactive loop
     * to read and process user commands. The method manages the text-based user interface and delegates command
     * execution to specialized handler methods.
     * </p>
     *
     * @throws IOException If an IOException occurs during terminal initialization or if a critical I/O error
     *                     occurs during input reading
     * @throws RuntimeException If an EndOfFileException or UserInterruptException is caught (e.g., Ctrl+D or Ctrl+C),
     *                          indicating the user wants to exit the application
     */
    @Override
    public void run() throws IOException {
        this.controller = new ClientController();
        Codec codec = new JsonCodec();
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
                    case "help" -> lineReader.printAbove(
                            new AttributedString(
                                """
                                    Available commands:
                                      %-66s - %s
                                      %-66s - %s
                                      %-66s - %s
                                      %-66s - %s
                                      %-66s - %s
                                      %-66s - %s
                                      %-66s - %s
                                      %-66s - %s
                                      %-66s - %s
                                      %-66s - %s
                                      %-66s - %s
                                      %-66s - %s
                                      %-66s - %s
                                    """.formatted(
                                        "exit", "Exit the application",
                                        "help", "Show this list",
                                        "connect [socket|rmi]", "Specify the networking technology to use",
                                        "create [numPlayers] [totemColor]", "Create a new game",
                                        "join [gameId] [totemColor]", "Join an existing game",
                                        "pick [cardID]", "Pick the specified card",
                                        "place [position]", "Place the totem on the specified position",
                                        "show [nickname|cardId|board|TOC|gameHistory] <nickname> | <cardId>",
                                        "Show information about the current game",
                                        "\tshow nickname <nickname>", "Show information about the specified player",
                                        "\tshow cardId <cardId>", "Show information about the specified card",
                                        "\tshow board", "Show the current state of the board",
                                        "\tshow TOC", "Show the current turn order card",
                                        "\tshow gameHistory", "Show the history of the latest game"
                                    ),
                                AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN)
                            )
                    );
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
                    default -> lineReader.printAbove("Invalid option. Please type 'help' for a list of available commands.");
                }
            }
        } catch (IOException e) {
            throw new IOException("Error initializing terminal: " + e.getMessage());
        } catch (EndOfFileException | UserInterruptException e) {
            throw new RuntimeException("Exiting...");
        }
    }

    /**
     * {@inheritDoc}
     *
     * If no model has yet been assigned, it assigns the provided model to the view and sets the chosen nickname to the home player's nickname. Finally, it calls the render method of the event, passing this view as an argument.
     * @param model the client model to update the view with
     * @param event the event that triggered the update
     */
    @Override
    public void update(ClientModel model, ViewEvent event) {

        // if no model has yet been assigned -> assign
        if (model != null && this.modelReference == null) {
            this.modelReference = model;
            this.chosenNickname = model.getHomePlayerNickname();
            if (chosenNickname.equalsIgnoreCase("marcob")) {
                lineReader.printAbove("Porterai minecref?");
            }
        }
        event.render(this);
    }

    /**
     * {@inheritDoc}
     * <p>
     *     This method synchronizes on the renderLock object to ensure thread safety while rendering the login message. It uses the lineReader to print the message above the current input line, prefixed with "[Server]: ".
     * </p>
     * @param message the message to render
     */
    @Override
    public void renderLogin(String message) {
        synchronized (renderLock) {
            lineReader.printAbove("[Server]: " + message);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     *     This method synchronizes on the renderLock object to ensure thread safety while rendering the player join message. It uses the lineReader to print the message above the current input line, prefixed with "[Server]: ".
     * </p>
     * @param message the message to render
     */
    @Override
    public void renderPlayerJoin(String message) {
        synchronized (renderLock) {
            lineReader.printAbove("[Server]: " + message);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     *     This method adds the provided message to the game history, calls the printBoard method to update the board display, and synchronizes on the renderLock object to ensure thread safety while rendering the game start message. It uses the lineReader to print the message above the current input line, prefixed with "[Server]: ".
     * </p>
     * @param message the message to render
     */
    @Override
    public void renderGameStart(String message) {
        gameHistory.add(message);
        printBoard();
        synchronized (renderLock) {
            lineReader.printAbove("[Server]: " + message);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     *     This method adds the provided message to the game history and synchronizes on the renderLock object to ensure thread safety while rendering the totem placement message. It uses the lineReader to print the message above the current input line, prefixed with "[Server]: ".
     * </p>
     * @param message the message to render
     */
    @Override
    public void renderTotemPlaced(String message) {
        gameHistory.add(message);
        printBoard();
        synchronized (renderLock) {
            lineReader.printAbove("[Server]: " + message);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     *     This method adds the provided message to the game history, calls the printBoard method to update the board display, and synchronizes on the renderLock object to ensure thread safety while rendering the card pick message. It uses the lineReader to print the message above the current input line, prefixed with "[Server]: ".
     * </p>
     * @param message the message to render
     */
    @Override
    public void renderCardPicked(String message) {
        if (!canDiscardPickUpdate(message)) {
            gameHistory.add(message);
            printBoard();
            synchronized (renderLock) {
                lineReader.printAbove("[Server]: " + message);
            }
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     *     This method adds the provided message to the game history, calls the printBoard method to update the board display, and synchronizes on the renderLock object to ensure thread safety while rendering the end round message. It uses the lineReader to print the message above the current input line, prefixed with "[Server]: ".
     * </p>
     * @param message the message to render
     */
    @Override
    public void renderEndRound(String message) {
        gameHistory.add(message);
        printBoard();
        synchronized (renderLock) {
            lineReader.printAbove("[Server]: " + message);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     *     This method adds the provided message to the game history, constructs a scoreboard string based on the final player rankings and their prestige points, and synchronizes on the renderLock object to ensure thread safety while rendering the end game message. It uses the lineReader to print the message above the current input line, prefixed with "[Server]: ", followed by the constructed scoreboard.
     * </p>
     * @param message the message to render
     */
    @Override
    public void renderEndGame(String message) {
        gameHistory.add(message);
        AttributedStringBuilder scoreBoard = new AttributedStringBuilder();
        scoreBoard.append(new AttributedString(
                "Final Scoreboard for this game:\n",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)
        ));
        // the ranking is printed in reverse order to show the winner at the top of the scoreboard
        for (int i = modelReference.getFinalRank().size() - 1; i >= 0; i--) {
            String player = modelReference.getFinalRank().get(i);
            scoreBoard.append(new AttributedString(
                    "\t" + player + ": " + modelReference.getPlayerByNickname(player).getNumPrestigePoints() + " points\n",
                    AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE)
            ));
        }
        scoreBoard.append(new AttributedString(
                "The standing global ranking is:\n",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)
        ));
        for (String entry : modelReference.getGlobalRank()) {
            scoreBoard.append(new AttributedString(
                    "\t" + entry + "\n",
                    AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE)
            ));
        }
        synchronized (renderLock) {
            lineReader.printAbove("[Server]: " + message);
            lineReader.printAbove(scoreBoard.toAttributedString());
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     *     This method sets the model reference to null, clears the game history, and synchronizes on the renderLock object to ensure thread safety while rendering the disconnection message. It uses the lineReader to clear the screen and print the disconnection message above the current input line, prefixed with "[Server]: ", followed by instructions for the user to join or create a new game.
     * </p>
     * @param message the message to render
     */
    @Override
    public void renderDisconnection(String message) {
        this.modelReference = null;
        gameHistory.clear();
        synchronized (renderLock) {
            terminal.puts(InfoCmp.Capability.clear_screen);
            terminal.flush();
            lineReader.printAbove("[Server]: " + message);
            lineReader.printAbove("Type join to enter a game or create to start a new one.");
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     *     This method adds the provided message to the game history, sets the chosen nickname to the home player's nickname from the model reference, and synchronizes on the renderLock object to ensure thread safety while rendering the reconnection message. It uses the lineReader to print the message above the current input line, prefixed with "[Server]: ".
     * </p>
     * @param message the message to render
     */
    @Override
    public void renderReconnection(String message) {
        gameHistory.add(message);
        this.chosenNickname = modelReference.getHomePlayerNickname();
        synchronized (renderLock) {
            lineReader.printAbove("[Server]: " + message);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     *     This method adds the provided message to the game history, calls the printBoard method to update the board display, and synchronizes on the renderLock object to ensure thread safety while rendering the game restarted message. It uses the lineReader to print the message above the current input line, prefixed with "[Server]: ".
     * </p>
     * @param message the message to render
     */
    @Override
    public void renderGameRestarted(String message) {
        gameHistory.add(message);
        printBoard();
        synchronized (renderLock) {
            lineReader.printAbove("[Server]: " + message);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     *     This method synchronizes on the renderLock object to ensure thread safety while rendering the exception message. It uses the lineReader to print the message above the current input line, prefixed with "[Server]: ".
     * </p>
     * @param message the exception message to render
     */
    @Override
    public void renderException(String message) {
        synchronized (renderLock) {
            lineReader.printAbove("[Server]: " + message);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     *     This method synchronizes on the renderLock object to ensure thread safety while rendering the network error message. It uses the lineReader to print the message above the current input line, prefixed with "[Network Error]: ", followed by instructions for the user to return to the main menu. It then resets the chosen nickname and model reference, and calls the printGameTitle method to display the game title.
     * </p>
     * @param errorMessage the network error message to render
     */
    @Override
    public void handleNetworkError(String errorMessage) {
        synchronized (renderLock) {
            lineReader.printAbove("[Network Error]: " + errorMessage + " Returning to main menu...");
        }
        this.chosenNickname = "";
        this.modelReference = null;
        printGameTitle();
    }

    private void show(String[] args) throws IllegalArgumentException, IllegalStateException{
        if (args.length < 2) {
            throw new IllegalArgumentException("Invalid command format. Usage: show [nickname|cardId|TOC|board|gameHistory] <nickname> | <cardId>");
        }

        if (modelReference == null) {
            throw new IllegalStateException("Please wait for the game to start.");
        }

        switch (args[1].toLowerCase()) {
            case "nickname" -> {
                if (args.length < 3) {
                    throw new IllegalArgumentException("Invalid command format. Usage: show nickname [nickname]");
                }
                lineReader.printAbove(new AttributedString(
                        modelReference.getPlayerByNickname(args[2]).toString(),
                        AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN)
                ));
            }
            case "cardid" -> {
                if (args.length < 3) {
                    throw new IllegalArgumentException("Invalid command format. Usage: show cardId [cardId]");
                }
                lineReader.printAbove(cardDescriptions.get(Integer.parseInt(args[2])));
            }
            case "board" -> printBoard();
            case "toc" -> printTurnOrderCard();
            case "gamehistory" -> {
                if (gameHistory.isEmpty()) {
                    lineReader.printAbove("No game history available.");
                } else {
                    AttributedStringBuilder historyBuilder = new AttributedStringBuilder();
                    for (String event : gameHistory) {
                        historyBuilder.append(event, AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW)).append("\n");
                    }
                    lineReader.printAbove(historyBuilder.toAttributedString());
                }
            }
            default -> throw new IllegalArgumentException("Invalid show option. Available options: nickname, cardId, TOC, offersPath.");
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
        synchronized (renderLock) {
            lineReader.printAbove(attributedTitleBuilder.toAttributedString());
            lineReader.printAbove(new AttributedString(
                    "Type 'help' for commands info.", AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN)
            ));
        }
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
        line2.append("▐     ▌ ");
        for (char trackCardName : offersPath) {
            line2.append("║  ");
            line2.append(colorTotemInOffersPath(getColorAtPosition(trackCardName)));
            line2.append("  ║ ");
        }
        line3.append("▐ TOC ▌ ");
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

        synchronized (renderLock) {
            lineReader.printAbove(firstRowBuilder.toAttributedString());
            lineReader.printAbove(secondRowBuilder.toAttributedString());
            lineReader.printAbove(thirdRowBuilder.toAttributedString());
        }
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
            String nickInTurnOrder;
            try {
                nickInTurnOrder = turnOrderCard.get(i);
            } catch (IndexOutOfBoundsException _) {
                nickInTurnOrder = "";
            }
            AttributedString nickname = new AttributedString(
                    nickInTurnOrder == null ? "" : nickInTurnOrder,
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

    private record CardColors(int lower, int upper, AttributedStyle style) {}

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

    private AttributedString colorTotemInOffersPath(String totemColor) {
        return totemColor != null ?
                switch (totemColor.toLowerCase()) {
                    case "purple" -> new AttributedString("█", AttributedStyle.DEFAULT.foreground(147, 68, 97));
                    case "yellow" -> new AttributedString("█", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
                    case "red" -> new AttributedString("█", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
                    case "blue" -> new AttributedString("█", AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN));
                    default -> new AttributedString("█", AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
                } :
                new AttributedString(" ", AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
    }

    private String getColorAtPosition(char position) {
        return modelReference.getNicknameByPosition(position) == null ?
                null :
                modelReference.getPlayerByNickname(modelReference.getNicknameByPosition(position)).getTotemColor();
    }

    private boolean canDiscardPickUpdate(String s) {
        return s.equals("Pick phase has started. ");
    }
}
