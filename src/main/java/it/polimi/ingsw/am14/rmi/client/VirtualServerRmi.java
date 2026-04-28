package it.polimi.ingsw.am14.rmi.client;

import it.polimi.ingsw.am14.VirtualServer;
import it.polimi.ingsw.am14.rmi.server.VirtualClientRmi;

import java.rmi.Remote;

public interface VirtualServerRmi extends VirtualServer, Remote {
        void connect(VirtualClientRmi client, String nickname) throws java.rmi.RemoteException;
}
