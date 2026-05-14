package it.polimi.ingsw.am14.view;

import it.polimi.ingsw.am14.clientModel.ClientModel;
import it.polimi.ingsw.am14.clientModel.ClientPlayer;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import it.polimi.ingsw.am14.network.EntryPointFactory;
import it.polimi.ingsw.am14.network.VirtualServer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.io.IOException;
import java.util.*;

// TODO: problemi con il clientmodel in fase di login

public class GUIView extends Application implements ViewType {

    // sono tutti a null fino a quando non verranno inizializzati
    private ClientModel modelReference;
    private VirtualServer server;
    private Stage primaryStage;

    //TODO: fatto per prova al posto del getter sul model

    private Scene gameChoiceScene;
    private Scene lobbyScene;
    private Scene gameScene;

    private VBox loginBox;
    private VBox initialChoiceBox;

    private Label serverMessage;

    private VBox centerBox;
    private VBox topBox;
    private ImageView deckView;

    private VBox playersStatsBox;
    private Pane lobbyTotemLayer;
    private HBox homeTribeBox;
    private final Map<String, TilePane> awayTribePanes = new HashMap<>();

    private enum StatType {
        FOOD,
        AXE,
        PRESTIGE,
        STARS,
        BOWL
    }

    private final Map<String, Map<StatType, Label>> playerStatLabels = new HashMap<>();


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        Scene loginScene = new Scene(createNicknameRoot(), 1400, 900);

        primaryStage.setTitle("Login");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    @Override
    public void run() throws IOException {
        // Non chiamare start() qui.
        // JavaFX chiama automaticamente start(Stage primaryStage) dopo launch().
    }

    @Override
    public void update(ClientModel model, String eventChoice) {
        this.modelReference = model;

        System.out.println("GUI update received: " + eventChoice);
        System.out.println("message = " + model.getMessage());


        javafx.application.Platform.runLater(() -> {
        switch (eventChoice) {
            case "login" -> updateLogin();
            case "gameChoice" -> updateChoiceScene();
            case "lobbyUpdate" -> updateLobbyTotems();
            case "gameStart" -> gameCreation();
            case "pickUpdate" -> {
                updateBoard();
                updateTribes();
                updateTop();
                updatePlayerStats();
            }
            case "positionUpdate" -> {
                updateBoard();
                updateTop();
            }
            case "endRound", "endGame" -> {
                updateBoard();
                updateTop();
                updatePlayerStats();
            }
            //case "gameOver" -> showEndGameScene(); da fare ancora
            case "exception" -> {
                    updateTop(); // controllo di esistenza della scena nei singoli metodi
                    updateChoiceScene();
                    updateLogin();
                // verificare con Marco e Lorenzo
                // if (model.getMessage().equals("The connection with the server has been lost. Please try to reconnect.")) {
                    //disconnect();

                // mi updeta il messaggio di exception nelle varie scene. IN lobby non serve in quanto non abbiamo possibilità di exception

            }
            default -> updateLogin();

            //TODO: parte di riconnessione
        }
        this.modelReference = model;
        });
    }

    @Override
    public void disconnect() {

    }


