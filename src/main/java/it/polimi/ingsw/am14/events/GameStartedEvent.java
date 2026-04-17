package it.polimi.ingsw.am14.events;

import it.polimi.ingsw.am14.model.gameLogic.Player;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class GameStartedEvent extends ServerEvent {

    private final ArrayList<String> playerOrder;

    public GameStartedEvent(final ArrayList<Player> players) {
        this.playerOrder = players.stream()
                .map(Player::getNickname)
                .collect(Collectors.toCollection(ArrayList::new));
        this.description = "The game has started";
    }
}
