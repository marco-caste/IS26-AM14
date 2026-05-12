package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.clientModel.ClientModel;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.network.VirtualClient;

public class PlayerDisconnectedEvent extends ServerEvent{
    private final String nickname;
    @JsonIgnore
    private int gameId;

    public PlayerDisconnectedEvent(String nickname, int gameId) {
        super("");
        this.nickname = nickname;
        this.description = "Player " + nickname + " disconnected";
        this.gameId = gameId;
    }

    @JsonCreator
    public PlayerDisconnectedEvent(
            @JsonProperty("description") String description,
            @JsonProperty("nickname") String nickname) {
        super(description);
        this.nickname = nickname;
    }

    @Override
    public void bufferize(NetworkLayer networkLayer) {
        networkLayer.broadcastUpdate(this, gameId);
        VirtualClient client = networkLayer.getClientBy(nickname);
        if (client != null) {
            networkLayer.notifyBufferForClient(client);
            networkLayer.removeThreadsByClient(client);
            networkLayer.removeBuffer(client);
        }
        networkLayer.removeClientBy(nickname);
        if (gameId >= 0) {
            networkLayer.removeGame(gameId);
        }
    }

    @Override
    public void effect(ClientModel model) {
        //TODO
    }
}
