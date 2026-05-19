package it.polimi.ingsw.am14.view;

import it.polimi.ingsw.am14.clientModelController.ClientController;
import it.polimi.ingsw.am14.clientModelController.ClientModel;
import it.polimi.ingsw.am14.clientModelController.ClientPlayer;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import it.polimi.ingsw.am14.network.VirtualServer;
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

// TODO: salviamo il nickname qui nella gui , in quanto chiamo tante volte il metodo getHomePlayernickname???

//TODO 15/05 fatto tutto, manca : aggiornamento posizione del player sulla trackCard quando finito a picakare --> mettere playeer position a null ( gestione nell gui ok , va fatta nell'evento)
// controllare l'update stats che non funziona perfettamente
// creare la scena di fine partita
// chiedere a lorenzo di ritagliare i totem che dopo varie prove dovrebbero essere centrati bene

// TODO: opzionale per LAB
// finire sempre a definire il design grafico del login, game choiche e lobby con i i tre totem a sx leggermente decentrati
//TODO aggiustare il testo sopra
//TODO aggiungere num rounds
//TODO scena finale con punteggi e database
// TODO: RIMUOVERE PARAMETRI INUTILIZZATI NEI METODI DEL CONTROLLER
// TODO: UPDATE TOP DA SEPARARE TRA INTERNO E ESTERNO


public class GUIView extends Application implements ViewType {

    // sono tutti a null fino a quando non verranno inizializzati
    private ClientModel modelReference;
    private ClientController controllerReference;
    private Stage primaryStage;

    private Scene gameChoiceScene;
    private Scene lobbyScene;
    private Scene gameScene;

    private VBox loginBox;
    private Label gameChoiceErrorLabel;
    private Label loginErororLabel;
    private Label lobbyNumPlayerLabel;

    //topGameScene
    private Label serverMessage;
    private VBox topBox;

    //centerGameScene
    private HBox topCenterBox;
    private HBox bottomCenterBox;
    private HBox offersPathBox;


    private static final double BOARD_CARD_WIDTH = 110;
    private static final double BOARD_CARD_HEIGHT = 165;
    private static final double SMALL_TOTEM_WIDTH = 46;
    private static final double SMALL_TOTEM_HEIGHT = 56;
    private static final double END_TOTEM_WIDTH = 230;
    private static final double LOBBY_TOTEM_WIDTH = 300;
    private static final double END_TOTEM_BOX_WIDTH = 260;

    private double SCENE_WIDTH;
    private double SCENE_HEIGHT;

    //TurnOrderCard + Totem managament
    private boolean totemClicked = false; // it's needed to make the trackCards clickable (if the player has  clicked it's token)
    private Map<Character,Pane> trackCardTotemLayers = new HashMap<>(); // we need to keep track of the panes with the totems on the track cards to update them when a new totem is placed or removed
    private Pane turnOrderCardTotemLayer;

    //Tribe
    private HBox tribeCardsBox;
    private Label selectedTribeLabel;
    private ToggleGroup tribeToggleGroup;
    private String selectedTribeNickname;

    private VBox playersStatsBox;
    private Pane lobbyTotemLayer;

    private Scene endGameScene;
    private Pane endGameTotemLayer;


    private enum StatType {
        FOOD,
        AXE,
        PRESTIGE,
        STARS,
        BOWL,
        COLOUR
    }

    private final Map<String, Map<StatType, Label>> playerStatLabels = new HashMap<>();
    private String updateType;

    @Override
    public void start(Stage primaryStage) {
        this.controllerReference = new ClientController();
        this.primaryStage = primaryStage;

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();

        SCENE_WIDTH = screen.getWidth() * 0.80;   // 80% dello schermo
        SCENE_HEIGHT = screen.getHeight() * 0.80; // 80% dello schermo

        Scene loginScene = new Scene(createLoginScene(), SCENE_WIDTH, SCENE_HEIGHT);

        primaryStage.setTitle("Login");
        primaryStage.setScene(loginScene);
        primaryStage.setResizable(true); // adattabile
        primaryStage.show();
    }