    private BorderPane createNicknameRoot() {
        BorderPane root = new BorderPane();

        root.setStyle("""
        -fx-background-color: linear-gradient(to bottom, #2b1230, #5b1630);
        -fx-padding: 30;
                    """);

        Label title = new Label("Mesos");
        title.setStyle("""
        -fx-text-fill: white;
        -fx-font-size: 46;
        -fx-font-weight: bold;
""");

        Label subtitle = new Label("Inserisci il tuo nickname");
        subtitle.
                setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 20;
    """);

        TextField nicknameField = new TextField();
        nicknameField.setPromptText("Nickname");
        nicknameField.setMaxWidth(300);

        ComboBox<String> connectionTypeBox = new ComboBox<>();
        connectionTypeBox.getItems().addAll("Socket", "RMI");
        connectionTypeBox.setValue("Socket");
        connectionTypeBox.setMaxWidth(300);

        Label connectionLabel = new Label("Connection type");
        connectionLabel.setStyle(
                """
        -fx-text-fill: white;
        -fx-font-size: 16;
    """);

        Label serverIpAddress = new Label("Insert server IP address");
        serverIpAddress.setStyle("""
    -fx-text-fill: white;
    -fx-font-size: 16;
""");

        TextField ipField = new TextField();
        ipField.setPromptText("ipField");
        ipField.setMaxWidth(300);


        Label serverPort = new Label("Insert server port");
        serverPort.setStyle("""
    -fx-text-fill: white;
    -fx-font-size: 16;
""");

        TextField portField = new TextField();
        portField.setPromptText("serverPort");
        portField.setMaxWidth(300);


        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ffb3b3; -fx-font-size: 14;");

        Button connectButton = new Button("Login");
        connectButton.setStyle(
                """
        -fx-font-size: 16;
        -fx-font-weight: bold;
        -fx-padding: 8 22 8 22;
    """);


        connectButton.setOnAction(event -> {
            String insertedNickname = nicknameField.getText().trim();
            String insertedIp = ipField.getText().trim();
            String insertedPort = portField.getText().trim();

            if (insertedNickname.isEmpty()) {
                errorLabel.setText("Inserisci un nickname valido.");
                return;
            }

            String netProtocol = connectionTypeBox.getValue().toLowerCase();

            connect(insertedNickname, netProtocol, insertedIp, insertedPort);
            System.out.println("Trying connect:");
            System.out.println("protocol = " + netProtocol);
            System.out.println("ip = " + insertedIp);
            System.out.println("port = " + insertedPort);
            System.out.println("nickname = " + insertedNickname);


        });

        nicknameField.setOnAction(event -> connectButton.fire());

        loginBox = new VBox(12);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setMaxWidth(420);
        loginBox.setStyle("""
    -fx-background-color: rgba(255, 255, 255, 0.12);
    -fx-background-radius: 16;
    -fx-padding: 35;
""");

        nicknameField.setPrefWidth(300);
        nicknameField.setMaxWidth(300);

        ipField.setPrefWidth(210);
        ipField.setMaxWidth(210);

        portField.setPrefWidth(80);
        portField.setMaxWidth(80);

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
                errorLabel
        );

        root.setCenter(loginBox);
        return root;
    }


    private void showGameChoiceScene() {
        if (gameChoiceScene == null) {
            gameChoiceScene = new Scene(createGameChoiceRoot(), 1400, 900);
        }

        primaryStage.setTitle("Game Menu");
        primaryStage.setScene(gameChoiceScene);
    }


    private void updateLogin() {
        if (modelReference == null || loginBox == null) return;

        if (modelReference.getHomePlayer() != null) {
            showGameChoiceScene();
        } else {
            Label error = (Label) loginBox.getChildren().getLast();
            error.setText(modelReference.getMessage());
        }
    }



    private BorderPane createGameChoiceRoot() {
        BorderPane root = new BorderPane();

        root.setStyle(
                """
        -fx-background-color: linear-gradient(to bottom, #2b1230,
                #5b1630);
                    -fx-padding: 30;
    """);

        Label title = new Label("HELLO WELCOME TO MESOS");
        title.
                setStyle("""

                -fx-text-fill: white;
                -fx-font-size: 38;
                -fx-font-weight: bold;
    """);

        VBox joinBox = new VBox(10);
        VBox createBox = new VBox(10);

        Button createGameButton = new Button("Create Game");
        Button joinGameButton = new Button("Join Game");

        Label idLabel = new Label("GAME ID");
        idLabel.setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 16;
 """);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ffb3b3; -fx-font-size: 14;");


        ComboBox<Integer> playersNumberBox = new ComboBox<>();
        playersNumberBox.getItems().addAll(2,3,4,5);
        playersNumberBox.setValue(2);
        playersNumberBox.setMaxWidth(300);

        ComboBox<TotemColor> totemBox = new ComboBox<>();
        totemBox.getItems().addAll(TotemColor.RED, TotemColor.BLUE, TotemColor.PURPLE, TotemColor.WHITE, TotemColor.YELLOW);
        totemBox.setValue(TotemColor.RED);
        totemBox.setMaxWidth(300);


        TextField idField = new TextField();
        idField.setPromptText("Game ID");
        idField.setMaxWidth(300);

        createGameButton.setPrefWidth(240);
        joinGameButton.setPrefWidth(240);

        createGameButton.
                setStyle("""

                -fx-font-size: 17;
                    -fx-font-weight: bold;
                    -fx-padding: 10 20 10 20;
    """);

        joinGameButton.setStyle(
                """
        -fx-font-size: 17;
        -fx-font-weight: bold;
        -fx-padding: 10 20 10 20;
    """);


        createGameButton.setOnAction(event -> {
            if (totemBox.getValue() == null || playersNumberBox.getValue() == null) {
                errorLabel.setText("Please fill in the missing fields to create your game.");
                return;
            }

            String insertedNickname = modelReference.getHomePlayer().getNickname();
            String totemColorChoice = totemBox.getValue().toString();
            int numPlayersChoice = playersNumberBox.getValue();

            createGame(insertedNickname, numPlayersChoice, totemColorChoice);
        });


        joinGameButton.setOnAction(event -> {

            // check phase;
            if (totemBox.getValue() == null) {
                errorLabel.setText("Please select a totem color.");
                return;
            }

            String gameIdText = idField.getText().trim();
            if (gameIdText.isEmpty()) {
                errorLabel.setText("Please enter a game ID.");
                return;
            }

            int gameId;
            try {
                gameId = Integer.parseInt(gameIdText);
            } catch (NumberFormatException e) {
                errorLabel.setText("Please enter a valid numeric game ID.");
                return;
            }

            String insertedNickname = modelReference.getHomePlayer().getNickname();
            String totemColorChoice = totemBox.getValue().toString();

            joinGame(insertedNickname, gameId, totemColorChoice);
        });

        joinBox.getChildren().addAll(
                joinGameButton,
                idLabel,
                idField
        );
        createBox.getChildren().addAll(
                createGameButton,
                playersNumberBox
        );

        initialChoiceBox = new VBox(22);
        initialChoiceBox.setAlignment(Pos.CENTER);
        initialChoiceBox.getChildren().addAll(
                title,
                createBox,
                joinBox,
                totemBox,
                errorLabel
        );

        root.setCenter(initialChoiceBox);

        return root;
    }



