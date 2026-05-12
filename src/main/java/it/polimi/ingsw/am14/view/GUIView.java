package it.polimi.ingsw.am14.view;

import it.polimi.ingsw.am14.clientModel.ClientModel;
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
import java.util.ArrayList;

/*public class GUIView extends Application implements ViewType {

    private ClientModel modelReference = null;
    private BorderPane root;
    private Label serverMessage;
    private VBox centerBoard = null;
    private VBox playersStatsBox;

    @Override
    public void start(Stage primaryStage) {
        // Il ClientModel viene passato da fuori (vedi main())
        // Non lo creiamo qui, ma lo riceviamo registrato come observer

        Image backgroundImage = new Image("file:C:/Users/matt0/Desktop/mesos/game_background.png");
        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(409, 630, true, true, true , false)
        );

        root = new BorderPane();
        root.setBackground(new Background(bgImage));

        root.setTop(createTop());
        root.setCenter(createCenter());
        root.setRight(createRight());
        root.setBottom(createBottom());

        Scene scene = new Scene(root, 1400, 900);
        primaryStage.setTitle("Game - " + modelReference.getNumPlayers() + " Players");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void run() throws IOException {

    }

    // Questo metodo viene chiamato dal ClientModel quando i dati cambiano
    @Override
    public void update(ClientModel model) {
            this.modelReference = model;

        // Usa Platform.runLater() perché gli aggiornamenti vengono da thread diversi
        Platform.runLater(() -> {
            // Aggiorna il messaggio dal server
            serverMessage.setText(model.getMessage());

            // Aggiorna le carte
            updateCards();

            // Aggiorna le stats dei giocatori
            updatePlayersStats(model);
        });
    }



    private void updatePlayersStats(ClientModel model) {

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


    private HBox createCardRow() {
        HBox row = new HBox(10); // we use 10 pixel of space between the cards
        row.setAlignment(Pos.CENTER); // this function is needed to center the cards in the row. So the card will spawn from the center to the left.
        row.setStyle("""
        -fx-background-color: rgba(255, 255, 255, 0.18);   
        -fx-background-radius: 14;
        -fx-padding: 7.5;
    """);
        // this is the color the hobox
        // the radius is for the rounded corners and the padding is for the space between the border and the content of the hbox
        // the padding is the space between the border and the content of the hbox, so if you want to have more space between the border and the cards you can increase the padding value.

        return row;
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


    private HBox createBottom() {
        HBox bottom = new HBox(20);
        bottom.setStyle("-fx-background-color: rgba(60, 30, 60, 0.9); -fx-padding: 20;");
        bottom.setAlignment(Pos.CENTER_LEFT);

        Label myCardsLabel = new Label("Le Tue Carte:");
        myCardsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold;");

        HBox myCards = new HBox(15);
        myCards.setStyle("-fx-background-color: transparent;");

        for (int i = 1; i <= 3; i++) {
            VBox card = createCardBox("Mia " + i);
            card.setPrefWidth(100);
            card.setPrefHeight(150);
            myCards.getChildren().add(card);
        }

        Separator sep = new Separator();
        sep.setOrientation(javafx.geometry.Orientation.VERTICAL);

        HBox commands = new HBox(10);
        commands.getChildren().addAll(
                new Button("Conferma"),
                new Button("Annulla"),
                new Button("Menu")
        );

        bottom.getChildren().addAll(myCardsLabel, myCards, sep, commands);
        return bottom;
    }

















    private ScrollPane createRight() {
        ScrollPane scroll = new ScrollPane();
        scroll.setStyle("-fx-background-color: transparent;");
        scroll.setFitToHeight(true);
        scroll.setPrefWidth(280);

        playersStatsBox = new VBox(15);
        playersStatsBox.setStyle("-fx-background-color: rgba(80, 40, 80, 0.8); -fx-padding: 15;");

        scroll.setContent(playersStatsBox);
        return scroll;
    }

    private VBox createPlayerStats(String playerName) {
        VBox playerBox = new VBox(5);

        Label name = new Label(playerName);
        name.setStyle("-fx-text-fill: white; -fx-font-size: 12; -fx-font-weight: bold;");

        Label points = new Label("Punti: 0");
        points.setStyle("-fx-text-fill: #FFD700;");

        Label lives = new Label("Vite: 0");
        lives.setStyle("-fx-text-fill: #FF6B6B;");

        Label status = new Label("Status: Attivo");
        status.setStyle("-fx-text-fill: #90EE90;");

        playerBox.getChildren().addAll(name, points, lives, status);
        return playerBox;
    }



    private ImageView createDeckCard(int id){

        ImageView card = new ImageView("C:/Users/matt0/IdeaProjects/IS26-AM14/src/main/resources/cardsFront/" + id + ".jpg");
        card.setFitWidth(120);
        card.setFitHeight(180);
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
*/