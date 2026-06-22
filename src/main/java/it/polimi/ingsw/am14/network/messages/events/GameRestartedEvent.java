package it.polimi.ingsw.am14.network.messages.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.clientModelController.ClientModel;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.view.viewEvents.GameRestartViewEvent;

/**
 * Event representing the restart of a game after being paused or interrupted.
 * Inherits from {@link ServerEvent}.
 * It contains the updated state of the game to be synchronized across the connected clients.
 */

public class GameRestartedEvent extends ServerEvent {

    private int gameId; // only used for bufferization, will not be sent to the client
    private String lastPlayerConnectedNickname; // only used for bufferization, will not be sent to the client
    private final String state;

    /**
     * Constructs a {@code GameRestartedEvent} triggered by the server model when the game resumes.
     *
     * @param gameId                      the unique ID of the game this event belongs to.
     * @param lastPlayerConnectedNickname the nickname of the last player who reconnected and caused the restart.
     * @param state                       the current state to which the game is transitioning.
     */

    public GameRestartedEvent(int gameId, String lastPlayerConnectedNickname, String state) {
        super("The game has restarted");
        this.gameId = gameId;
        this.lastPlayerConnectedNickname = lastPlayerConnectedNickname;
        this.state = state;
    }

    /**
     * Constructs a {@code GameRestartedEvent} from JSON data, used by Jackson upon receiving a message.
     *
     * @param description the description of the event.
     * @param state       the state of the game.
     */

    @JsonCreator
    public GameRestartedEvent(
            @JsonProperty("description") String description,
            @JsonProperty("state") String state){
        this.state = state;
        super(description);
    }

    /**
     * Applies the effect of this event to the provided client model, updating the local state
     * and notifying the view observers that the game has restarted.
     *
     * @param model the {@link ClientModel} representing the client-side state of the game.
     */

    @Override
    public void effect(ClientModel model) {
        model.setState(state);
        model.notifyObservers(new GameRestartViewEvent(description));
    }

    /**
     * Bufferizes the event into the network layer to be sent to the reconnected player
     * and broadcasted to all other connected clients in the game.
     *
     * @param networkLayer the network layer used to broadcast the event.
     */

    @Override
    public void bufferize(NetworkLayer networkLayer) {
        networkLayer.addToEventBuffer(networkLayer.getClientBy(lastPlayerConnectedNickname), this);
        networkLayer.broadcastUpdate(this, gameId);
    }
}