    //TODO: gestione colore già preso, devo verificare se marco ha inserito questo check nella gestione nuova
    private void updateChoiceScene(){

        if (gameChoiceScene == null)  // check per gli update che arrivano prima che la scena venga creata, così evitiamo errori di null pointer
            return;

        if(modelReference.getMessage().equals("Game with the specified ID does not exist")){ // Id non esistente --> la join non è andata a buon fine
            Label error = (Label) initialChoiceBox.getChildren().getLast();
            error.setText("Il game ID inserito non esiste, controlla di averlo scritto correttamente.");

        } else
            showLobbyScene(); // se la create o join è andata a buon fine passo alla scena successiva (lobby)
    }


    private void showLobbyScene() {
        if (lobbyScene == null) {
            lobbyScene = new Scene(createLobbyRoot(), 1400, 900);
        }

        primaryStage.setTitle("Lobby");
        primaryStage.setScene(lobbyScene);

        updateLobbyTotems();
    }


    private StackPane createLobbyRoot() {
        StackPane lobbyRoot = new StackPane();

        ImageView background = new ImageView("file:C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/background/lobby.png"); // adding the bg

        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(lobbyRoot.widthProperty());
        background.fitHeightProperty().bind(lobbyRoot.heightProperty());

        lobbyTotemLayer = new Pane();
        lobbyTotemLayer.setPickOnBounds(false);

        VBox topOverlay = createLobbyLabels();

        StackPane.setAlignment(topOverlay, Pos.TOP_CENTER);

        lobbyRoot.getChildren().addAll(background, lobbyTotemLayer, topOverlay);

        return lobbyRoot;
    }



