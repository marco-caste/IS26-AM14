package it.polimi.ingsw.am14.view;

import java.util.ArrayList;
import java.util.List;

public class ClientModel {

    List<ClientView> observers;
    ClientPlayer homePlayer;
    ArrayList<ClientPlayer> otherPlayers;
    ArrayList<String> playerOrder;
    ArrayList<ArrayList<Integer>> board;;

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

    public void setPlayerOrder(ArrayList<String> playerOrder) {
        this.playerOrder = playerOrder;
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

    private ClientPlayer getPlayerByNickname(String nickname) {
        if (homePlayer.getNickname().equals(nickname)) {
            return homePlayer;
        }
        for (ClientPlayer player : otherPlayers) {
            if (player.getNickname().equals(nickname)) {
                return player;
            }
        }
        return null;
    }

    public void updatePlayerFood(String nickname, int numFood) {
        ClientPlayer player = getPlayerByNickname(nickname);
        if (player != null) {
            player.setNumFood(numFood);
            notifyObservers();
        }
    }

    public void updatePlayerPrestigePoints(String nickname, int numPrestigePoints) {
        ClientPlayer player = getPlayerByNickname(nickname);
        if (player != null) {
            player.setNumPrestigePoints(numPrestigePoints);
            notifyObservers();
        }
    }

    public void addPlayerTribeElement(String nickname, Integer tribeElement) {
        ClientPlayer player = getPlayerByNickname(nickname);
        if (player != null) {
            player.addToPlayerTribe(tribeElement);
            notifyObservers();
        }
    }
}