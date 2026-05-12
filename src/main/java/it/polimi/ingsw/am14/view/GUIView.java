package it.polimi.ingsw.am14.view;

import it.polimi.ingsw.am14.clientModel.ClientModel;
import it.polimi.ingsw.am14.clientModel.ClientPlayer;
import it.polimi.ingsw.am14.network.EntryPointFactory;
import it.polimi.ingsw.am14.network.VirtualServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

import static it.polimi.ingsw.am14.utils.Constants.TotemColors;

public class GUIView extends Application implements ViewType {

    private ClientModel modelReference = null;
    private VirtualServer server;
    private Stage primaryStage;

    private Scene lobbyScene;
    private Scene gameScene;

    private VBox loginBox;
    private BorderPane root;

    private Label serverMessage;
    private Label lobbyMessage;
    private VBox lobbyPlayersBox;

    private VBox centerBoard;
    private ImageView deckView;

    private VBox playersStatsBox;

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

        lobbyScene = new Scene(createLobby(), 1400, 900);

        primaryStage.setTitle("Lobby");
        primaryStage.setScene(lobbyScene);
        primaryStage.show();
    }

    @Override
    public void run() throws IOException {
        // Non chiamare start() qui.
        // JavaFX chiama automaticamente start(Stage primaryStage) dopo launch().
    }

    @Override
    public void update(ClientModel model) {
        this.modelReference = model;

        Platform.runLater(() -> {
            if (modelReference == null) return;

            /*
             * QUI DEVI ADATTARE QUESTA CONDIZIONE AL TUO CLIENTMODEL.
             *
             * Se avete un metodo tipo:
             * modelReference.isGameStarted()
             * usate quello.
             *
             * Se invece avete uno stato tipo:
             * modelReference.getGameState().equals("PLAYING")
             * adattalo lì.
             */
            if (!isGameStarted()) {
                updateLobby();
                showLobbyScene();
            } else {
                if (gameScene == null) {
                    createGameScene();
                }

                showGameScene();

                updateTop();
                updateCards();
                updatePlayerStats();
                updateHomeTribe(homeTribeBox);
            }
        });
    }

    private boolean isGameStarted() {
        /*
         * METODO DA ADATTARE.
         *
         * Per ora metto una logica provvisoria:
         * se la board esiste e non è vuota, considero il game iniziato.
         *
         * Meglio sarebbe avere nel ClientModel:
         *
         * public boolean isGameStarted()
         *
         * e poi qui:
         *
         * return modelReference.isGameStarted();
         */

        return modelReference.getBoard() != null && !modelReference.getBoard().isEmpty();
    }


    private BorderPane createNicknameRoot() {
        BorderPane root = new BorderPane();

        root.setStyle("""
        -fx-background-color: linear-gradient(to bottom, #2b1230, #5b1630);
        -fx-padding: 30;
                    """);

        Label title = new Label("Mesos");
        title.setStyle(
                """
        -fx-text-fill:
                white;
                    -fx-font-size: 46;
        -f
                t-weight: bold;
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

        Label serverIpAddres = new Label("insert server ip address");
        connectionLabel.
                setStyle("""

                -fx-text-fill: white;
                    -fx-font-size: 16;
    """);

        TextField ipField = new TextField();
        ipField.setPromptText("ipField");
        ipField.setMaxWidth(300);


        Label serverPort = new Label("insert server port");
        connectionLabel
                .setStyle("""

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

            String netProtocol = connectionTypeBox.getValue();

            if (netProtocol == null) {
                errorLabel.setText("Seleziona un tipo di connessione.");
                return;
            }

            connect(insertedNickname, netProtocol, insertedIp, insertedPort);

            showGameChoiceScene();
        });

        nicknameField.setOnAction(event -> connectButton.fire());

        loginBox = new VBox(14);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.getChildren().addAll(
                title,
                subtitle,
                nicknameField,
                ipField,
                portField,
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


    private void updateLo gin(){
        if(modelReference.getHomePlayer() != null){ // login succesful
            showGameChoiceScene();
        } else{
         Label error = (Label) loginBox.getChildren().getLast();
         error
            .setText(modelReference.getMessage());
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

        Label title = new Label("HELLO, " + modelReference.getHomePlayer() + " B WELCOME TO MESOS");
        title.
                setStyle("""

                -fx-text-fill: white;
                    -fx-font-size: 38;
                    -fx-font-weight: bold;
    """);

        VBox joinBox = new VBox(10);


        Button createGameButton = new Button("Create Game");
        Button joinGameButton = new Button("Join Game");

        Label idLabel = new Label("GAME ID");
                idLabel.setStyle("""

                -fx-text-fill: white;
                    -fx-font-size: 16;
    """);

        TextField idField = new TextField("
        E ID");
        idField.setPromptText("serverPort");
        idField.setMaxWidth(300);

        createGameButton.setPrefWidth(240);
        joinGameButton.setPrefWidth(240);

        createGameButton.
                setStyle("""

                -fx-font-size: 17;
                    -fx-font-weight: bold;
                    -fx-padding: 10 20 10 20;
    """);

        join
                utton.setStyle(
                """
        -fx-font-size: 17;
        -fx-font-weight: bold;
        -fx-padding: 10 20 10 20;
    """);

        createGameButton.setOnAction(event -> {

        });

        joinGameButton.setOnAction(event -> {
            /*
             * Qui chiamerai il controller per joinare una partita.
             *
             * Esempio:
             * controller.joinGame();
             *
             * Oppure prima apri una schermata/lista delle partite disponibili.
             */
            showLobbyScene();
        });

        joinBox.getChildren().addAll(
                joinGameButton,
                idLabel,
                idField
        );

        VBox box = new VBox(22);
        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(
                title,
                createGameButton,
                joinBox
        );

        root.setCenter(box);

        return root;
    }







    private void connect(String chosenNickname, String netProtocol, String IP, String port) {
            this.server = EntryPointFactory.connect(new String[]{netProtocol, IP, port, chosenNickname}, this);
    }


    private void createGame(String[] args) throws IllegalArgumentException {

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














































    private BorderPane createLobby() {
        BorderPane lobbyRoot = new BorderPane();

        lobbyRoot.setStyle("""
            -fx-background-color: linear-gradient(to bottom, #2b1230, #5b1630);
            -fx-padding: 30;
        """);

        Label title = new Label("Lobby");
        title.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 42;
            -fx-font-weight: bold;
        """);

        lobbyMessage = new Label("In attesa che la partita inizi...");
        lobbyMessage.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 18;
        """);

        lobbyPlayersBox = new VBox(12);
        lobbyPlayersBox.setAlignment(Pos.CENTER);
        lobbyPlayersBox.setStyle("""
            -fx-background-color: rgba(255,255,255,0.15);
            -fx-background-radius: 14;
            -fx-padding: 25;
            -fx-min-width: 350;
        """);

        Button startButton = new Button("Start Game");
        startButton.setStyle("""
            -fx-font-size: 18;
            -fx-font-weight: bold;
            -fx-padding: 10 25 10 25;
        """);

        startButton.setOnAction(event -> {
            /*
             * Qui dovete chiamare il controller/client per chiedere al server
             * di iniziare la partita.
             *
             * Esempio:
             *
             * clientController.startGame();
             *
             * Oppure:
             *
             * networkHandler.sendStartGameRequest();
             */
        });

        VBox center = new VBox(25);
        center.setAlignment(Pos.CENTER);
        center.getChildren().addAll(title, lobbyMessage, lobbyPlayersBox, startButton);

        lobbyRoot.setCenter(center);

        return lobbyRoot;
    }

    private void showLobbyScene() {
        if (primaryStage == null || lobbyScene == null) return;

        if (primaryStage.getScene() != lobbyScene) {
            primaryStage.setTitle("Lobby");
            primaryStage.setScene(lobbyScene);
        }
    }

    private void updateLobby() {
        if (lobbyPlayersBox == null || modelReference == null) return;

        lobbyPlayersBox.getChildren().clear();

        if (lobbyMessage != null) {
            lobbyMessage.setText(modelReference.getMessage());
        }

        ClientPlayer homePlayer = modelReference.getHomePlayer();

        if (homePlayer != null) {
            lobbyPlayersBox.getChildren().add(
                    createLobbyPlayerLabel(homePlayer.getNickname() + "  (Tu)")
            );
        }

        if (modelReference.getOtherPlayers() != null) {
            for (ClientPlayer player : modelReference.getOtherPlayers()) {
                lobbyPlayersBox.getChildren().add(
                        createLobbyPlayerLabel(player.getNickname())
                );
            }
        }
    }

    private Label createLobbyPlayerLabel(String nickname) {
        Label label = new Label(nickname);

        label.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 22;
            -fx-font-weight: bold;
            -fx-background-color: rgba(255,255,255,0.18);
            -fx-background-radius: 10;
            -fx-padding: 8 20 8 20;
        """);

        return label;
    }

    // ============================================================
    // GAME SCENE
    // ============================================================

    private void createGameScene() {
        root = new BorderPane();

        Image backgroundImage = loadImage("/game_background.png");

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
        VBox top = new VBox(10);
        top.setStyle("-fx-background-color: rgb(255,0,0); -fx-padding: 15;");

        serverMessage = new Label();
        serverMessage.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 10;");

        top.getChildren().add(serverMessage);

        updateTop();
        return top;
    }

    private void updateTop(){
        serverMessage.setText(modelReference.getMessage());
    }




    private StackPane createCenter() {
        StackPane centerWrapper = new StackPane();
        centerWrapper.setStyle("-fx-background-color: transparent;");
        ImageView deck =  new ImageView("C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/deck/1.png"); // check
        ImageView turnOrderCard = new ImageView("C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/turnOrderCards" +modelReference.getNumPlayers() + ".png");
        ArrayList<Character> trackCards = modelReference.getOffersPath();

        centerBoard = new VBox(25);
        centerBoard.setAlignment(Pos.CENTER);
        centerBoard.setStyle("-fx-padding: 30;");

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

        centerBoard.getChildren().addAll(row0, row1, row2, row3, offersPath, deck); // we add the 4 rows of the board, the offersPath and the deck to the centerBoard
        centerWrapper.getChildren().add(centerBoard); // we add the vbox created to the stackPane

        updateCards(); // we update all the boxes with the cards from the board
        return centerWrapper;

    }


    private void updateCards() {
        ArrayList<ArrayList<Integer>> currentBoard = modelReference.getBoard();

        for (int i = 0; i < centerBoard.getChildren().size() - 2; i++) { // -1 in quanto la board riguarda solo le carte delle prime 4 row ( non riguarda la track card e turn order card)
            HBox box = (HBox) centerBoard.getChildren().get(i); // fatto cast forzato perché sappiamo che i figli di centerBoard sono tutti HBox
            box.getChildren().clear(); // we clear the actual cards in the box

            for (Integer cardId : currentBoard.get(i)) {
                ImageView card = createDeckCard(cardId);
                box.getChildren().add(card);
            }
            // for each Hbox of the centerBoard we update its cards
        }

        centerBoard.getChildren().removeLast();
        ImageView deck = new ImageView("C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/deck/" + modelReference.getAge() + ".png");
        centerBoard.getChildren().add(deck);// we update the deck with the image of the  new era

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

        HBox myTribe = new HBox(8);
        updateHomeTribe(myTribe);

        ScrollPane scroll = new ScrollPane(myTribe);
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

        updateAwayTribe(tribePreview, playerName);
        panel.getChildren().addAll(name, tribePreview);

        return panel;
    }


    public void updateHomeTribe (HBox tribe){

        tribe.getChildren().clear(); // cleaning the tribe
        ArrayList<Integer> playerTribe = new ArrayList<>(modelReference.getHomePlayer().getPlayerTribe().stream().sorted(Integer::compareTo).toList());
        // we sort the tribe of the player with ascending order, so the cards will be displayed for a better visualization

        for (Integer cardId : playerTribe) {
            ImageView card = createHomeTribeCard(cardId);
            tribe.getChildren().add(card);
        }
        // adding the updated tribe

    }


    public void updateAwayTribe ( TilePane tribe, String playerName){

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

        addStat(grid, labels, StatType.FOOD, "C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/stats/food.png", 0, 0);
        addStat(grid, labels, StatType.AXE, "C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/stats/buildingDiscount.png", 0, 2);
        addStat(grid, labels, StatType.PRESTIGE, "C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/stats/prestige.png", 0, 4);

        addStat(grid, labels, StatType.STARS, "C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/stats/stars.png", 1, 0);
        addStat(grid, labels, StatType.BOWL, "C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/stats/gatheringDiscount.png", 1, 2);

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
        ImageView card = new ImageView("C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/cardsFront/" + id + ".jpg");
        card.setFitWidth(110);
        card.setFitHeight(165);
        card.setPreserveRatio(true);
        card.setSmooth(true);
        return card;
    }

    private ImageView createHomeTribeCard(int id){
        ImageView card = new ImageView("C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/cardsFront/" + id + ".jpg");
        card.setFitWidth(70);
        card.setFitHeight(105);
        card.setPreserveRatio(true);
        card.setSmooth(true);
        return card;
    }

    private ImageView createAwayTribeCard(int id){
        ImageView card = new ImageView("C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/cardsFront/" + id + ".jpg");
        card.setFitWidth(50);
        card.setFitHeight(75);
        card.setPreserveRatio(true);
        card.setSmooth(true);
        return card;
    }



    private ImageView createTrackCard(char name){
        ImageView card = new ImageView("C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/trackCards/" + name + ".jpg");
        card.setFitWidth(120);
        card.setFitHeight(180);
        return card;
    }



    public static void main(String[] args) {
        launch(args);
    }
}
