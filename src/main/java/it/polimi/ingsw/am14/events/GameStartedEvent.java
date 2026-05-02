package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.clientModel.ClientModel;
import it.polimi.ingsw.am14.clientModel.ClientPlayer;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class GameStartedEvent extends ServerEvent {

    private final ArrayList<String> playerOrder;
    private int gameId; // only used for bufferization, will not be sent to the client
    private final ArrayList<String> totemOrder;
    private String lastPlayerConnectedNickname;

    public GameStartedEvent(final ArrayList<Player> players, int gameId, String lastPlayerConnectedNickname) {
        super("The game has started");
        this.playerOrder = players.stream()
                .map(Player::getNickname)
                .collect(Collectors.toCollection(ArrayList::new));
        this.totemOrder = players.stream()
                        .map(player -> player.getTotemColor().name())
                        .collect(Collectors.toCollection(ArrayList::new));
        this.gameId = gameId;
        this.lastPlayerConnectedNickname = lastPlayerConnectedNickname;
    }

    @JsonCreator
    public GameStartedEvent(
            @JsonProperty("description") String description,
            @JsonProperty("playerOrder") ArrayList<String> playerOrder,
            @JsonProperty("totemOrder") ArrayList<String> totemOrder) {
        super(description);
        this.playerOrder = playerOrder;
        this.totemOrder = totemOrder;
    }

    @Override
    public void effect(ClientModel model) {
        model.setMessage(description);
        int[] initialFood = {2, 3, 3, 4, 4};
        ClientPlayer player;
        model.setPlayerOrderInTurnOrderCard(playerOrder);
        for (int i = 0; i < playerOrder.size(); i++) {
            String nickname = playerOrder.get(i);
            if (model.getPlayerByNickname(nickname) == null) {
                player = new ClientPlayer(nickname);
                model.addPlayer(player);
            }
            else {
                player = model.getPlayerByNickname(nickname);
            }
            player.setTotemColor(totemOrder.get(i));
            player.setNumFood(initialFood[i]);
            player.setNumPrestigePoints(0);
            player.setPlayerPosition(null);
        }
        model.notifyObservers();
    }

    public void bufferize(NetworkLayer networkLayer) {
        networkLayer.addToEventBuffer(networkLayer.getClientBy(lastPlayerConnectedNickname), this);
        networkLayer.broadcastUpdate(this, gameId);
    }
}
