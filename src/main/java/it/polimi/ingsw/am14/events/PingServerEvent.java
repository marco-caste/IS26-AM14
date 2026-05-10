package it.polimi.ingsw.am14.events;

import it.polimi.ingsw.am14.clientModel.ClientModel;

public class PingServerEvent extends ServerEvent {

    private final static long PING_TIMEOUT = 30_000; // 30 secondi

    public PingServerEvent() {
        super("pingServer");
    }

    @Override
    public void effect(ClientModel model) {

    }
}
