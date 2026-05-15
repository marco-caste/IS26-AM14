package it.polimi.ingsw.am14.clientModel;

import it.polimi.ingsw.am14.view.ViewType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientModel {

    //TODO: conviene aggiugnere un array di tutti i clientPlayers in modo ogni volta nella gui di fare una add dell'home a otherPlayers per avere un array di tutti i player
    //TODO: aggiungere l'offersPath come mappa di character- String(nickname), in quanto utilizzando la posizione nel clientPlayuer è scomodo
    //TODO: fatti questi aggionamenti cambiare la gui

    Map<Integer, List<Character>> offersPath = Map.of(
            2, List.of('B','C','E','F'),
            3, List.of('B','C','D','E','F'),
            4, List.of('B','C','D','E','F','G'),
            5, List.of('A','B','C','D','E','F','G')
            );

    List<ViewType> observers;
    ClientPlayer homePlayer;
    ArrayList<ClientPlayer> otherPlayers;
    ArrayList<String> playerOrderInTurnOrderCard;
    ArrayList<ArrayList<Integer>> board;
    int turnOrderCardIndex; // it's needed to build the right TurnorderCard in the view, it depends on this index.
    int gameId;
    int numPlayers; // it's needed to build the right offersPath in the View.
    String message;
    int age;

    public ClientModel() {
        this.observers = new ArrayList<>();
        this.homePlayer = null;
        this.otherPlayers = new ArrayList<>();
        this.playerOrderInTurnOrderCard = new ArrayList<>();
        this.board = new ArrayList<>();
        this.turnOrderCardIndex = 0;
        this.gameId = 0;
        this.numPlayers = 0;
        this.message = "";
    }

    //Observer methods
    public void registerObserver(ViewType view) {
        observers.add(view);
    }

    // TODO: parlare con gli altri per verificare insieme quale idea piace di più
    //  1- Override usando delle classi Update che hanno un riferimento alla Gui e che hanno un metodo update implementato diversamente. Questo oggetto di tipo update dovrà essere passato nel clientModel
    //  2- case switch
    //  3-overloading nella GUI,per
    //  ora fatto switch intanto per provare la GUI con update focalizzato
    public void notifyObservers(String eventChoice){
        for(ViewType observer : observers){
            observer.update(this, eventChoice);
        }
    }


    //GETTERS
    public ClientPlayer getPlayerByNickname(String nickname) {
        if (homePlayer != null && homePlayer.getNickname().equals(nickname)) {
            return homePlayer;
        }
        for (ClientPlayer player : otherPlayers) {
            if (player.getNickname().equals(nickname)) {
                return player;
            }
        }
        return null;
    }

    public ArrayList<ArrayList<Integer>> getBoard() {
        return board;
    }

    public String getMessage() {
        return message;
    }

    public int getAge() {
        return this.age;
    }

    public int getNumPlayers(){
        return this.numPlayers;
    }

    public ArrayList<Character> getOffersPath() {
        return new ArrayList<>(offersPath.get(numPlayers));
    }

    public ClientPlayer getHomePlayer (){
        return this.homePlayer;
    }

    public String getHomePlayerNickname(){
        return this.homePlayer.getNickname();
    }

    public ArrayList<ClientPlayer> getOtherPlayers() {
        return this.otherPlayers;
    }

    public int getGameId(){
        return this.gameId;
    }

    public int getPlayerPositionInTurnOrderCard(String nickname){
        return playerOrderInTurnOrderCard.indexOf(nickname);
        // se non presente indexOf ritorna -1
    }


    //SETTERS
    public void setMessage(String message) {
        this.message = message;
    }

    public void setPlayerOrderInTurnOrderCard(ArrayList<String> playerOrderInTurnOrderCard) {
        this.playerOrderInTurnOrderCard = playerOrderInTurnOrderCard;
    }

    public void setTurnOrderCardIndex(int turnOrderCardIndex) {
        this.turnOrderCardIndex = turnOrderCardIndex;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public void setHomePlayer(ClientPlayer homePlayer) {
        this.homePlayer = homePlayer;
    }

    public void setOtherPlayers(ArrayList<ClientPlayer> otherPlayers) {
        this.otherPlayers = otherPlayers;
    }

    public void setBoard(ArrayList<ArrayList<Integer>> board) {
        this.board = board;
    }

    public void setHomePlayerTotemColor(String totemColor){
        homePlayer.setTotemColor(totemColor);
    }




    //UPDATES
    public void updateAge(int age){
        this.age = age;
    }

    public void updatePickPlayerStats(String nickname , int numFodd, int numPrestigePoints, int numStars, int buildingDiscount, int gatheringDiscount){
        ClientPlayer player = getPlayerByNickname(nickname);
        if (player != null) {
            updatePlayerFood(nickname, numFodd);
            updatePlayerPrestigePoints(nickname, numPrestigePoints);
            player.setNumStars(numStars);
            player.setTotalBuildingDiscount(buildingDiscount);
            player.setTotalGatheringDiscount(gatheringDiscount);
        }
    }

    public void updatePlayerPosition(String nickname, Character playerPosition) {
        ClientPlayer player = getPlayerByNickname(nickname);
        if (player != null) {
            player.setPlayerPosition(playerPosition);
        }
    }

    public void updatePlayerFood(String nickname, int numFood) {
        ClientPlayer player = getPlayerByNickname(nickname);
        if (player != null) {
            player.setNumFood(numFood);
        }
    }

    public void updatePlayerPrestigePoints(String nickname, int numPrestigePoints) {
        ClientPlayer player = getPlayerByNickname(nickname);
        if (player != null) {
            player.setNumPrestigePoints(numPrestigePoints);
        }
    }

    public void updatePlayerTribe(String nickname,int CardID ) {
        ClientPlayer player = getPlayerByNickname(nickname);
        if (player != null) {
            player.addToPlayerTribe(CardID);
        }
    }

    public void removePickedCardFromBoard(int row, int cardID){
        board.get(row).remove(Integer.valueOf(cardID));
    }

    public void addPlayer(ClientPlayer player) {
        otherPlayers.add(player);
    }

    public void updatePlayerRoundAttributes(Map<String, ArrayList<Integer>> playersAttributes) {
        for (String playerNickname : playersAttributes.keySet()) {
            ArrayList<Integer> attributes = playersAttributes.get(playerNickname);
            updatePlayerFood(playerNickname, attributes.get(0));
            updatePlayerPrestigePoints(playerNickname, attributes.get(1));
        }
    }

    //We put null if the player choose the position because if we remove we move all the other totem upwards
    public void removePlayerFromTurnOrderCard(int index) {
        this.playerOrderInTurnOrderCard.set(index, null);
    }

    public void disconnect() {
        this.homePlayer = null;
        this.otherPlayers = new ArrayList<>();
        this.playerOrderInTurnOrderCard = new ArrayList<>();
        this.board = new ArrayList<>();
        this.turnOrderCardIndex = 0;
        this.gameId = -1;
        this.numPlayers = 0;
        for (ViewType observer : observers) {
            observer.disconnect();
        }
    }
}