    private VBox createLobbyLabels() {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding: 25;");

        Label title = new Label("MESOS LOBBY");
        title.setStyle("""
        -fx-text-fill: white;
        -fx-font-size: 42;
        -fx-font-weight: bold;
        -fx-effect: dropshadow(gaussian, black, 8, 0.8, 2, 2);
    """);

        Label lobbyGameIdLabel = new Label("Game ID: -");
        lobbyGameIdLabel.setStyle("""
        -fx-text-fill: #ffd36b;
        -fx-font-size: 18;
        -fx-font-weight: bold;
        -fx-effect: dropshadow(gaussian, black, 6, 0.8, 1, 1);
    """);

        Label lobbyMessageLabel = new Label("Waiting for players...");
        lobbyMessageLabel.setStyle("""
        -fx-text-fill: white;
        -fx-font-size: 16;
        -fx-effect: dropshadow(gaussian, black, 6, 0.8, 1, 1);
    """);

        box.getChildren().addAll(title, lobbyGameIdLabel, lobbyMessageLabel);

        return box;
    }


    private void updateLobbyTotems() {
        if (lobbyTotemLayer == null || modelReference == null) return;

        lobbyTotemLayer.getChildren().clear();

        ArrayList<ClientPlayer> players = new ArrayList<>();

        players.add(modelReference.getHomePlayer());

        if (modelReference.getOtherPlayers() != null) {
            players.addAll(modelReference.getOtherPlayers());
        }

        for (ClientPlayer player : players) {
            VBox totemBox = createLobbyTotemBox(player);
            placeTotemByColor(totemBox, player.getTotemColor());
            lobbyTotemLayer.getChildren().add(totemBox);
        }
    }

    // metodo serve per adattare i totem quando andiamo a stringere la finestra, moltiplica il loro ratio per un x e y in base a come viene ridotta la finestra( commento in ita in quanto serve a me)
    private void placeTotemByColor(VBox node, String color) {
        double xRatio;
        double yRatio;

        switch (color) {
            case "red"-> {
                xRatio = 0.08;
                yRatio = 0.38;
            }
            case "yellow" -> {
                xRatio = 0.24;
                yRatio = 0.36;
            }
            case "blue" -> {
                xRatio = 0.43;
                yRatio = 0.38;
            }
            case "purple" -> {
                xRatio = 0.61;
                yRatio = 0.37;
            }
            case "white"-> {
                xRatio = 0.78;
                yRatio = 0.36;
            }
            default -> {
                xRatio = 0.45;
                yRatio = 0.38;
            }
        }

        node.layoutXProperty().bind(lobbyTotemLayer.widthProperty().multiply(xRatio));
        node.layoutYProperty().bind(lobbyTotemLayer.heightProperty().multiply(yRatio));
    }





    private VBox createLobbyTotemBox(ClientPlayer player) {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);

        ImageView totem = createLobbyTotem(player.getTotemColor());

        Label name = new Label(player.getNickname());
        name.setStyle("""
        -fx-text-fill: white;
        -fx-font-size: 16;
        -fx-font-weight: bold;
        -fx-effect: dropshadow(gaussian, black, 6, 0.8, 1, 1);
    """);

        box.getChildren().addAll(totem, name);