    @Override
    public void run() throws IOException {
        // Non chiamare start() qui.
        // JavaFX chiama automaticamente start(Stage primaryStage) dopo launch().
    }



    @Override
    public void update(ClientModel model, ViewEvent event) {

        // TODO: da rimuovere
        String eventChoice = "test";

        if (this.modelReference == null)
            this.modelReference = model;
        System.out.println("GUI update received: " + eventChoice);

        updateType = eventChoice;

        Platform.runLater(() -> {
            event.render(this);
//            switch (eventChoice) {
//                case "login" -> updateLogin();
//                case "gameChoice" -> {
//                    updateChoiceScene();
//                    updateLobby();
//                }
//                case "lobbyUpdate" -> updateLobby();
//                case "gameStart" -> gameCreation();
//                case "pickUpdate","endRound" -> {
//                    updateBoard();
//                    updateSelectedTribe();
//                    updateTop(modelReference.getMessage());
//                    updatePlayerStats();
//                    updateoffersPathBox();
//                }
//                case "positionUpdate" -> {
//                    updateoffersPathBox();
//                    updateTop(modelReference.getMessage());
//                }
//                //case "gameOver" -> showEndGameScene(); da fare ancora
//                case "exception" -> {
//                        updateTop(modelReference.getMessage()); // controllo di esistenza della scena nei singoli metodi
//                        updateChoiceScene();
//                        updateLogin();
//                    // verificare con Marco e Lorenzo
//                    // if (model.getMessage().equals("The connection with the server has been lost. Please try to reconnect.")) {
//                        //disconnect();
//
//                    // mi updeta il messaggio di exception nelle varie scene. IN lobby non serve in quanto non abbiamo possibilità di exception
//
//                }
//                case "endGame" -> showEndGameScene();
//
//                //TODO: parte di riconnessione
//            }

        });
    }




    //TODO: per disconnessione
    @Override
    public void disconnect() {

    }




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


        //=====================================//
        //Labels
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



        //==============================================//
        //TextFields
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


        //==============================================//
        //Buttons
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
            String insertedNickname = nicknameField.getText().trim().toLowerCase();
            String insertedIp = ipField.getText().trim();
            String insertedPort = portField.getText().trim();

            if (insertedNickname.isEmpty()) {
                loginErororLabel.setText("Insert a valid Nickname");
                return;
            }

            String netProtocol = connectionTypeBox.getValue().toLowerCase();
            connect(insertedNickname, netProtocol, insertedIp, insertedPort); // we call connect
            System.out.println("connect button clicked with: " + netProtocol + ", " + insertedIp + ", " + insertedPort);

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



    public void updateLogin() {

        if (updateType.equals("exception"))
            loginErororLabel.setText(modelReference.getMessage());
        else
            showGameChoiceScene();
    }



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
            // bug: con un certo nickname homePlayer non viene inizializzato
            String insertedNickname = modelReference.getHomePlayerNickname();
            String totemColorChoice = totemColorBox.getValue().toString().toLowerCase();
            int numPlayersChoice = playersNumberBox.getValue();

