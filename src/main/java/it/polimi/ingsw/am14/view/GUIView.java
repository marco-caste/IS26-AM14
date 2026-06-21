package it.polimi.ingsw.am14.view;

import it.polimi.ingsw.am14.clientModelController.ClientController;
import it.polimi.ingsw.am14.clientModelController.ClientModel;
import it.polimi.ingsw.am14.clientModelController.ClientPlayer;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import it.polimi.ingsw.am14.view.viewEvents.ViewEvent;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

public class GUIView extends Application implements ViewType {

    private ClientModel modelReference;
    private ClientController controllerReference;
    private Stage primaryStage;

    private Scene loginScene;

    private Scene gameChoiceScene;
    private Scene lobbyScene;
    private Scene gameScene;

    private VBox loginBox;
    private Label gameChoiceErrorLabel;
    private Label loginErororLabel;
    private Label lobbyNumPlayerLabel;

    // Top game scene
    private Label serverMessage;
    private Label roundCounter;
    private Integer round;
    private StackPane topPane;

    // Center game scene
    private HBox topCenterBox;
    private HBox bottomCenterBox;
    private HBox offersPathBox;

    // End-game ranking
    private VBox endGameRankingBox;
    private ToggleButton rankingToggle;
    private VBox localRankingBox;
    private VBox globalRankingBox;

    private static final double BOARD_CARD_WIDTH = 110;
    private static final double BOARD_CARD_HEIGHT = 165;
    private static final double SMALL_TOTEM_WIDTH = 46;
    private static final double SMALL_TOTEM_HEIGHT = 56;
    private static final double END_TOTEM_WIDTH = 230;
    private static final double LOBBY_TOTEM_WIDTH = 300;
    private static final double END_TOTEM_BOX_WIDTH = 260;

    private double SCENE_WIDTH;
    private double SCENE_HEIGHT;

    // Turn order card and totem management
    private boolean totemClicked = false; // Enables track card selection only after the player selects their totem.
    private final Map<Character, Pane> trackCardTotemLayers = new HashMap<>(); // Track card layers used to update placed totems.
    private Pane turnOrderCardTotemLayer;

    // Tribe area
    private HBox tribeCardsBox;
    private Label selectedTribeLabel;
    private ToggleGroup tribeToggleGroup;
    private String selectedTribeNickname;

    private VBox playersStatsBox;
    private Pane lobbyTotemLayer;

    private Scene gameEndingScene;
    private Pane endGameTotemLayer;

    private boolean isButtonAlreadySelected = false;

    private enum StatType {
        FOOD,
        AXE,
        PRESTIGE,
        STARS,
        BOWL,
        COLOUR
    }

    private final Map<String, Map<StatType, Label>> playerStatLabels = new HashMap<>();

    // ============================================================
    // Application lifecycle
    // ============================================================

    @Override
    public void start(Stage primaryStage) {
        this.controllerReference = new ClientController();
        this.primaryStage = primaryStage;

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();

        SCENE_WIDTH = screen.getWidth() * 0.80;
        SCENE_HEIGHT = screen.getHeight() * 0.80;

        loginScene = new Scene(createLoginScene(), SCENE_WIDTH, SCENE_HEIGHT);

        primaryStage.setTitle("Login");
        primaryStage.setScene(loginScene);
        primaryStage.setResizable(true); // Resizable window.

        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();

    }

    @Override
    public void run() throws IOException {
        // Do not call start() here.
        // JavaFX automatically calls start(Stage primaryStage) after launch().
    }

    @Override
    public void update(ClientModel model, ViewEvent viewEvent) {

            this.modelReference = model;

        isButtonAlreadySelected = false;

        Platform.runLater(() -> {
            viewEvent.render(this);

        });
    }

    // ============================================================
    // View rendering callbacks
    // ============================================================

    @Override
    public void renderLogin(String message) {
        updateLogin("");
    }

    @Override
    public void renderPlayerJoin(String message) {
        updateChoiceScene();
        updateLobby();
    }

    @Override
    public void renderGameStart(String message) {
        gameCreation();
        updatePlayerStats();
    }

    @Override
    public void renderTotemPlaced(String message) {
        updateoffersPathBox();
        updateTop(message);
        updatePlayerStats();
    }

    @Override
    public void renderCardPicked(String message) {
        updateBoard();
        updateSelectedTribe();
        updateTop(message);
        updatePlayerStats();
        updateoffersPathBox();
    }

    @Override
    public void renderEndRound(String message) {
        round++;
        updateBoard();
        updateSelectedTribe();
        updateTop(message);
        updatePlayerStats();
        updateoffersPathBox();

    }

    @Override
    public void renderEndGame(String message) {
        showEndGameScene();
        updateEndGameRankingBox();
        updateGlobalRankingBox();
    }

    @Override
    public void renderDisconnection(String message) {
        primaryStage.setScene(gameChoiceScene);
        loginErororLabel.setText(message);
        // put null so they will be reinitialized with the reconnection and render game started event
        lobbyScene = null;
        gameScene = null;
        gameEndingScene = null;


    }

    @Override
    public void renderReconnection(String message) {
        updateChoiceScene();
        updateLobby();
    }

    @Override
    public void renderGameRestarted(String message) {
        gameCreation();
        updatePlayerStats();
    }

    @Override
    public void renderException(String message) {
        if (gameScene != null) updateTop(message);
        if (loginScene != null) loginErororLabel.setText(message);
        if (gameChoiceScene != null) gameChoiceErrorLabel.setText(message);
    }

    @Override
    public void handleNetworkError(String errorMessage) {
        primaryStage.setScene(loginScene);
        loginErororLabel.setText(errorMessage);
        // put null so they will be reinitialized with the reconnection and render game started event
        gameScene = null;
        gameChoiceScene = null;
        gameEndingScene = null;
        lobbyScene = null;
    }

    // ============================================================
    // Login scene
    // ============================================================

