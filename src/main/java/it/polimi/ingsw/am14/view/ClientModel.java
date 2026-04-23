package it.polimi.ingsw.am14.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientModel {

    List<ClientView> observers;
    ClientPlayer homePlayer;
    ArrayList<ClientPlayer> otherPlayers =  new ArrayList<>();
    ArrayList<String> playerOrderInTurnOrderCard;
    ArrayList<ArrayList<Integer>> board;;

    int turnOrderCardIndex; // it's needed to build the right TurnorderCard in the view, it depends on this index.
    int gameId;
    int numPlayers; // it's needed to build the right offersPath in the View.
    String message;



    public void registerObserver(ClientView view) {
        observers.add(view);
    }

    public void notifyObservers(){
        for(ClientView observer : observers){
            observer.update(this);
        }
    }


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
        board.get(row).remove(cardID);
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
}