            createGame(insertedNickname, numPlayersChoice, totemColorChoice);
        });

        joinGameButton.setOnAction(event -> {

            String gameIdText = idField.getText().trim();
            int gameId;

            try {
                gameId = Integer.parseInt(gameIdText);
            } catch (NumberFormatException e) {
                gameChoiceErrorLabel.setText("Please enter a valid numeric game ID.");
                return;
            }

            String insertedNickname = modelReference.getHomePlayerNickname();
            String totemColorChoice = totemColorBox.getValue().toString().toLowerCase();


            joinGame(insertedNickname, gameId, totemColorChoice);

        });

        return root;
    }


    //TODO: gestione colore già preso, devo verificare se marco ha inserito questo check nella gestione nuova
    public void updateChoiceScene(){

        if (gameChoiceScene == null)  // check per gli update che arrivano prima che la scena venga creata, così evitiamo errori di null pointer
            return;

        if(updateType.equals("exception")){
            gameChoiceErrorLabel.setText(modelReference.getMessage());
        } else
            showLobbyScene(); // se la create o join è andata a buon fine passo alla scena successiva (lobby)
    }


    private void showLobbyScene() {
        if (lobbyScene == null) {
            lobbyScene = new Scene(createLobbyScene(), SCENE_WIDTH, SCENE_HEIGHT);
        }

        primaryStage.setTitle("Lobby");
        primaryStage.setScene(lobbyScene);


    }


    private StackPane createLobbyScene() {
        StackPane lobbyRoot = new StackPane();

        ImageView background = new ImageView(getClass().getResource("/background/lobby.png").toExternalForm()); // adding the bg

        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(lobbyRoot.widthProperty());
        background.fitHeightProperty().bind(lobbyRoot.heightProperty());

        lobbyTotemLayer = new Pane();
        lobbyTotemLayer.setPickOnBounds(false);

        updateLobby(); // inserisce i totem dei player presenti

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
        if (lobbyScene== null) return;

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

    // metodo serve per adattare i totem quando andiamo a stringere la finestra, moltiplica il loro ratio per un x e y in base a come viene ridotta la finestra( commento in ita in quanto serve a me)
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
                xRatio = 0.770;
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

        ImageView totem = createAngryTotem(color.toLowerCase());

        box.getChildren().addAll(name, totem);

        return box;
    }



    private void connect(String chosenNickname, String netProtocol, String IP, String port) {
        try {
            int portNumber;
            try {
                 portNumber = Integer.parseInt(port);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Please enter a valid numeric port.");
            }

            this.controllerReference.connect(netProtocol, IP, portNumber, chosenNickname, this);

        } catch (Exception e) {
            Label error = (Label) loginBox.getChildren().getLast();
            error.setText(e.getMessage());
        }
    }


    private void createGame(String chosenNickname, int numPlayers,String totemColor ) throws IllegalArgumentException {

        try {
            controllerReference.createGame(numPlayers, totemColor);
        } catch (Exception e) {
            gameChoiceErrorLabel.setText("A network error occurred while sending your request. Please try again.");
        }
    }


    private void joinGame(String nickname, int gameId, String totemColor) throws IllegalArgumentException {

        try {
            controllerReference.joinGame(gameId, totemColor);
            System.out.println("invio metodo");
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

        gameScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
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

    private VBox createTop() {
        topBox = new VBox(10);
        topBox.setStyle("-fx-background-color: rgb(255,0,0); -fx-padding: 15;");

        serverMessage = new Label();
        serverMessage.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 20; -fx-font-weight: bold;");
        topBox.setAlignment(Pos.CENTER);

        topBox.getChildren().add(serverMessage);

        updateTop(modelReference.getMessage());
        return topBox;
    }

    public void updateTop(String message){
        if (topBox != null) serverMessage.setText(message); //check per gli update che arrivano prima che la scena venga creata, così evitiamo errori di null pointer
    }


    private StackPane createCenter() {
        StackPane centerWrapper = new StackPane();
        centerWrapper.setStyle("-fx-background-color: transparent;");
        ImageView deck =  new ImageView(getClass().getResource("/deck/1.png").toExternalForm());// parto con uno e poi aggiorno con il primo endRound
        deck.setFitWidth(BOARD_CARD_WIDTH);
        deck.setFitHeight(BOARD_CARD_HEIGHT);


        StackPane turnOrderCard = createTurnOrderCardStack();



        ArrayList<Character> trackCards = modelReference.getOffersPath();

        //CenterGameScene
        VBox centerBox = new VBox(25);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setStyle("-fx-padding: 30;");



        HBox topDeckable = createCardRow(); // top deckable row
        HBox topBuilding = createCardRow(); // top building row
        HBox bottomDeckable = createCardRow(); // bottom deckable row
        HBox bottomBuilding = createCardRow(); // bottom building row
        offersPathBox = createCardRow(); // turnordercard + offersPath



        topCenterBox = new HBox(8);
        topCenterBox.getChildren().addAll(topDeckable, topBuilding);
        bottomCenterBox = new HBox(8);
        bottomCenterBox.getChildren().addAll(bottomDeckable, bottomBuilding);


        //creazione dell'offersPath = turnordercard + trackcards
        offersPathBox.getChildren().addAll(deck,turnOrderCard); // we add the turn order card to the row4
        for (char name : trackCards) {
            StackPane trackCard = createTrackCard(name);
            offersPathBox.getChildren().add(trackCard);
        }

        centerBox.getChildren().addAll(topCenterBox,offersPathBox,bottomCenterBox); // we add the 4 rows of the board, the offersPath and the deck to the centerBoard
        centerWrapper.getChildren().add(centerBox); // we add the vbox created to the stackPane
        updateBoard(); // we update all the boxes with the cards from the board


        //metodi di ridensionamento. Da controllare meglio dopo
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
        // occupa tutto lo stackpane( hanno al stesa dimensione)
        turnOrderCard.setFitWidth(BOARD_CARD_WIDTH);
        turnOrderCard.setFitHeight(BOARD_CARD_HEIGHT);
        turnOrderCard.setPreserveRatio(false);
        turnOrderCard.setSmooth(true);

        // pane sovrapposto alla turnOrderCard per i totem
        turnOrderCardTotemLayer = new Pane();
        turnOrderCardTotemLayer.setPrefSize(BOARD_CARD_WIDTH, BOARD_CARD_HEIGHT);
        turnOrderCardTotemLayer.setMaxSize(BOARD_CARD_WIDTH, BOARD_CARD_HEIGHT);
        turnOrderCardTotemLayer.setPickOnBounds(false); // è cliccabile solo la parte effettivamente con contenuto
        ; // l'aggiungo alla lista attributo in modo che possa ripulirlo

        addTotemsOnTurnOrderCard(turnOrderCardTotemLayer);

        stack.getChildren().addAll(turnOrderCard,turnOrderCardTotemLayer);

        return stack;
    }



    private void addTotemsOnTurnOrderCard(Pane totemLayer) {

        for (String nickname : modelReference.getPlayerOrderInTurnOrderCard()) {
            if (nickname == null) continue; // se è null vuol dire che in quella posizione non c'è nessun player, quindi salto
            ClientPlayer player = modelReference.getPlayerByNickname(nickname);
            String color = player.getTotemColor().toLowerCase();

            int pos = modelReference.getPlayerPositionInTurnOrderCard(player.getNickname()); // we get the playerOrder to understand in which slot of the turn order card we have to put the totem

            // we create the image totem
            ImageView totem = createSmallTotem(color);
            positionTotemOnTurnOrderCard(totem, pos); // we position the totem in the right slot based on the player position in the turn order card

            // only the totem of the homePlayer will be pickable
            if (player.getNickname().equals(modelReference.getHomePlayerNickname())) {
                totem.setCursor(Cursor.HAND);

                totem.setOnMouseClicked(event -> {
                    totemClicked = true;
                    updateTop("Totem selected. Choose a track card.");
                });
            }

            // abbiamo un totemLayer sovrapposto alla turnOrderCard, i totem verranno aggiunti in base alla posizione del player sulla turnOrderCard con il ratio fornito da positiontotemOnTurnOrderCard
            totemLayer.getChildren().add(totem);
        }
    }


    private void positionTotemOnTurnOrderCard(ImageView totem, int index) {

        PointRatio pos = getTurnOrderSlotPosition(index);
        //RATIO PER POSIZIONARE I TOTEM IN BASE ALLA LARGHEZZA DELLA TURN ORDER CARD, MOLTIPLICANDO IL RATIO PER LA LARGHEZZA OTTENIAMO LA POSIZIONE IN PIXEL, POI SOTTRAIAMO LA META' DELLA LARGHEZZA DEL TOTEM PER CENTRARLO NELLO SLOT
        double x = BOARD_CARD_WIDTH * pos.x() - SMALL_TOTEM_WIDTH / 2; // formula usata per calcolare al meglio il ratio
        double y = BOARD_CARD_HEIGHT * pos.y() - SMALL_TOTEM_HEIGHT / 2;

        totem.setLayoutX(x); // mette il totem a x pixel da sinistra del bordo
        totem.setLayoutY(y); // mette il totem a y pixel da sopra del bordo  (più è alto più va in basso)
    }


    private ImageView createSmallTotem(String color) {
        System.out.println(color);
        ImageView totem = new ImageView(getClass().getResource("/totem/" + color + ".png").toExternalForm());

        totem.setFitWidth(SMALL_TOTEM_WIDTH);
        totem.setFitHeight(SMALL_TOTEM_HEIGHT);
        totem.setPreserveRatio(true);
        totem.setSmooth(true);

        return totem;
    }


    private record PointRatio(double x, double y) {}


    // it returns the ratio needed based on the position a totem should have on the turnOrderCard to be rightfully center based on playerPosition
    private PointRatio getTurnOrderSlotPosition(int index) {
        return switch (index) {
            case 0 -> new PointRatio(0.50, 0.30);  // x va bene y è da lavorarci // per foto non ritagliate va bene dopo aver fatto un po di prove
            case 1 -> new PointRatio(0.50, 0.48);
            case 2 -> new PointRatio(0.50, 0.66);
            case 3 -> new PointRatio(0.50, 0.79);
            case 4 -> new PointRatio(0.50, 0.92);
            default -> throw new IllegalStateException("Unexpected value: " + index);
        };
    }


    private void addTotemOnTrackCard(Pane totemLayer, String color) {
        ImageView totem = createSmallTotem(color);

        // stesso principio di quanto visto per la turnOrderCard
        PointRatio pos = new PointRatio(0.50, 0.30);  // x,y

        double x = BOARD_CARD_WIDTH * pos.x() - SMALL_TOTEM_WIDTH / 2;
        double y = BOARD_CARD_HEIGHT * pos.y() - SMALL_TOTEM_HEIGHT / 2;

        totem.setLayoutX(x);
        totem.setLayoutY(y);

        totem.setMouseTransparent(true);

        totemLayer.getChildren().clear();
        totemLayer.getChildren().add(totem);
    }





    private void updateCenterCards(int row, HBox box){
        ArrayList<ArrayList<Integer>> currentBoard = modelReference.getBoard();
        List<Integer> orderedTopRow= currentBoard.get(row).stream().sorted(Comparator.naturalOrder()).toList(); // we order the cards in the row by their id so they are ordered by tipe

        box.getChildren().clear(); // we clear the actual cards in the box
        for (Integer cardId : orderedTopRow) {
            ImageView card = createPickableCard(cardId,row);
            box.getChildren().add(card);
        }
    }


    //TODO: fatto ipotizzando che andiamo a mettere un aggiornamento a null di tutte le trackCard in EndRoun a nulll, o anche meglio proprio quando il player ha finito di pickare
    public void updateoffersPathBox(){

        // updateTurnOrdercard
        turnOrderCardTotemLayer.getChildren().clear(); // we clear the totems on the turn order card, then we will add them again based on the new player position in turn order card after the update of the board
        addTotemsOnTurnOrderCard(turnOrderCardTotemLayer);

        //update trackCards totem
        ArrayList<ClientPlayer> players = new ArrayList<>(modelReference.getOtherPlayers());
        players.add(modelReference.getHomePlayer());
        clearAllTrackCardTotems(); // pulisco tutto e ricreo

        for(ClientPlayer player : players){
            // dando per scontato che se il mio server gestisca bene il fatto che se vado a cliccare in una trackCard già occupata mi lancia un exception
            // e che vado a ripulire l'offersPath almeno in endRound ( o preferibilmente a fine pick, ad gni placement lo ricostruisco
            Character position = player.getPlayerPosition();
            if (position != null) { // ha una posizione sulla trackCard, va fatto altrimenti avrei nullPointerException
                Pane totemLayer = trackCardTotemLayers.get(player.getPlayerPosition());
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

        //update topRow with the index based on the row linked to the hbox
        for(int i =0 ; i<2 ; i++){
            HBox box = (HBox) topCenterBox.getChildren().get(i);
            updateCenterCards(i, box);
        }

        //update BottomRow with the index based on the row linked to the hbox
        for(int i = 0; i<2 ; i++){
            HBox box = (HBox) bottomCenterBox.getChildren().get(i);
            updateCenterCards(i+2, box);
        }

        // deck update:
        if (modelReference.getAge() > 0) { // update fatto solo se ho l'age mi arriva con l'evento di endRound
            ImageView deck = new ImageView(
                    getClass().getResource("/deck/" + modelReference.getAge() + ".png").toExternalForm()
            );

            deck.setFitWidth(BOARD_CARD_WIDTH);
            deck.setFitHeight(BOARD_CARD_HEIGHT);
            deck.setPreserveRatio(false);
            deck.setSmooth(true);


            //TODO: capire se conviene tenere la genstione con removeFirst / addFirst ecc e il lavoro con gli indici. in quanto molto debole, oppure semplciemente andare a salvare le variabili e evitare ciò
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



    private VBox createBottom() {
        VBox bottom = new VBox(8);
        bottom.setStyle("""
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


        bottom.getChildren().addAll(playersTabs, tribeCardsBox);

        updateSelectedTribe();

        return bottom;
    }


    private ToggleButton createTribePlayerButton(String nickname) {
        ToggleButton button = new ToggleButton(nickname);
        button.setToggleGroup(tribeToggleGroup);

        button.setStyle("""
        -fx-background-color: rgba(255,255,255,0.15);
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 10;
        -fx-padding: 6 14 6 14;
        -fx-border-color: rgba(255,255,255,0.35);
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

        for (String playerName : players) {
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

        Map<StatType, Label> labels = new HashMap<>();  // here we create the map linked to the player

        addStat(grid, labels, StatType.FOOD, getClass().getResource("/stats/food.png").toExternalForm(), 0, 0);
        addStat(grid, labels, StatType.AXE, getClass().getResource("/stats/buildingDiscount.png").toExternalForm(), 0, 2);
        addStat(grid, labels, StatType.PRESTIGE, getClass().getResource("/stats/prestige.png").toExternalForm(), 0, 4);

        addStat(grid, labels, StatType.STARS, getClass().getResource("/stats/stars.png").toExternalForm(), 1, 0);
        addStat(grid, labels, StatType.BOWL, getClass().getResource("/stats/gatheringDiscount.png").toExternalForm(), 1, 2);
        addStat(grid, labels, StatType.COLOUR, getClass().getResource("/colour/"+ colour +".png").toExternalForm(), 1, 4);

        playerStatLabels.put(playerName, labels);  // here we put the updated map linked to the player in the map with all the playerStats

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

        ImageView icon = new ImageView(imagePath); // here we create the image with the simbol of the stat we are adding to the grid
        icon.setFitWidth(34);
        icon.setFitHeight(34);
        icon.setPreserveRatio(true);
        icon.setSmooth(true);
        if(!(type == StatType.COLOUR)){
            Label valueLabel = new Label("0");  // here we put the value of the stat initialized to 0
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
        grid.add(icon, col, row);  // here we add the image to the grid

    }



    public void updatePlayerStats() {

        ArrayList<ClientPlayer> clientPlayers = new ArrayList<>(modelReference.getOtherPlayers());
        clientPlayers.add(modelReference.getHomePlayer());

        for ( ClientPlayer p : clientPlayers) {

            Map<StatType, Label> labels = playerStatLabels.get(p.getNickname());
             {
                labels.get(StatType.FOOD).setText(String.valueOf(p.getNumFood()));
                labels.get(StatType.PRESTIGE).setText(String.valueOf(p.getNumPrestigePoints()));
                labels.get(StatType.STARS).setText(String.valueOf(p.getNumStars()));
                labels.get(StatType.AXE).setText(String.valueOf(p.getTotalBuildingDiscount()));
                labels.get(StatType.BOWL).setText(String.valueOf(p.getTotalGatheringDiscount()));
            }

        }

    }


        // TODO: possiamo rendere le carte evento non prendibili sfruttando l'intervallo degli id se vogliamo. Mi sembra meglio come cosa
        private ImageView createPickableCard(int id, int row){
            ImageView card = new ImageView(getClass().getResource("/cardsFront/" + id + ".jpg").toExternalForm());
            card.setFitWidth(BOARD_CARD_WIDTH);
            card.setFitHeight(BOARD_CARD_HEIGHT);
            card.setPreserveRatio(true);
            card.setSmooth(true);
            addHoverEffect(card);


            card.setCursor(Cursor.HAND);

            card.setOnMouseClicked(event -> {
                pickCard(modelReference.getHomePlayerNickname(), id, row);
            });
            return card;
        }


        private void pickCard(String nickname, int cardId, int row){
            try {
                controllerReference.pickCard(cardId);
            } catch (Exception e) {
                throw new RuntimeException("[Client] A network error occurred while sending your request\nDetails: " + e.getMessage());
            }
        }


    private ImageView createTribeCard(int id) {
        ImageView card = new ImageView(getClass().getResource("/cardsFront/" + id + ".jpg").toExternalForm()
        );
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
        trackCardTotemLayers.put(name,trackCardtotemLayer); // lo aggiungo alla mappa degli strati dei totem delle track card per poterli aggiornare quando arriva l'evento di piazzamento del totem

        // importante: il layer dei totem non deve bloccare il click sulla trackCard
        trackCardtotemLayer.setMouseTransparent(true);

        trackCard.getChildren().addAll(cardImage, trackCardtotemLayer);

        trackCard.setCursor(javafx.scene.Cursor.HAND);
        addHoverEffect(trackCard);

        trackCard.setOnMouseClicked(event -> {
            if (!totemClicked) {
                updateTop("Select your totem first.");
                return;
            }


            // TODO: gestione se la trackCard è già occupata per evitare la comunicazione con il server
            System.out.println("Clicked track card: " + name);
            try {
                controllerReference.placeTotem(name);
            } catch (Exception e) {
                throw new RuntimeException("[Client] A network error occurred while sending your request\nDetails: " + e.getMessage());
            }

            totemClicked = false; // se il placement va a buon fine , metto totemClicked a false( in quanto è legato al click fatto nella trackCard e ora il totem è stato posizionato bene)
        });
        return trackCard;
    }



    private ImageView createAngryTotem(String totemColor) {
        ImageView totem = new ImageView(
                getClass().getResource("/totemLobby/" + totemColor + ".png").toExternalForm()
        );

        if(updateType.equals("endGame"))
            totem.setFitWidth(END_TOTEM_WIDTH);
        else
            totem.setFitWidth(LOBBY_TOTEM_WIDTH);

        totem.setPreserveRatio(true);
        totem.setSmooth(true);

        return totem;
    }



    private void createEndGameScene() {
        StackPane root = new StackPane();

        ImageView background = new ImageView(getClass().getResource("/background/endGame.png").toExternalForm());


        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());

        endGameTotemLayer = new Pane();
        endGameTotemLayer.setPickOnBounds(false);

        root.getChildren().addAll(background, endGameTotemLayer);

        endGameScene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
    }


    public void showEndGameScene() {
        if (endGameScene == null) {
            createEndGameScene();
        }

        primaryStage.setTitle("Fine partita");
        primaryStage.setScene(endGameScene);

        updateEndGamePodium();
    }


    private void updateEndGamePodium() {

        endGameTotemLayer.getChildren().clear();

        ArrayList<String> ranking = modelReference.getFinalRank();

        for (int i = 0; i < Math.min(3, ranking.size()); i++) { // uso il minimo in quanto se sono in due il podio sarà solo da due
            ClientPlayer player = modelReference.getPlayerByNickname(ranking.get(i));


            ImageView totem = createAngryTotem(
                    player.getTotemColor().toLowerCase()
            );
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

            placeTotemOnPodium(totemBox, i);

            endGameTotemLayer.getChildren().add(totemBox);

        }
    }




    private void placeTotemOnPodium(VBox totemBox, int rankingPosition) {
        double xRatio;
        double yRatio;

        switch (rankingPosition) {
            case 0 -> { // primo posto, centro
                xRatio = 0.500;
                yRatio = 0.330;
            }
            case 1 -> { // secondo posto, sinistra
                xRatio = 0.275;
                yRatio = 0.365;
            }
            case 2 -> { // terzo posto, destra
                xRatio = 0.720;
                yRatio = 0.400;
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
}