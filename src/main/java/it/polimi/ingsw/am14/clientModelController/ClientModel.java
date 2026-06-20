package it.polimi.ingsw.am14.clientModelController;

import it.polimi.ingsw.am14.view.ViewType;
import it.polimi.ingsw.am14.view.viewEvents.ViewEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.am14.utils.Constants.*;

public class ClientModel {

    //TODO: conviene aggiungere un array di tutti i clientPlayers in modo ogni volta nella gui di fare una add dell'homePlayer a otherPlayers per avere un array di tutti i player
    //TODO: fatti questi aggiornamenti cambiare la gui

    private static final Map<Integer, List<Character>> offersPath = Map.of(
            2, List.of('B','C','E','F'),
            3, List.of('B','C','D','E','F'),
            4, List.of('B','C','D','E','F','G'),
            5, List.of('A','B','C','D','E','F','G')
            );

    private List<ViewType> observers;
    private ClientPlayer homePlayer;
    private ArrayList<ClientPlayer> otherPlayers;
    private ArrayList<String> playerOrderInTurnOrderCard;
    private ArrayList<ArrayList<Integer>> board;
    private Map<Character, String> positionToNickname;
    private ArrayList<String> finalRank;
    private List<String> globalRank;
    private int turnOrderCardIndex; // it's needed to build the right TurnorderCard in the view, it depends on this index.
    private int gameId;
    private int numPlayers; // it's needed to build the right offersPath in the View.
    private String message;
    private int age;
    private ClientState state;

    public ClientModel() {
        this.observers = new ArrayList<>();
        this.homePlayer = null;
        this.otherPlayers = new ArrayList<>();
        this.playerOrderInTurnOrderCard = new ArrayList<>();
        this.board = new ArrayList<>();
        this.positionToNickname = new HashMap<>();
        this.turnOrderCardIndex = 0;
        this.gameId = -1;
        this.numPlayers = 0;
        this.message = "";
        this.state = null;
    }

    //Observer methods
    public void registerObserver(ViewType view) {
        observers.add(view);
    }


    public void notifyObservers(ViewEvent viewEvent){
        for(ViewType observer : observers){
            observer.update(this, viewEvent);
        }
    }


