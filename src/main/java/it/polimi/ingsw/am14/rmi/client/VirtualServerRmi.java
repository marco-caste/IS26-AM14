package it.polimi.ingsw.am14.rmi.client;

import it.polimi.ingsw.am14.VirtualServer;
import it.polimi.ingsw.am14.rmi.server.VirtualClientRmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServerRmi extends VirtualServer, Remote {
    void connect(VirtualClientRmi client) throws RemoteException;
    @Override
    void connectUser(String nickname);
    @Override
    void createGame(String nickname, int numPlayers, String totemColor);
    @Override
    void joinGame(String nickname, int gameId, String totemColor);
    @Override
    void placeTotem(String nickname, char position);
    @Override
    void pickCard(String nickname, int cardId, int row);
}
