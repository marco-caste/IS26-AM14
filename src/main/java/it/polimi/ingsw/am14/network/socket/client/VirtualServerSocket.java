package it.polimi.ingsw.am14.network.socket.client;

import it.polimi.ingsw.am14.network.VirtualServer;

public interface VirtualServerSocket extends VirtualServer {
    void connectUser(String nickname);
}