    //GETTERS
    public int getTurnOrderCardIndex() {
        return turnOrderCardIndex;
    }

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
        // TODO: RACE CONDITION TRA TUI E THREAD SOCKETCLIENT
        return new ArrayList<>(offersPath.get(numPlayers));
    }

    public ClientPlayer getHomePlayer (){
        return this.homePlayer;
    }

    public String getHomePlayerNickname(){
        return (this.homePlayer == null) ?
                "" : this.homePlayer.getNickname();

    }

    public ArrayList<ClientPlayer> getOtherPlayers() {
        return this.otherPlayers;
    }

    public ArrayList<ClientPlayer> getAllPlayers() {
        ArrayList<ClientPlayer> allPlayers = new ArrayList<>();
        allPlayers.add(homePlayer);
        allPlayers.addAll(otherPlayers);
        return allPlayers;
    }

    public int getGameId(){
        return this.gameId;
    }

    public int getPlayerPositionInTurnOrderCard(String nickname){
        return playerOrderInTurnOrderCard.indexOf(nickname);
        // if not present returns -1;
    }

    public ArrayList<String> getFinalRank() {
        return finalRank;
    }

    public List<String> getGlobalRank() {
        return globalRank;
    }

    public ArrayList<String> getPlayerOrderInTurnOrderCard() {
        return playerOrderInTurnOrderCard;
    }

    public Map<Character, String> getPositionToNickname() {
        return new HashMap<>(positionToNickname);
    }

    //Helper methods to get player positions from the positionToNickname map
    public Character getPlayerPosition(String nickname) {
        for (Map.Entry<Character, String> entry : positionToNickname.entrySet()) {
            if (entry.getValue().equals(nickname)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Character getHomePlayerPosition() {
        return getPlayerPosition(homePlayer.getNickname());
    }

    public ClientState getClientState(){
        return this.state;
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

    public void setBoard(ArrayList<ArrayList<Integer>> board) {
        this.board = board;
    }

    public void setHomePlayerTotemColor(String totemColor){
        homePlayer.setTotemColor(totemColor);
    }

    public void setFinalRank(ArrayList<String> finalRank) {
        this.finalRank = finalRank;
    }
    public void setGlobalRank(List<String> globalRank) {
        this.globalRank = globalRank;
    }

    public void checkAndEventuallySetPickState() {
        for (String nickname : playerOrderInTurnOrderCard) {
            System.out.println(playerOrderInTurnOrderCard);
            if (nickname != null) return;
        }
        this.state = ClientState.PICK;
    }

    public void setPlaceState() {
        this.state = ClientState.PLACE;
    }

    public void setState(String state) {
        this.state = ClientState.valueOf(state);
    }


    //UPDATES
    public void updateAge(int age){
        this.age = age;
    }

    public void updatePickPlayerStats(String nickname , int numFod, int numPrestigePoints, int numStars, int buildingDiscount, int gatheringDiscount){
        ClientPlayer player = getPlayerByNickname(nickname);
        if (player != null) {
            updatePlayerFood(nickname, numFod);
            updatePlayerPrestigePoints(nickname, numPrestigePoints);
            player.setNumStars(numStars);
            player.setTotalBuildingDiscount(buildingDiscount);
            player.setTotalGatheringDiscount(gatheringDiscount);
        }
    }

    public void updatePlayerPosition(String nickname, Character playerPosition) {
        ClientPlayer player = getPlayerByNickname(nickname);
        if (player != null) {
            Character oldPosition = getPlayerPosition(nickname);
            if (oldPosition != null) {
                positionToNickname.remove(oldPosition);
            }
            if (playerPosition != null) {
                positionToNickname.put(playerPosition, nickname);
            }
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
    public void removePlayerFromTurnOrderCard(String nickname) {
        int index = playerOrderInTurnOrderCard.indexOf(nickname);
        if (index != -1) {
            playerOrderInTurnOrderCard.set(index, null);
        }
    }

    public void returnToLobby() {
        this.otherPlayers = new ArrayList<>();
        this.playerOrderInTurnOrderCard = new ArrayList<>();
        this.board = new ArrayList<>();
        this.positionToNickname = new HashMap<>();
        this.finalRank = new ArrayList<>();
        this.gameId = -1;
        this.state = null;
    }

    //Methods used by controller

    public boolean checkPickTurn() {
        Character homePlayerPosition = getHomePlayerPosition();

        if (homePlayerPosition == null || homePlayerPosition == 'A') {
            return false;
        }
        for (ClientPlayer player : otherPlayers) {
            Character playerPos = getPlayerPosition(player.getNickname());
            if (playerPos != null && homePlayerPosition > playerPos) {
                return false;
            }
        }
        return true;
    }

    public int findRowFromCardId(int cardId) {
        for (int i = 0; i < board.size(); i++) {
            if (board.get(i).contains(cardId)) {
                return i;
            }
        }
        return -1; // Card not found
    }

    public boolean checkRow(int row) {
        return true;
    }

    public boolean checkPlaceTurn() {
        for (String nickname : playerOrderInTurnOrderCard) {
            if (nickname == null) continue;
            return nickname.equals(homePlayer.getNickname());
        }
        return false;
    }


    public boolean checkTotemPosition(char position) {
        List<Character> validPositions = offersPath.get(numPlayers);
        if (!validPositions.contains(position)) {
            return false;
        }
        if (positionToNickname.containsKey(position)) {
            return false;
        }
        return true;
    }


    public boolean checkPickState() {
        return state == ClientState.PICK;
    }

    public boolean checkPlaceState() {
        return state == ClientState.PLACE;
    }
}