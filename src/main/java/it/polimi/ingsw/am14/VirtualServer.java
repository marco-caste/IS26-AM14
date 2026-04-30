package it.polimi.ingsw.am14;

import java.rmi.RemoteException;

public interface VirtualServer {
    void createGame(String nickname, int numPlayers, String totemColor) throws RemoteException;
    void joinGame(String nickname, int gameId, String totemColor) throws RemoteException;
    void placeTotem(String nickname, char position) throws RemoteException;
    void pickCard(String nickname, int cardId, int row) throws RemoteException;
}
