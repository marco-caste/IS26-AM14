package it.polimi.ingsw.am14.network.rmi.client;

import it.polimi.ingsw.am14.network.VirtualServer;
import it.polimi.ingsw.am14.network.rmi.server.VirtualClientRmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServerRmi extends VirtualServer, Remote {
        void connect(VirtualClientRmi client, String nickname) throws RemoteException;
        @Override
        void createGame(String nickname, int numPlayers, String totemColor) throws RemoteException;
        @Override
        void joinGame(String nickname, int gameId, String totemColor) throws RemoteException;
        @Override
        void placeTotem(String nickname, char position) throws RemoteException;
        @Override
        void pickCard(String nickname, int cardId, int row) throws RemoteException;
        @Override
        void pingServer(String nickname);
}
