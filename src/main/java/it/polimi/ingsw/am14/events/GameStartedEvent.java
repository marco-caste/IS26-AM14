package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.NetworkLayer;
import it.polimi.ingsw.am14.model.gameLogic.Player;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class GameStartedEvent extends ServerEvent {

    private final ArrayList<String> playerOrder;
    private int gameId; // only used for bufferization, will not be sent to the client

    public GameStartedEvent(final ArrayList<Player> players, int gameId) {
        super("The game has started");
        this.playerOrder = players.stream()
                .map(Player::getNickname)
                .collect(Collectors.toCollection(ArrayList::new));
        this.gameId = gameId;
    }

    @JsonCreator
    public GameStartedEvent(
            @JsonProperty("description") String description,
            @JsonProperty("playerOrder") ArrayList<String> playerOrder) {
        super(description);
        this.playerOrder = playerOrder;
    }

    public void bufferize(NetworkLayer networkLayer) {
        networkLayer.broadcastUpdate(this, gameId);
    }
}
