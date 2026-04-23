package it.polimi.ingsw.am14.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientModel {

    List<ClientView> observers;
    List<Integer> playerTribe;
    Map<String, ArrayList<Integer>> playersAttributes;
    String playerNickname;
    ArrayList<String> playerOrder;
    ArrayList<ArrayList<Integer>> board;

    int turnOrderCardIndex; // it's needed to build the right TurnorderCard in the view, it depends on this index.
    int gameId;
    int numPlayers; // it's needed to build the right offersPath in the View.


    public void registerObserver(ClientView view) {
        observers.add(view);
    }


    public void notifyObservers(){
        for(ClientView observer : observers){
            observer.update(this);
        }
    }





    // setter
    public void setPlayerNickname(String playerNickname) {
        this.playerNickname = playerNickname;
    }

    public void setPlayerTribe(List<Integer> playerTribe) {
        this.playerTribe = playerTribe;
    }

    public void setPlayerOrder(ArrayList<String> playerOrder) {
        this.playerOrder = playerOrder;
    }

    public void setPlayersAttributes(Map<String, ArrayList<Integer>> playersAttributes) {
        this.playersAttributes = playersAttributes;
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





}
