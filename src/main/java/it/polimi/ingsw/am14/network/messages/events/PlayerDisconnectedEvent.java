package it.polimi.ingsw.am14.network.messages.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.clientModelController.ClientModel;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.network.VirtualClient;
import it.polimi.ingsw.am14.view.viewEvents.UserDisconnectedViewEvent;
/**
 * Event representing a player disconnecting from the game.
 * Inherits from {@link ServerEvent}.
 * It notifies other clients about the disconnection and triggers cleanup operations on the server network layer.
 */
public class PlayerDisconnectedEvent extends ServerEvent{
    @JsonIgnore
    private String nickname;
    @JsonIgnore
    private int gameId;

    /**
     * Constructs a {@code PlayerDisconnectedEvent} triggered by the server when a player disconnects.
     *
     * @param nickname the nickname of the disconnected player.
     * @param gameId   the unique ID of the game the player was in.
     */

    public PlayerDisconnectedEvent(String nickname, int gameId) {
        super("Player " + nickname + " disconnected");
        this.nickname = nickname;
        this.gameId = gameId;
    }


    /**
     * Constructs a {@code PlayerDisconnectedEvent} from JSON data, used by Jackson upon receiving a message.
     *
     * @param description the description of the event.
     */

    @JsonCreator
    public PlayerDisconnectedEvent(
            @JsonProperty("description") String description) {
        super(description);
    }

    /**
     * Bufferizes the event into the network layer, broadcasting the disconnection to other players
     * and performing cleanup tasks such as removing the client's threads, buffers, and game associations.
     *
     * @param networkLayer the network layer used to perform cleanup and broadcast the event.
     */

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

    /**
     * Applies the effect of this event to the provided client model, returning the remaining
     * players to the lobby and notifying the view observers of the disconnection.
     *
     * @param model the {@link ClientModel} representing the client-side state of the game.
     */

    @Override
    public void effect(ClientModel model) {
//        model.setMessage(description);
        model.returnToLobby();
        model.notifyObservers(new UserDisconnectedViewEvent(description));
    }
}
