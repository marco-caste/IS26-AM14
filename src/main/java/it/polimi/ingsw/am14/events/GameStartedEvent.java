package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.view.ClientModel;

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
            @JsonProperty("playerOrder") ArrayList<String> playerOrder, ArrayList<String> totemOrder) {
        super(description);
        this.playerOrder = playerOrder;
        this.totemOrder = totemOrder;
    }

    @Override
    public void effect(ClientModel model) {
        model.setPlayerOrderInTurnOrderCard(playerOrder);
        model.
        model.notifyObservers();
    }

}
