package it.polimi.ingsw.am14.network.messages.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.clientModel.ClientModel;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.view.viewEvents.GameRestartViewEvent;


public class GameRestartedEvent extends ServerEvent {

    private int gameId; // only used for bufferization, will not be sent to the client
    private String lastPlayerConnectedNickname; // only used for bufferization, will not be sent to the client

    public GameRestartedEvent(int gameId, String lastPlayerConnectedNickname) {
        super("The game has restarted");
        this.gameId = gameId;
        this.lastPlayerConnectedNickname = lastPlayerConnectedNickname;
    }

    @JsonCreator
    public GameRestartedEvent(
            @JsonProperty("description") String description) {
        super(description);
    }

    @Override
    public void effect(ClientModel model) {
        model.setMessage(description);
        model.notifyObservers(new GameRestartViewEvent());
    }

    @Override
    public void bufferize(NetworkLayer networkLayer) {
        networkLayer.addToEventBuffer(networkLayer.getClientBy(lastPlayerConnectedNickname), this);
        networkLayer.broadcastUpdate(this, gameId);
    }
}
