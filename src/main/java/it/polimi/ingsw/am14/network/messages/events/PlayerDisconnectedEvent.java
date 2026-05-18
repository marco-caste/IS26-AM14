package it.polimi.ingsw.am14.network.messages.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.clientModelController.ClientModel;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.network.VirtualClient;
import it.polimi.ingsw.am14.view.viewEvents.UserDisconnectedViewEvent;

public class PlayerDisconnectedEvent extends ServerEvent{
    @JsonIgnore
    private String nickname;
    @JsonIgnore
    private int gameId;

    public PlayerDisconnectedEvent(String nickname, int gameId) {
        super("Player " + nickname + " disconnected");
        this.nickname = nickname;
        this.gameId = gameId;
    }

    @JsonCreator
    public PlayerDisconnectedEvent(
            @JsonProperty("description") String description) {
        super(description);
    }

    @Override
    public void bufferize(NetworkLayer networkLayer) {
        VirtualClient client = networkLayer.getClientBy(nickname);
        if (client != null) {
            networkLayer.notifyBufferForClient(client);
            networkLayer.removeThreadsByClient(client);
            networkLayer.removeBuffer(client);
        }
        networkLayer.removeClientBy(nickname);
        networkLayer.removeClientFromGameId(client, gameId);
        networkLayer.broadcastUpdate(this, gameId);
        if (gameId >= 0) {
            networkLayer.removeGame(gameId);
        }
    }

    @Override
    public void effect(ClientModel model) {
        model.setMessage(description);
        model.returnToLobby();
        model.notifyObservers(new UserDisconnectedViewEvent());
    }
}
