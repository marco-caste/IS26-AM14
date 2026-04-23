package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.view.ClientModel;
import it.polimi.ingsw.am14.view.ClientPlayer;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class GameStartedEvent extends ServerEvent {

    private final ArrayList<String> playerOrder;
    private final ArrayList<String> totemOrder;

    public GameStartedEvent(final ArrayList<Player> players) {
        super("The game has started");
        this.playerOrder = players.stream()
                .map(Player::getNickname)
                .collect(Collectors.toCollection(ArrayList::new));
        this.totemOrder = players.stream()
                        .map(player -> player.getTotemColor().name())
                        .collect(Collectors.toCollection(ArrayList::new));
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
}
