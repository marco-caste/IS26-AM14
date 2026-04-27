package it.polimi.ingsw.am14.rmi.server;

import it.polimi.ingsw.am14.rmi.client.VirtualServerRmi;

public class RmiClientHandler implements VirtualServerRmi {

    @Override
    public void connectUser(String nickname) {

    }

    @Override
    public void createGame(String nickname, int numPlayers, String totemColor) {

    }

    @Override
    public void joinGame(String nickname, int gameId, String totemColor) {

    }

    @Override
    public void placeTotem(String nickname, char position) {

    }

    @Override
    public void pickCard(String nickname, int cardId, int row) {

    }
}