        return box;
    }



    private void connect(String chosenNickname, String netProtocol, String IP, String port) {
        try {
            this.server = EntryPointFactory.connect(
                    new String[]{netProtocol, IP, port, chosenNickname},
                    this
            );

            System.out.println("Connection successful, server proxy = " + server);

        } catch (Exception e) {
            Label error = (Label) loginBox.getChildren().getLast();
            error.setText(e.getMessage());
        }
    }


    private void createGame(String chosenNickname, int numPlayers,String totemColor ) throws IllegalArgumentException {

        try {
            server.createGame(chosenNickname, numPlayers,totemColor);
        } catch (Exception e) {
            Label error = (Label) initialChoiceBox.getChildren().getLast();
            error.setText("A network error occurred while sending your request. Please try again.");
        }
    }


    private void joinGame(String nickname, int gameId, String totemColor) throws IllegalArgumentException {

        try {
            server.joinGame(nickname, gameId, totemColor);
        } catch (Exception e) {
            Label error = (Label) initialChoiceBox.getChildren().getLast();
            error.setText("A network error occurred while sending your request. Please try again.");
        }

    }



    private void gameCreation() {

        if (gameScene == null) {
            createGameScene();
        }

        showGameScene();
    }









    // ============================================================
    // GAME SCENE
    // ============================================================

    private void createGameScene() {
        BorderPane root = new BorderPane();

        Image backgroundImage = new Image("file:C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/background/game.jpeg");

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

        gameScene = new Scene(root, 1400, 900);
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
        serverMessage.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 10;");

        topBox.getChildren().add(serverMessage);

        updateTop();
        return topBox;
    }

    private void updateTop(){
        if (topBox != null) serverMessage.setText(modelReference.getMessage()); //check per gli update che arrivano prima che la scena venga creata, così evitiamo errori di null pointer
    }


    private StackPane createCenter() {
        StackPane centerWrapper = new StackPane();
        centerWrapper.setStyle("-fx-background-color: transparent;");
        ImageView deck =  new ImageView("file:C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/deck/1.png"); // check
        ImageView turnOrderCard = new ImageView(
                "file:C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/turnOrderCards/"
                        + modelReference.getNumPlayers()
                        + ".png"
        );
        ArrayList<Character> trackCards = modelReference.getOffersPath();

        centerBox = new VBox(25);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setStyle("-fx-padding: 30;");

        HBox row0 = createCardRow(); // top deckable row
        HBox row1 = createCardRow(); // top building row
        HBox row2 = createCardRow(); // bottom deckable row
        HBox row3 = createCardRow(); // bottom building row
        HBox offersPath = createCardRow(); // turnordercard + offersPath

        //creazione dell'offersPath = turnordercard + trackcards
        offersPath.getChildren().add(turnOrderCard); // we add the turn order card to the row4
        for ( char name : trackCards){
            ImageView trackCard = createTrackCard(name);
            offersPath.getChildren().add(trackCard);
        }

        centerBox.getChildren().addAll(row0, row1, row2, row3, offersPath, deck); // we add the 4 rows of the board, the offersPath and the deck to the centerBoard
        centerWrapper.getChildren().add(centerBox); // we add the vbox created to the stackPane

        updateBoard(); // we update all the boxes with the cards from the board
        return centerWrapper;

    }


    private void updateBoard() {

        ArrayList<ArrayList<Integer>> currentBoard = modelReference.getBoard();

        for (int i = 0; i < centerBox.getChildren().size() - 2; i++) { // -1 in quanto la board riguarda solo le carte delle prime 4 row ( non riguarda la track card e turn order card)
            HBox box = (HBox) centerBox.getChildren().get(i); // fatto cast forzato perché sappiamo che i figli di centerBoard sono tutti HBox
            box.getChildren().clear(); // we clear the actual cards in the box

            for (Integer cardId : currentBoard.get(i)) {
                ImageView card = createDeckCard(cardId);
                box.getChildren().add(card);
            }
            // for each Hbox of the centerBoard we update its cards
        }

        centerBox.getChildren().removeLast();
        ImageView deck = new ImageView("file:C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/deck/" + modelReference.getAge() + ".png");
        centerBox.getChildren().add(deck);// we update the deck with the image of the  new era

    }


    private HBox createCardRow() {
        HBox row = new HBox(10); // we use 10 pixel of space between the cards
        row.setAlignment(Pos.CENTER); // this function is needed to center the cards in the row. So the card will spawn from the center to the left.
        row.setStyle("""
        -fx-background-color: rgba(255, 255, 255, 0.18);  \s
        -fx-background-radius: 14;
        -fx-padding: 7.5;
   \s""");
        // this is the color the hobox
        // the radius is for the rounded corners and the padding is for the space between the border and the content of the hbox
        // the padding is the space between the border and the content of the hbox, so if you want to have more space between the border and the cards you can increase the padding value.

        return row;
    }




    private VBox createBottom() {

        // the bottom of the screen is divided in two parts, the first part is for the home player and the second part is for the other players.
        // The home player is a vbox which contains a label ( with the nickname of the player and a scroll pane with the card of the tribe)
        // For the other players we have a hbox where we have a vbox which contains a label ( with the nickname of the player) and a tilepane with the cards of the tribe, we have a tilepane because we want to have a grid of cards and not a row of cards.

        VBox bottom = new VBox(10);
        bottom.setStyle("""
        -fx-background-color: rgba(60, 20, 40, 0.55);
        -fx-padding: 10;
    """);

        VBox myPlayerPanel = createMyPlayerPanel();
        HBox otherPlayersPanel = new HBox(10);
        otherPlayersPanel.setAlignment(Pos.CENTER_LEFT);


        for (ClientPlayer player : modelReference.getOtherPlayers()) {
            otherPlayersPanel.getChildren().add(createOtherPlayerPanel(player.getNickname()));
        }

        bottom.getChildren().addAll(myPlayerPanel, otherPlayersPanel);

        return bottom;
    }


    private VBox createMyPlayerPanel() {
        VBox panel = new VBox(8);
        panel.setStyle("""
        -fx-background-color: rgba(255, 180, 180, 0.25);
        -fx-background-radius: 10;
        -fx-border-color: #5b1630;
        -fx-border-width: 3;
        -fx-border-radius: 10;
        -fx-padding: 10;
    """);

        Label name = new Label(modelReference.getHomePlayer().getNickname());
        name.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");


        homeTribeBox = new HBox(8);
        homeTribeBox.setAlignment(Pos.CENTER_LEFT);
        updateHomeTribe(homeTribeBox);

        ScrollPane scroll = new ScrollPane(homeTribeBox);
        scroll.setFitToHeight(true); // putting it to true , the scrollpane will adjust its height to the content, so it will not have a fixed height and it will grow as the content grows
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // it will show the scrollbar only when the content exceeds the width of the scrollpane.
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // we don't want a vertical scrollbar
        scroll.setPrefHeight(120);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        panel.getChildren().addAll(name, scroll);

        return panel;
    }


    private VBox createOtherPlayerPanel(String playerName) {
        VBox panel = new VBox(6);
        panel.setPrefWidth(320);
        panel.setMaxHeight(120);

        panel.setStyle("""
        -fx-background-color: rgba(255, 255, 255, 0.18);
        -fx-background-radius: 10;
        -fx-border-color: rgba(255, 255, 255, 0.45);
        -fx-border-radius: 10;
        -fx-padding: 8;
    """);

        Label name = new Label(playerName);
        name.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        TilePane tribePreview = new TilePane();
        tribePreview.setHgap(5);
        tribePreview.setVgap(5);
        tribePreview.setPrefColumns(8);
        awayTribePanes.put(playerName, tribePreview);

        updateAwayTribe(tribePreview, playerName);
        panel.getChildren().addAll(name, tribePreview);

        return panel;
    }



    // update method which we call every time we have an update from a pickEvent
    private void updateTribes() {
        if (homeTribeBox != null) {
            updateHomeTribe(homeTribeBox);
        }

        for (Map.Entry<String, TilePane> entry : awayTribePanes.entrySet()) {
            updateAwayTribe(entry.getValue(), entry.getKey());
        }
    }


    private void updateHomeTribe (HBox tribe){

        tribe.getChildren().clear(); // cleaning the tribe
        ArrayList<Integer> playerTribe = new ArrayList<>(modelReference.getHomePlayer().getPlayerTribe().stream().sorted(Integer::compareTo).toList());
        // we sort the tribe of the player with ascending order, so the cards will be displayed for a better visualization

        for (Integer cardId : playerTribe) {
            ImageView card = createHomeTribeCard(cardId);
            tribe.getChildren().add(card);
        }
        // adding the updated tribe

    }


    private void updateAwayTribe ( TilePane tribe, String playerName){

        tribe.getChildren().clear(); // cleaning the tribe

        List<Integer> playerTribe;
        playerTribe = modelReference.getOtherPlayers().stream().filter(player -> player.getNickname().equals(playerName)).findFirst().orElseThrow().getPlayerTribe();
         // adding the updated tribe of the other players, we iterate over the list of the other players and for each player we get its tribe and we add the cards to the tilepane

        playerTribe.stream().sorted(Integer::compareTo).forEach(cardId -> {;
            ImageView card = createAwayTribeCard(cardId);
            tribe.getChildren().add(card);
        });
        // we sort the tribe of the player with ascending order, so the cards will be displayed for a better visualization

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

        grid.setHgap(10);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER_LEFT);

        Map<StatType, Label> labels = new HashMap<>();  // here we create the map linked to the player

        addStat(grid, labels, StatType.FOOD, "file:C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/stats/food.png", 0, 0);
        addStat(grid, labels, StatType.AXE, "file:C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/stats/buildingDiscount.png", 0, 2);
        addStat(grid, labels, StatType.PRESTIGE, "file:C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/stats/prestige.png", 0, 4);

        addStat(grid, labels, StatType.STARS, "file:C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/stats/stars.png", 1, 0);
        addStat(grid, labels, StatType.BOWL, "file:C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/stats/gatheringDiscount.png", 1, 2);

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

        Label valueLabel = new Label("0");  // here we put the value of the stat initialized to 0
        valueLabel.setMinWidth(26);
        valueLabel.setAlignment(Pos.CENTER_LEFT);

        valueLabel.setStyle("""
        -fx-text-fill: white;
        -fx-font-size: 15;
        -fx-font-family: 'Georgia';
        -fx-font-weight: bold;
    """);

        grid.add(icon, col, row);  // here we add the image to the grid
        grid.add(valueLabel, col + 1, row); // here we add the value to the grid

        labels.put(type, valueLabel);
    }



    private void updatePlayerStats() {

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


    private ImageView createDeckCard(int id){
        ImageView card = new ImageView("file:C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/cardsFront/" + id + ".jpg");
        card.setFitWidth(110);
        card.setFitHeight(165);
        card.setPreserveRatio(true);
        card.setSmooth(true);
        return card;
    }

    private ImageView createHomeTribeCard(int id){
        ImageView card = new ImageView("file:C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/cardsFront/" + id + ".jpg");
        card.setFitWidth(70);
        card.setFitHeight(105);
        card.setPreserveRatio(true);
        card.setSmooth(true);
        return card;
    }

    private ImageView createAwayTribeCard(int id){
        ImageView card = new ImageView("file:C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/cardsFront/" + id + ".jpg");
        card.setFitWidth(50);
        card.setFitHeight(75);
        card.setPreserveRatio(true);
        card.setSmooth(true);
        return card;
    }



    private ImageView createTrackCard(char name){
        ImageView card = new ImageView("file:C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/trackCards/" + name + ".jpg");
        card.setFitWidth(120);
        card.setFitHeight(180);
        return card;
    }


    private ImageView createLobbyTotem(String totemColor) {
        ImageView totem = new ImageView("file:C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/totem/" + totemColor+ ".png");

        totem.setFitWidth(220);
        totem.setPreserveRatio(true);
        totem.setSmooth(true);

        return totem;
    }



    public static void main(String[] args) {
        launch(args);
    }
}