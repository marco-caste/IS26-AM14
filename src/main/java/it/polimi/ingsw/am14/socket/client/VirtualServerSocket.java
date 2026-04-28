package it.polimi.ingsw.am14.socket.client;

import it.polimi.ingsw.am14.VirtualServer;

public interface VirtualServerSocket extends VirtualServer {
    void connectUser(String nickname);
}
