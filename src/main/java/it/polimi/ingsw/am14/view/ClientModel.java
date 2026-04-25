package it.polimi.ingsw.am14.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientModel {

    //TODO: Serve replicare la macchina a stati? Come gestiamo i vari turni? aggiungiamo agli eventi qualcosa?
    List<ClientView> observers;
    ClientPlayer homePlayer;
    ArrayList<ClientPlayer> otherPlayers =  new ArrayList<>();
    ArrayList<String> playerOrderInTurnOrderCard;
    ArrayList<ArrayList<Integer>> board;;

    int turnOrderCardIndex; // it's needed to build the right TurnorderCard in the view, it depends on this index.
    int gameId;
    int numPlayers; // it's needed to build the right offersPath in the View.
    String message;

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
    public void registerObserver(ClientView view) {
        observers.add(view);
    }

    public void notifyObservers(){
        for(ClientView observer : observers){
            observer.update(this);
        }
    }

    //setters
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

     public void setBoard(ArrayList<ArrayList<Integer>> board) {
        this.board = board;
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
}