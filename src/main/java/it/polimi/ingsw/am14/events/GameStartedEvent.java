package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.clientModel.ClientModel;
import it.polimi.ingsw.am14.clientModel.ClientPlayer;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static it.polimi.ingsw.am14.utils.Constants.initialFood;

public class GameStartedEvent extends ServerEvent {

    private final ArrayList<String> playerOrder;
    @JsonIgnore
    private int gameId; // only used for bufferization, will not be sent to the client
    @JsonIgnore
    private String lastPlayerConnectedNickname; // only used for bufferization, will not be sent to the client

    public GameStartedEvent(final ArrayList<Player> players, int gameId, String lastPlayerConnectedNickname) {
        super("The game has started");
        this.playerOrder = players.stream()
                .map(Player::getNickname)
                .collect(Collectors.toCollection(ArrayList::new));
        this.gameId = gameId;
        this.lastPlayerConnectedNickname = lastPlayerConnectedNickname;
    }

    @JsonCreator
    public GameStartedEvent(
            @JsonProperty("description") String description,
            @JsonProperty("playerOrder") ArrayList<String> playerOrder) {
        super(description);
        this.playerOrder = playerOrder;
    }

    @Override
    public void effect(ClientModel model) {
        model.setMessage(description);

        ClientPlayer player;
        model.setPlayerOrderInTurnOrderCard(playerOrder);
        for (int i = 0; i < playerOrder.size(); i++) {
            String nickname = playerOrder.get(i);
            player = model.getPlayerByNickname(nickname);
            player.setNumFood(initialFood[i]);
            player.setNumPrestigePoints(0);
            player.setPlayerPosition(null);
        }
        model.notifyObservers("gameStart");
    }

    @Override
    public void bufferize(NetworkLayer networkLayer) {
        networkLayer.addToEventBuffer(networkLayer.getClientBy(lastPlayerConnectedNickname), this);
        networkLayer.broadcastUpdate(this, gameId);
    }
}