    private BorderPane createLoginScene() {
        BorderPane root = new BorderPane();
        Image backgroundImage = new Image(getClass().getResource("/background/login.png").toExternalForm());

        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true)
        );

        root.setBackground(new Background(bgImage));

        // Labels
        Label title = new Label("Mesos");
        title.setStyle("""
        -fx-text-fill: white;
        -fx-font-size: 46;
        -fx-font-weight: bold;
""");

        Label subtitle = new Label("Chose your Nickname");
        subtitle.
                setStyle("""
             -fx-text-fill: white;
                -fx-font-size: 20;
    """);

        Label connectionLabel = new Label("Connection type");
        connectionLabel.setStyle(
                """
        -fx-text-fill: white;
        -fx-font-size: 16;
    """);

        loginErororLabel = new Label();
        loginErororLabel.setStyle("""
        -fx-text-fill: #ffb3b3;
        -fx-font-size: 14;
        -fx-font-weight: bold;
        -fx-effect: dropshadow(gaussian, black, 5, 0.7, 1, 1);
    """);

        // Text fields
        TextField nicknameField = new TextField();
        nicknameField.setPromptText("Nickname");
        nicknameField.setMaxWidth(300);
        nicknameField.setPrefWidth(300);
        nicknameField.setMaxWidth(300);

        TextField ipField = new TextField();
        ipField.setPromptText("server IP");
        ipField.setMaxWidth(300);
        ipField.setPrefWidth(210);
        ipField.setMaxWidth(210);

        TextField portField = new TextField();
        portField.setPromptText("server Port");
        portField.setMaxWidth(300);
        portField.setPrefWidth(80);
        portField.setMaxWidth(80);

        // Buttons
        Button connectButton = new Button("Login");
        connectButton.setStyle(
                """
        -fx-font-size: 16;
        -fx-font-weight: bold;
        -fx-padding: 8 22 8 22;
    """);

        ComboBox<String> connectionTypeBox = new ComboBox<>();
        connectionTypeBox.getItems().addAll("Socket", "RMI");
        connectionTypeBox.setValue("Socket");
        connectionTypeBox.setMaxWidth(300);

        connectButton.setOnAction(event -> {
            if (!isButtonAlreadySelected) {
                String insertedNickname = nicknameField.getText().trim().toLowerCase();
                String insertedIp = ipField.getText().trim();
                String insertedPort = portField.getText().trim();

                if (insertedNickname.isEmpty() || insertedIp.isEmpty() || insertedPort.isEmpty()) {
                    loginErororLabel.setText("Insert all the fields to connect.");
                    isButtonAlreadySelected = false;
                    return;
                }

                String netProtocol = connectionTypeBox.getValue().toLowerCase();
                connect(insertedNickname, netProtocol, insertedIp, insertedPort); // Send the connection request to the controller.

            }

        });

        nicknameField.setOnAction(event -> connectButton.fire());

        loginBox = new VBox(12);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setMaxWidth(420);
        loginBox.setStyle("""
    -fx-background-color: transparent;
    -fx-background-radius: 16;
    -fx-padding: 35;
""");

        HBox serverBox = new HBox(10);
        serverBox.setAlignment(Pos.CENTER);
        serverBox.getChildren().addAll(ipField, portField);
        connectionTypeBox.setPrefWidth(300);
        connectionTypeBox.setMaxWidth(300);

        VBox.setMargin(connectButton, new Insets(10, 0, 0, 0));
        loginBox.getChildren().addAll(
                title,
                subtitle,
                nicknameField,
                serverBox,
                connectionLabel,
                connectionTypeBox,
                connectButton,
                loginErororLabel

        );

        root.setCenter(loginBox);
        return root;
    }

    private void showGameChoiceScene() {
        if (gameChoiceScene == null) {
            gameChoiceScene = new Scene(createGameChoiceScene(), SCENE_WIDTH, SCENE_HEIGHT);
        }

        primaryStage.setTitle("Game Menu");
        primaryStage.setScene(gameChoiceScene);
    }

    public void updateLogin(String message) {
        if (loginBox == null) return;

        loginErororLabel.setText(message);
        showGameChoiceScene();
    }

    // ============================================================
    // Game choice scene
    // ============================================================

    private BorderPane createGameChoiceScene() {
        BorderPane root = new BorderPane();

        Image backgroundImage = new Image(
                getClass().getResource("/background/login.png").toExternalForm()
        );

        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true)
        );

        root.setBackground(new Background(bgImage));

        Label title = new Label("HELLO WELCOME TO MESOS");
        title.setStyle("""
        -fx-text-fill: white;
        -fx-font-size: 38;
        -fx-font-weight: bold;
        -fx-effect: dropshadow(gaussian, black, 8, 0.8, 2, 2);
    """);

        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(35, 0, 0, 0));
        root.setTop(title);

        VBox createBox = new VBox(14);
        createBox.setAlignment(Pos.CENTER);
        createBox.setPrefWidth(280);
        createBox.setStyle("""
        -fx-background-color: transparent;
        -fx-background-radius: 16;
        -fx-padding: 25;
    """);

        VBox joinBox = new VBox(14);
        joinBox.setAlignment(Pos.CENTER);
        joinBox.setPrefWidth(280);
        joinBox.setStyle("""
        -fx-background-color: transparent;
        -fx-background-radius: 16;
        -fx-padding: 25;
    """);

        VBox centerBox = new VBox(12);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPrefWidth(300);
        centerBox.setStyle("""
        -fx-background-color: transparent;
        -fx-background-radius: 16;
        -fx-padding: 10;
    """);

        Button createGameButton = new Button("Create Game");
        Button joinGameButton = new Button("Join Game");

        createGameButton.setPrefWidth(240);
        joinGameButton.setPrefWidth(240);

        createGameButton.setStyle("""
        -fx-font-size: 17;
        -fx-font-weight: bold;
        -fx-padding: 10 20 10 20;
    """);

        joinGameButton.setStyle("""
        -fx-font-size: 17;
        -fx-font-weight: bold;
        -fx-padding: 10 20 10 20;
    """);

        ComboBox<Integer> playersNumberBox = new ComboBox<>();
        playersNumberBox.getItems().addAll(2, 3, 4, 5);
        playersNumberBox.setValue(2);
        playersNumberBox.setMaxWidth(240);

        ComboBox<TotemColor> totemColorBox = new ComboBox<>();
        totemColorBox.getItems().addAll(
                TotemColor.RED,
                TotemColor.BLUE,
                TotemColor.PURPLE,
                TotemColor.WHITE,
                TotemColor.YELLOW
        );
        totemColorBox.setValue(TotemColor.RED);
        totemColorBox.setMaxWidth(240);

        TextField idField = new TextField();
        idField.setPromptText("Game ID");
        idField.setMaxWidth(240);

        gameChoiceErrorLabel = new Label();
        gameChoiceErrorLabel.setWrapText(true);
        gameChoiceErrorLabel.setMaxWidth(260);
        gameChoiceErrorLabel.setAlignment(Pos.CENTER);
        gameChoiceErrorLabel.setStyle("""
        -fx-text-fill: #ffb3b3;
        -fx-font-size: 14;
        -fx-font-weight: bold;
        -fx-effect: dropshadow(gaussian, black, 5, 0.7, 1, 1);
    """);

        Label colorLabel = new Label("Choose your totem color");
        colorLabel.setStyle("""
        -fx-text-fill: white;
        -fx-font-size: 16;
        -fx-font-weight: bold;
    """);

        Label playersLabel = new Label("Number of players");
        playersLabel.setStyle("""
        -fx-text-fill: white;
        -fx-font-size: 16;
        -fx-font-weight: bold;
    """);

        Label gameIdLabel = new Label("Game ID");
        gameIdLabel.setStyle("""
        -fx-text-fill: white;
        -fx-font-size: 16;
        -fx-font-weight: bold;
    """);

        createBox.getChildren().addAll(
                playersLabel,
                playersNumberBox,
                createGameButton
        );

        centerBox.getChildren().addAll(
                gameChoiceErrorLabel,
                colorLabel,
                totemColorBox
        );

        joinBox.getChildren().addAll(
                gameIdLabel,
                idField,
                joinGameButton
        );

        HBox mainContent = new HBox(90);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.getChildren().addAll(createBox, centerBox, joinBox);

        createBox.setTranslateY(-200);
        joinBox.setTranslateY(-200);
        centerBox.setTranslateY(-20);

        BorderPane.setAlignment(mainContent, Pos.CENTER);
        root.setCenter(mainContent);

        createGameButton.setOnAction(event -> {
            if (!isButtonAlreadySelected) {

                isButtonAlreadySelected = true;
                String insertedNickname = modelReference.getHomePlayerNickname();
                String totemColorChoice = totemColorBox.getValue().toString().toLowerCase();
                int numPlayersChoice = playersNumberBox.getValue();

                createGame(insertedNickname, numPlayersChoice, totemColorChoice);
            }
        });

        joinGameButton.setOnAction(event -> {

            if (!isButtonAlreadySelected) {

                isButtonAlreadySelected = true;
                String gameIdText = idField.getText().trim();
                int gameId;

                try {
                    gameId = Integer.parseInt(gameIdText);
                } catch (NumberFormatException e) {
                    gameChoiceErrorLabel.setText("Please enter a valid numeric game ID.");
                    isButtonAlreadySelected = false;
                    return;
                }

                String insertedNickname = modelReference.getHomePlayerNickname();
                String totemColorChoice = totemColorBox.getValue().toString().toLowerCase();

                joinGame(insertedNickname, gameId, totemColorChoice);
            }
        });

        return root;
    }

    public boolean updateChoiceScene() {

        if (gameChoiceScene == null) { // Ignore updates that arrive before the scene is created.
            return false;}

        showLobbyScene(); // Move to the lobby after a successful create or join operation.
        return true;
    }

    private void showLobbyScene() {
        if (lobbyScene == null) {
            lobbyScene = new Scene(createLobbyScene(), SCENE_WIDTH, SCENE_HEIGHT);
        }

        primaryStage.setTitle("Lobby");
        primaryStage.setScene(lobbyScene);

    }

    // ============================================================
    // Lobby scene
    // ============================================================

    private StackPane createLobbyScene() {
        StackPane lobbyRoot = new StackPane();

        ImageView background = new ImageView(getClass().getResource("/background/lobby.png").toExternalForm()); // Background image.

        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(lobbyRoot.widthProperty());
        background.fitHeightProperty().bind(lobbyRoot.heightProperty());

        lobbyTotemLayer = new Pane();
        lobbyTotemLayer.setPickOnBounds(false);

        updateLobby(); // Draw the totems of the players currently in the lobby.

        VBox topOverlay = createLobbyLabels();

        StackPane.setAlignment(topOverlay, Pos.TOP_CENTER);
        StackPane.setMargin(topOverlay, new Insets(20, 0, 0, 0));

        lobbyRoot.getChildren().addAll(background, lobbyTotemLayer, topOverlay);

        return lobbyRoot;
    }

    private VBox createLobbyLabels() {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);

        box.setMaxWidth(Region.USE_PREF_SIZE);
        box.setMaxHeight(Region.USE_PREF_SIZE);

        box.setStyle("""
        -fx-padding: 10 0 0 0;
    """);

        Label title = new Label("MESOS LOBBY");
        title.setStyle("""
        -fx-text-fill: white;
        -fx-font-size: 38;
        -fx-font-weight: bold;
        -fx-effect: dropshadow(gaussian, black, 8, 0.8, 2, 2);
    """);

        Label lobbyGameIdLabel = new Label("GAME ID: " + modelReference.getGameId());
        lobbyGameIdLabel.setStyle("""
        -fx-text-fill: #ffd36b;
        -fx-font-size: 17;
        -fx-font-weight: bold;
        -fx-effect: dropshadow(gaussian, black, 6, 0.8, 1, 1);
    """);

        lobbyNumPlayerLabel = new Label("Waiting for players : 1 /" + modelReference.getNumPlayers()) ;
        lobbyNumPlayerLabel.setStyle("""
        -fx-text-fill: white;
        -fx-font-size: 15;
        -fx-effect: dropshadow(gaussian, black, 6, 0.8, 1, 1);
    """);

        box.getChildren().addAll(title, lobbyGameIdLabel, lobbyNumPlayerLabel);

        return box;
    }

    public void updateLobby() {
        if (lobbyScene == null) return;

        int playerNumber = 0;
        lobbyTotemLayer.getChildren().clear();

        ArrayList<ClientPlayer> players = new ArrayList<>();

        if (modelReference.getHomePlayer() != null) {
            players.add(modelReference.getHomePlayer());
        }

        if (modelReference.getOtherPlayers() != null) {
            players.addAll(modelReference.getOtherPlayers());
        }



        for (ClientPlayer player : players) {
            if (player == null) continue;
            playerNumber++;


            String color = player.getTotemColor();

            color = color.toLowerCase();

            VBox totemBox = createLobbyTotemBox(player);
            placeTotemByColor(totemBox, color);
            lobbyTotemLayer.getChildren().add(totemBox);
        }

        lobbyNumPlayerLabel.setText("Waiting for players : "+ playerNumber + "/" + modelReference.getNumPlayers());
    }

    private void placeTotemByColor(VBox node, String color) {
        if (color == null) return;

        double xRatio;
        double yRatio;

        switch (color.toLowerCase()) {
            case "red" -> {
                xRatio = 0.065;
                yRatio = 0.405;
            }
            case "yellow" -> {
                xRatio = 0.235;
                yRatio = 0.395;
            }
            case "blue" -> {
                xRatio = 0.425;
                yRatio = 0.415;
            }
            case "purple" -> {
                xRatio = 0.600;
                yRatio = 0.405;
            }
            case "white" -> {
                xRatio = 0.745;
                yRatio = 0.395;
            }
            default -> {
                xRatio = 0.45;
                yRatio = 0.40;
            }
        }

        node.layoutXProperty().bind(lobbyTotemLayer.widthProperty().multiply(xRatio));
        node.layoutYProperty().bind(lobbyTotemLayer.heightProperty().multiply(yRatio));
    }

    private VBox createLobbyTotemBox(ClientPlayer player) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);

        String color = player.getTotemColor();

        Label name = new Label(player.getNickname());
        name.setStyle("""
        -fx-text-fill: white;
        -fx-font-size: 17;
        -fx-font-weight: bold;
        -fx-effect: dropshadow(gaussian, black, 7, 0.9, 1, 1);
    """);

        ImageView totem = createAngryTotem(color.toLowerCase(), "lobby");

        box.getChildren().addAll(name, totem);

        return box;
    }

    // ============================================================
    // Communications actions
    // ============================================================

    private void connect(String chosenNickname, String netProtocol, String IP, String port) {
        try {
            int portNumber;
            try {
                portNumber = Integer.parseInt(port);
            } catch (NumberFormatException e) {
                loginErororLabel.setText("Please enter a valid numeric port.");
                isButtonAlreadySelected = false;
                return;
            }

            this.controllerReference.connect(netProtocol, IP, portNumber, chosenNickname, this);

        } catch (Exception e) {
            loginErororLabel.setText("A network error occurred while sending your request. Please try again.");
            isButtonAlreadySelected = false;
        }
    }

    private void createGame(String chosenNickname, int numPlayers, String totemColor) throws IllegalArgumentException {

        try {
            controllerReference.createGame(numPlayers, totemColor);
        } catch (Exception e) {
            gameChoiceErrorLabel.setText("A network error occurred while sending your request. Please try again.");
        }
    }

    private void joinGame(String nickname, int gameId, String totemColor) throws IllegalArgumentException {

        try {
            controllerReference.joinGame(gameId, totemColor);
        } catch (Exception e) {
            gameChoiceErrorLabel.setText("A network error occurred while sending your request. Please try again.");
        }

    }

    public void gameCreation() {

        if (gameScene == null) {
            createGameScene();
        }

        showGameScene();
    }

    // ============================================================
    // GAME SCENE
    // ============================================================
    private void addHoverEffect(ImageView card) {
        card.setOnMouseEntered(e -> {
            TranslateTransition tt = new TranslateTransition(Duration.millis(120), card);
            tt.setToY(-12);
            tt.play();
        });

        card.setOnMouseExited(e -> {
            TranslateTransition tt = new TranslateTransition(Duration.millis(120), card);
            tt.setToY(0);
            tt.play();
        });
    }
    private void addHoverEffect(Node node) {
        node.setOnMouseEntered(e -> node.setTranslateY(-12));
        node.setOnMouseExited(e -> node.setTranslateY(0));
    }

    private void createGameScene() {
        BorderPane root = new BorderPane();

        Image backgroundImage = new Image(getClass().getResource("/background/game.jpeg").toExternalForm());

        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true)
        );

        root.setBackground(new Background(bgImage));

        root.setTop(createTop());
        root.setCenter(createCenter());
        root.setRight(createRight());
        root.setBottom(createBottom());

        ScrollPane gameScroll = new ScrollPane(root);

        gameScroll.setFitToWidth(true);
        gameScroll.setFitToHeight(false);

        gameScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        gameScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        gameScroll.setStyle("""
        -fx-background-color: transparent;
        -fx-background: transparent;
        -fx-control-inner-background: transparent;
    """);

        gameScroll.viewportBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            root.setMinWidth(newBounds.getWidth());

            double minHeight = Math.max(SCENE_HEIGHT, newBounds.getHeight());
            root.setMinHeight(minHeight);
        });

        gameScene = new Scene(gameScroll, SCENE_WIDTH, SCENE_HEIGHT);
    }

    private void showGameScene() {
        if (primaryStage == null || gameScene == null) return;

        if (primaryStage.getScene() != gameScene) {
            primaryStage.setTitle("Game - " + modelReference.getNumPlayers() + " Players");
            primaryStage.setScene(gameScene);
        }
    }

    // ------------------------------------------------------------
    // Top area
    // ------------------------------------------------------------

    private StackPane createTop() {
        topPane = new StackPane();
        topPane.setStyle("-fx-background-color: rgb(255, 0, 0); -fx-padding: 15;");

        serverMessage = new Label();
        serverMessage.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 20; -fx-font-weight: bold;");

        roundCounter = new Label();
        roundCounter.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 15; -fx-font-weight: bold;");

        round = 1;

        StackPane.setAlignment(serverMessage, Pos.CENTER);
        StackPane.setAlignment(roundCounter, Pos.CENTER_RIGHT);

        topPane.getChildren().addAll(serverMessage, roundCounter);

        updateTop("Game has Started");

        return topPane;
    }

    public void updateTop (String message) {
        if (topPane == null) return;
        serverMessage.setText(message);
        String roundString = "Current Round: " + round.toString();
        roundCounter.setText(roundString);

    }

    // ------------------------------------------------------------
    // Center board area
    // ------------------------------------------------------------

    private StackPane createCenter() {
        StackPane centerWrapper = new StackPane();
        centerWrapper.setStyle("-fx-background-color: transparent;");
        ImageView deck =  new ImageView(getClass().getResource("/deck/1.png").toExternalForm());// parto con uno e poi aggiorno con il primo endRound
        deck.setFitWidth(BOARD_CARD_WIDTH);
        deck.setFitHeight(BOARD_CARD_HEIGHT);

        StackPane turnOrderCard = createTurnOrderCardStack();

        ArrayList<Character> trackCards = modelReference.getOffersPath();

        // Center game scene
        VBox centerBox = new VBox(25);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setStyle("-fx-padding: 30;");

        HBox topDeckable = createCardRow(); // Top deckable row.
        HBox topBuilding = createCardRow(); // Top building row.
        HBox bottomDeckable = createCardRow(); // Bottom deckable row.
        HBox bottomBuilding = createCardRow(); // Bottom building row.
        offersPathBox = createCardRow(); // Deck, turn order card and track cards.

        topCenterBox = new HBox(8);
        topCenterBox.getChildren().addAll(topDeckable, topBuilding);
        bottomCenterBox = new HBox(8);
        bottomCenterBox.getChildren().addAll(bottomDeckable, bottomBuilding);

        offersPathBox.getChildren().addAll(deck, turnOrderCard); // Add the deck and the turn order card to the offer path.
        for (char name : trackCards) {
            StackPane trackCard = createTrackCard(name);
            offersPathBox.getChildren().add(trackCard);
        }

        centerBox.getChildren().addAll(topCenterBox, offersPathBox, bottomCenterBox); // Add the board rows and the offer path to the center area.
        centerWrapper.getChildren().add(centerBox); // Add the board container to the wrapper.
        updateBoard(); // Fill the board rows with the current cards.

        BorderPane.setAlignment(centerWrapper, Pos.CENTER);
        VBox.setVgrow(centerBox, Priority.ALWAYS);

        return centerWrapper;

    }

    private StackPane createTurnOrderCardStack() {
        StackPane stack = new StackPane();

        stack.setPrefSize(BOARD_CARD_WIDTH, BOARD_CARD_HEIGHT);
        stack.setMaxSize(BOARD_CARD_WIDTH, BOARD_CARD_HEIGHT);

        ImageView turnOrderCard = new ImageView(
                getClass().getResource("/turnOrderCards/" + modelReference.getNumPlayers() + ".png").toExternalForm()
        );
        turnOrderCard.setFitWidth(BOARD_CARD_WIDTH);
        turnOrderCard.setFitHeight(BOARD_CARD_HEIGHT);
        turnOrderCard.setPreserveRatio(false);
        turnOrderCard.setSmooth(true);

        turnOrderCardTotemLayer = new Pane();
        turnOrderCardTotemLayer.setPrefSize(BOARD_CARD_WIDTH, BOARD_CARD_HEIGHT);
        turnOrderCardTotemLayer.setMaxSize(BOARD_CARD_WIDTH, BOARD_CARD_HEIGHT);
        turnOrderCardTotemLayer.setPickOnBounds(false); // The layer does not capture clicks outside its content.

        addTotemsOnTurnOrderCard(turnOrderCardTotemLayer);

        stack.getChildren().addAll(turnOrderCard, turnOrderCardTotemLayer);

        return stack;
    }

    private void addTotemsOnTurnOrderCard(Pane totemLayer) {

        for (String nickname : modelReference.getPlayerOrderInTurnOrderCard()) {
            if (nickname == null) continue; // Empty slot.
            ClientPlayer player = modelReference.getPlayerByNickname(nickname);
            String color = player.getTotemColor().toLowerCase();

            int pos = modelReference.getPlayerPositionInTurnOrderCard(player.getNickname()); // Read the slot where the player totem must be placed.

            // Create and position the totem image.
            ImageView totem = createSmallTotem(color);
            positionTotemOnTurnOrderCard(totem, pos); // Place the totem in the correct turn order slot.

            // Only the home player's totem can be selected.
            if (player.getNickname().equals(modelReference.getHomePlayerNickname())) {
                totem.setCursor(Cursor.HAND);

                totem.setOnMouseClicked(event -> {
                    totemClicked = true;
                    updateTop("Totem selected. Choose a track card.");
                });
            }

            totemLayer.getChildren().add(totem);
        }
    }

    private void positionTotemOnTurnOrderCard(ImageView totem, int index) {

        PointRatio pos = getTurnOrderSlotPosition(index);
        double x = BOARD_CARD_WIDTH * pos.x() - SMALL_TOTEM_WIDTH / 2; // Center the totem on the target ratio.
        double y = BOARD_CARD_HEIGHT * pos.y() - SMALL_TOTEM_HEIGHT / 2;

        totem.setLayoutX(x); // Horizontal position inside the card.
        totem.setLayoutY(y); // Vertical position inside the card.
    }

    private ImageView createSmallTotem(String color) {
        ImageView totem = new ImageView(getClass().getResource("/totem/" + color + ".png").toExternalForm());

        totem.setFitWidth(SMALL_TOTEM_WIDTH);
        totem.setFitHeight(SMALL_TOTEM_HEIGHT);
        totem.setPreserveRatio(true);
        totem.setSmooth(true);

        return totem;
    }

    private record PointRatio(double x, double y) { }

    // Returns the position ratio used to center a totem in the correct turn order slot.
    private PointRatio getTurnOrderSlotPosition(int index) {
        if (modelReference.getNumPlayers() < 4) {
            return switch (index) {
                case 0 -> new PointRatio(0.50, 0.25);
                case 1 -> new PointRatio(0.50, 0.43);
                case 2 -> new PointRatio(0.50, 0.61);
                case 3 -> new PointRatio(0.50, 0.74);
                case 4 -> new PointRatio(0.50, 0.87);
                default -> throw new IllegalStateException("Unexpected value: " + index);
            };
        } else {
            return switch (index) {
                case 0 -> new PointRatio(0.50, 0.12);
                case 1 -> new PointRatio(0.50, 0.32);
                case 2 -> new PointRatio(0.50, 0.49);
                case 3 -> new PointRatio(0.50, 0.69);
                case 4 -> new PointRatio(0.50, 0.82);
                default -> throw new IllegalStateException("Unexpected value: " + index);
            };
        }
    }

    private void addTotemOnTrackCard(Pane totemLayer, String color) {
        ImageView totem = createSmallTotem(color);

        PointRatio pos = new PointRatio(0.50, 0.20);

        double x = BOARD_CARD_WIDTH * pos.x() - SMALL_TOTEM_WIDTH / 2;
        double y = BOARD_CARD_HEIGHT * pos.y() - SMALL_TOTEM_HEIGHT / 2;

        totem.setLayoutX(x);
        totem.setLayoutY(y);

        totem.setMouseTransparent(true);

        totemLayer.getChildren().clear();
        totemLayer.getChildren().add(totem);
    }

    private void updateCenterCards(int row, HBox box) {
        ArrayList<ArrayList<Integer>> currentBoard = modelReference.getBoard();
        List<Integer> orderedTopRow = currentBoard.get(row).stream().sorted(Comparator.naturalOrder()).toList(); // Sort cards by id so that the row is displayed consistently.

        box.getChildren().clear(); // Clear the current cards in the row.
        for (Integer cardId : orderedTopRow) {
            ImageView card = createPickableCard(cardId, row);
            box.getChildren().add(card);
        }
    }

    public void updateoffersPathBox() {

        // Update turn order card totems.
        turnOrderCardTotemLayer.getChildren().clear(); // Clear and redraw totems based on the updated turn order.
        addTotemsOnTurnOrderCard(turnOrderCardTotemLayer);

        // Update track card totems.
        ArrayList<ClientPlayer> players = new ArrayList<>(modelReference.getOtherPlayers());
        players.add(modelReference.getHomePlayer());
        clearAllTrackCardTotems(); // Clear all layers before drawing the updated totems.

        Map<Character, String> positionToNickname = modelReference.getPositionToNickname();
        for (Character position : positionToNickname.keySet()) {
            String nickname = positionToNickname.get(position);
            ClientPlayer player = modelReference.getPlayerByNickname(nickname);
            if (player != null) {
                Pane totemLayer = trackCardTotemLayers.get(position);
                addTotemOnTrackCard(totemLayer, player.getTotemColor().toLowerCase());
            }
        }

    }

    private void clearAllTrackCardTotems() {
        for (Pane layer : trackCardTotemLayers.values()) {
            layer.getChildren().clear();
        }
    }

    public void updateBoard() {

        for (int i = 0; i < 2; i++) {
            HBox box = (HBox) topCenterBox.getChildren().get(i);
            updateCenterCards(i, box);
        }

        for (int i = 0; i < 2; i++) {
            HBox box = (HBox) bottomCenterBox.getChildren().get(i);
            updateCenterCards(i + 2, box);
        }



        if (modelReference.getAge() > 0) { // update fatto solo se ho l'age mi arriva con l'evento di endRound
            ImageView deck = new ImageView(
                    getClass().getResource("/deck/" + modelReference.getAge() + ".png").toExternalForm()
            );

            deck.setFitWidth(BOARD_CARD_WIDTH);
            deck.setFitHeight(BOARD_CARD_HEIGHT);
            deck.setPreserveRatio(false);
            deck.setSmooth(true);

            offersPathBox.getChildren().removeFirst();
            offersPathBox.getChildren().addFirst(deck);

        }

    }

    private HBox createCardRow() {
        HBox row = new HBox(10); // we use 10 pixel of space between the cards
        row.setAlignment(Pos.CENTER); // this function is needed to center the cards in the row. So the card will spawn from the center to the left.
        row.setStyle("""
        -fx-background-color: rgba(255, 255, 255, 0.18);  \s
        -fx-background-radius: 14;
        -fx-padding: 7.5;
        \s""");
        // this is the color the hbox
        // the radius is for the rounded corners and the padding is for the space between the border and the content of the hbox
        // the padding is the space between the border and the content of the hbox, so if you want to have more space between the border and the cards you can increase the padding value.

        return row;
    }

    // ------------------------------------------------------------
    // Bottom tribe area
    // ------------------------------------------------------------

    private ScrollPane createBottom() {
        ScrollPane bottom = new ScrollPane();
        bottom.setFitToWidth(true);
        bottom.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        bottom.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        bottom.setPrefWidth(280);
        bottom.setStyle("""
        -fx-background-color: transparent;
        -fx-background: transparent;
    """);

        VBox bottomBox = new VBox(8);
        bottomBox.setStyle("""
        -fx-background-color: rgba(60, 20, 40, 0.60);
        -fx-padding: 10;
        -fx-border-color: rgba(255, 255, 255, 0.25);
        -fx-border-width: 1 0 0 0;
    """);

        HBox playersTabs = new HBox(8);
        playersTabs.setAlignment(Pos.CENTER_LEFT);

        tribeToggleGroup = new ToggleGroup();

        selectedTribeNickname = modelReference.getHomePlayer().getNickname();

        ToggleButton homeButton = createTribePlayerButton(
                modelReference.getHomePlayer().getNickname()
        );
        homeButton.setSelected(true);
        playersTabs.getChildren().add(homeButton);

        for (ClientPlayer player : modelReference.getOtherPlayers()) {
            playersTabs.getChildren().add(
                    createTribePlayerButton(player.getNickname())
            );
        }

        tribeCardsBox = new HBox(8);
        tribeCardsBox.setAlignment(Pos.CENTER_LEFT);

        bottomBox.getChildren().addAll(playersTabs, tribeCardsBox);
        bottom.setContent(bottomBox);

        updateSelectedTribe();

        return bottom;
    }

    private ToggleButton createTribePlayerButton(String nickname) {
        ToggleButton button = new ToggleButton(nickname);
        button.setToggleGroup(tribeToggleGroup);

        button.setStyle("""
        -fx-background-color: rgba(255, 255, 255, 0.15);
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 10;
        -fx-padding: 6 14 6 14;
        -fx-border-color: rgba(255, 255, 255, 0.35);
        -fx-border-radius: 10;
    """);

        button.setOnAction(event -> {
            selectedTribeNickname = nickname;
            updateSelectedTribe();
        });

        return button;
    }

    public void updateSelectedTribe() {
        if (tribeCardsBox == null || selectedTribeNickname == null) {
            return;
        }
        tribeCardsBox.getChildren().clear();

        if (selectedTribeLabel != null) {
            selectedTribeLabel.setText("Tribe di: " + selectedTribeNickname);
        }

        ClientPlayer selectedPlayer = modelReference.getPlayerByNickname(selectedTribeNickname);

        if (selectedPlayer == null || selectedPlayer.getPlayerTribe() == null) {
            return;
        }

        selectedPlayer.getPlayerTribe()
                .stream()
                .sorted(Integer::compareTo)
                .forEach(cardId -> {
                    ImageView card = createTribeCard(cardId);
                    tribeCardsBox.getChildren().add(card);
                });
    }

    // ------------------------------------------------------------
    // Right statistics area
    // ------------------------------------------------------------

    private ScrollPane createRight() {
        ScrollPane scroll = new ScrollPane();
        scroll.setStyle("""
        -fx-background-color: transparent;
        -fx-background: transparent;
    """);

        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setPrefWidth(280);

        playersStatsBox = new VBox(15);
        playersStatsBox.setStyle("""
        -fx-background-color: rgba(80, 40, 80, 0.8);
        -fx-padding: 15;
    """);

        buildInitialPlayersStatsPanels();

        scroll.setContent(playersStatsBox);
        return scroll;
    }

    private void buildInitialPlayersStatsPanels() {
        playersStatsBox.getChildren().clear();
        playerStatLabels.clear();
        ArrayList<String> players = new ArrayList<>(modelReference.getOtherPlayers().stream().map(ClientPlayer::getNickname).toList());
        players.add(modelReference.getHomePlayer().getNickname());

        for (int i = players.size() - 1; i >= 0; i--) {
            String playerName = players.get(i);
            playersStatsBox.getChildren().add(
                    createPlayerStats(playerName)
            );

        }
    }

    private VBox createPlayerStats(String playerName) {
        VBox panel = new VBox(8);
        panel.setPrefWidth(240);

        panel.setStyle("""
        -fx-background-color: rgba(90, 35, 60, 0.95);
        -fx-border-color: #67c7d8;
        -fx-border-width: 2;
        -fx-background-radius: 8;
        -fx-border-radius: 8;
        -fx-padding: 8;
    """);

        Label nameLabel = new Label(playerName);
        nameLabel.setStyle("""
        -fx-text-fill: white;
        -fx-font-size: 14;
        -fx-font-weight: bold;
    """);

        GridPane statsGrid = createStatsGrid(playerName);

        panel.getChildren().addAll(nameLabel, statsGrid);

        return panel;
    }

    private GridPane createStatsGrid(String playerName) {
        GridPane grid = new GridPane();
        String colour = modelReference.getPlayerByNickname(playerName).getTotemColor().toLowerCase();

        grid.setHgap(10);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER_LEFT);

        Map<StatType, Label> labels = new HashMap<>();  // Labels used to update this player statistics.

        addStat(grid, labels, StatType.FOOD, getClass().getResource("/stats/food.png").toExternalForm(), 0, 0);
        addStat(grid, labels, StatType.AXE, getClass().getResource("/stats/buildingDiscount.png").toExternalForm(), 0, 2);
        addStat(grid, labels, StatType.PRESTIGE, getClass().getResource("/stats/prestige.png").toExternalForm(), 0, 4);

        addStat(grid, labels, StatType.STARS, getClass().getResource("/stats/stars.png").toExternalForm(), 1, 0);
        addStat(grid, labels, StatType.BOWL, getClass().getResource("/stats/gatheringDiscount.png").toExternalForm(), 1, 2);
        addStat(grid, labels, StatType.COLOUR, getClass().getResource("/colour/" + colour + ".png").toExternalForm(), 1, 4);

        playerStatLabels.put(playerName, labels);  // Store labels so that they can be updated later.

        return grid;
    }

    private void addStat(
            GridPane grid,
            Map<StatType, Label> labels,
            StatType type,
            String imagePath,
            int row,
            int col
    ) {

        ImageView icon = new ImageView(imagePath); // Create the icon for the statistic.
        icon.setFitWidth(34);
        icon.setFitHeight(34);
        icon.setPreserveRatio(true);
        icon.setSmooth(true);
        if (type != StatType.COLOUR) {
            Label valueLabel = new Label("0");  // Initial value for the statistic.
            valueLabel.setMinWidth(26);
            valueLabel.setAlignment(Pos.CENTER_LEFT);

            valueLabel.setStyle("""
        -fx-text-fill: white;
        -fx-font-size: 15;
        -fx-font-family: 'Georgia';
        -fx-font-weight: bold;
    """);
            grid.add(valueLabel, col + 1, row);
            labels.put(type, valueLabel);

        }
        grid.add(icon, col, row);  // Add the statistic icon to the grid.

    }

    public void updatePlayerStats() {

        ArrayList<ClientPlayer> clientPlayers = new ArrayList<>(modelReference.getOtherPlayers());
        clientPlayers.add(modelReference.getHomePlayer());

        for (ClientPlayer p : clientPlayers) {

            Map<StatType, Label> labels = playerStatLabels.get(p.getNickname());

            labels.get(StatType.FOOD).setText(String.valueOf(p.getNumFood()));
            labels.get(StatType.PRESTIGE).setText(String.valueOf(p.getNumPrestigePoints()));
            labels.get(StatType.STARS).setText(String.valueOf(p.getNumStars()));
            labels.get(StatType.AXE).setText(String.valueOf(p.getTotalBuildingDiscount()));
            labels.get(StatType.BOWL).setText(String.valueOf(p.getTotalGatheringDiscount()));

        }

    }

    private ImageView createPickableCard(int id, int row) {
        ImageView card = new ImageView(getClass().getResource("/cardsFront/" + id + ".jpg").toExternalForm());
        card.setFitWidth(BOARD_CARD_WIDTH);
        card.setFitHeight(BOARD_CARD_HEIGHT);
        card.setPreserveRatio(true);
        card.setSmooth(true);
        addHoverEffect(card);

        card.setCursor(Cursor.HAND);

        card.setOnMouseClicked(event -> {
            if (!isButtonAlreadySelected) {
                isButtonAlreadySelected = true;
                pickCard(modelReference.getHomePlayerNickname(), id, row);
            }
        });
        return card;
    }

    private void pickCard(String nickname, int cardId, int row) {
        try {
            controllerReference.pickCard(cardId);
        } catch (Exception e) {
            throw new RuntimeException("[Client] A network error occurred while sending your request\nDetails: " + e.getMessage());
        }
    }

    private ImageView createTribeCard(int id) {
        ImageView card = new ImageView(getClass().getResource("/cardsFront/" + id + ".jpg").toExternalForm());
        card.setFitWidth(BOARD_CARD_WIDTH);
        card.setFitHeight(BOARD_CARD_HEIGHT);
        card.setPreserveRatio(true);
        card.setSmooth(true);
        return card;
    }

    private StackPane createTrackCard(char name) {
        StackPane trackCard = new StackPane();

        trackCard.setPrefSize(BOARD_CARD_WIDTH, BOARD_CARD_HEIGHT);
        trackCard.setMinSize(BOARD_CARD_WIDTH, BOARD_CARD_HEIGHT);
        trackCard.setMaxSize(BOARD_CARD_WIDTH, BOARD_CARD_HEIGHT);

        ImageView cardImage = new ImageView(
                getClass().getResource("/trackCards/" + name + ".png").toExternalForm()
        );

        cardImage.setFitWidth(BOARD_CARD_WIDTH);
        cardImage.setFitHeight(BOARD_CARD_HEIGHT);
        cardImage.setPreserveRatio(false);
        cardImage.setSmooth(true);

        Pane trackCardtotemLayer = new Pane();
        trackCardtotemLayer.setPrefSize(BOARD_CARD_WIDTH, BOARD_CARD_HEIGHT);
        trackCardtotemLayer.setMinSize(BOARD_CARD_WIDTH, BOARD_CARD_HEIGHT);
        trackCardtotemLayer.setMaxSize(BOARD_CARD_WIDTH, BOARD_CARD_HEIGHT);
        trackCardTotemLayers.put(name, trackCardtotemLayer); // Store the layer so track card totems can be updated later.

        trackCardtotemLayer.setMouseTransparent(true);

        trackCard.getChildren().addAll(cardImage, trackCardtotemLayer);

        trackCard.setCursor(javafx.scene.Cursor.HAND);
        addHoverEffect(trackCard);

        trackCard.setOnMouseClicked(event -> {
            if (!totemClicked) {
                updateTop("Select your totem first.");
                return;
            }

            try {
                controllerReference.placeTotem(name);
            } catch (Exception e) {
                throw new RuntimeException("[Client] A network error occurred while sending your request\nDetails: " + e.getMessage());
            }

            totemClicked = false; // Reset the selection after sending the placement request.
        });
        return trackCard;
    }

    private ImageView createAngryTotem(String totemColor, String totemType) {
        ImageView totem = new ImageView(
                getClass().getResource("/totemLobby/" + totemColor + ".png").toExternalForm()
        );

        if (totemType.equals("endGame"))
            totem.setFitWidth(END_TOTEM_WIDTH);
        else
            totem.setFitWidth(LOBBY_TOTEM_WIDTH);

        totem.setPreserveRatio(true);
        totem.setSmooth(true);

        return totem;
    }

    // ============================================================
    // End-game scene
    // ============================================================

    private void createEndGameScene() {
        StackPane root = new StackPane();

        ImageView background = new ImageView(getClass().getResource("/background/endGame.png").toExternalForm());

        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());

        endGameTotemLayer = new Pane();
        endGameTotemLayer.setPickOnBounds(false);

        endGameRankingBox = new VBox(10);
        endGameRankingBox.setStyle("""
        -fx-background-color: rgba(20, 20, 20, 0.7);
        -fx-padding: 15;
        -fx-background-radius: 10;
    """);

        endGameRankingBox.setVisible(false);
        localRankingBox = new VBox(8);
        globalRankingBox = new VBox(8);

        localRankingBox.setVisible(true);
        globalRankingBox.setVisible(false);
        globalRankingBox.managedProperty().bind(globalRankingBox.visibleProperty());
        localRankingBox.managedProperty().bind(localRankingBox.visibleProperty());

        ToggleGroup rankingModeGroup = new ToggleGroup();

        ToggleButton localBtn = new ToggleButton("Local Ranking");
        ToggleButton globalBtn = new ToggleButton("Global Ranking");
        localBtn.setToggleGroup(rankingModeGroup);
        globalBtn.setToggleGroup(rankingModeGroup);
        localBtn.setSelected(true);

        localBtn.setOnAction(e -> {
            localRankingBox.setVisible(true);
            globalRankingBox.setVisible(false);
        });

        globalBtn.setOnAction(e -> {
            localRankingBox.setVisible(false);
            globalRankingBox.setVisible(true);
        });

        HBox toggleRow = new HBox(8, localBtn, globalBtn);
        toggleRow.setAlignment(Pos.CENTER);

        endGameRankingBox.getChildren().addAll(toggleRow, localRankingBox, globalRankingBox);

        StackPane.setAlignment(endGameRankingBox, Pos.TOP_CENTER);
        StackPane.setMargin(endGameRankingBox, new Insets(0, 0, 350, 900));

        rankingToggle = new ToggleButton("Show Total Ranking");
        rankingToggle.setStyle("""
        -fx-background-color: rgba(255, 255, 255, 0.2);
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
        -fx-padding: 6 12;
    """);

        rankingToggle.setOnAction(e -> {
            boolean show = rankingToggle.isSelected();
            endGameRankingBox.setVisible(show);
            rankingToggle.setText(show ? "Hide Ranking" : "Show Ranking");
        });

        StackPane.setAlignment(rankingToggle, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(rankingToggle, new Insets(20, 0, 0, 20));

        root.getChildren().addAll(background, endGameTotemLayer, rankingToggle, endGameRankingBox);
        gameEndingScene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
    }

    public void updateEndGameRankingBox() {
        localRankingBox.getChildren().clear();

        ArrayList<String> ranking = modelReference.getFinalRank();

        for (int i = ranking.size() - 1; i >= 0; i--) {
            String name = ranking.get(i);
            Label lbl = new Label((ranking.size() - i) + "°  " + name + " " + modelReference.getPlayerByNickname(name).getNumPrestigePoints() + " pts");
            lbl.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 16;
            -fx-font-weight: bold;
        """);
            localRankingBox.getChildren().add(lbl);
        }
    }

    public void updateGlobalRankingBox() {
        globalRankingBox.getChildren().clear();
        List<String> global = modelReference.getGlobalRank();

        for (int i = 0; i <= global.size() - 1; i++) {
            String name = global.get(i);
            Label lbl = new Label(name);
            lbl.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");
            globalRankingBox.getChildren().add(lbl);
        }
    }

    public void showEndGameScene() {
        if (gameEndingScene == null) {
            createEndGameScene();
        }

        primaryStage.setTitle("Fine partita");
        primaryStage.setScene(gameEndingScene);

        updateEndGamePodium();
    }

    private void updateEndGamePodium() {

        endGameTotemLayer.getChildren().clear();

        ArrayList<String> ranking = modelReference.getFinalRank();
        int pos = 0;

        for (int i = Math.min(3, ranking.size()) - 1; i >= 0; i--) {// Use the minimum because a two-player game has only two podium positions.

            ClientPlayer player = modelReference.getPlayerByNickname(ranking.get(i));

            ImageView totem = createAngryTotem(
                    player.getTotemColor().toLowerCase(), "endgame");
            Label nickname = new Label(player.getNickname());
            nickname.setStyle("""
        -fx-text-fill: white;
        -fx-font-size: 17;
        -fx-font-weight: bold;
        -fx-effect: dropshadow(gaussian, black, 7, 0.9, 1, 1);
    """);

            VBox totemBox = new VBox(4);
            totemBox.setAlignment(Pos.CENTER);
            totemBox.setPrefWidth(END_TOTEM_BOX_WIDTH);
            totemBox.setMaxWidth(END_TOTEM_BOX_WIDTH);
            totemBox.getChildren().addAll(nickname, totem);

            placeTotemOnPodium(totemBox, pos);
            pos++;

            endGameTotemLayer.getChildren().add(totemBox);

        }
    }

    private void placeTotemOnPodium(VBox totemBox, int rankingPosition) {
        double xRatio;
        double yRatio;

        switch (rankingPosition) {
            case 0 -> { // primo posto, centro
                xRatio = 0.500;
                yRatio = 0.270;
            }
            case 1 -> { // secondo posto, sinistra
                xRatio = 0.275;
                yRatio = 0.360;
            }
            case 2 -> { // terzo posto, destra
                xRatio = 0.720;
                yRatio = 0.375;
            }
            default -> {
                xRatio = 0.5;
                yRatio = 0.5;
            }
        }

        totemBox.layoutXProperty().bind(
                endGameTotemLayer.widthProperty()
                        .multiply(xRatio)
                        .subtract(END_TOTEM_BOX_WIDTH / 2)
        );

        totemBox.layoutYProperty().bind(
                endGameTotemLayer.heightProperty()
                        .multiply(yRatio)
        );
    }

    //----------------------------------------------------
    //
    //----------------------------------------------------





}
