package it.polimi.ingsw.am14.network.messages.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.view.viewEvents.GameStartViewEvent;
import it.polimi.ingsw.am14.clientModelController.ClientModel;
import it.polimi.ingsw.am14.clientModelController.ClientPlayer;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static it.polimi.ingsw.am14.utils.Constants.initialFood;
/**
 * Event representing the start of a game.
 * Inherits from {@link ServerEvent}.
 * It contains the established turn order of players for the current game.
 */
public class GameStartedEvent extends ServerEvent {

    private final ArrayList<String> playerOrder;
    @JsonIgnore
    private int gameId; // only used for bufferization, will not be sent to the client
    @JsonIgnore
    private String lastPlayerConnectedNickname; // only used for bufferization, will not be sent to the client

    /**
     * Constructs a {@code GameStartedEvent} triggered by the server model when the game starts.
     *
     * @param players                     the list of {@link Player} instances participating in the game.
     * @param gameId                      the unique ID of the game this event belongs to.
     * @param lastPlayerConnectedNickname the nickname of the last player who connected and caused the game to start.
     */

    public GameStartedEvent(final ArrayList<Player> players, int gameId, String lastPlayerConnectedNickname) {
        super("The game has started");
        this.playerOrder = players.stream()
                .map(Player::getNickname)
                .collect(Collectors.toCollection(ArrayList::new));
        this.gameId = gameId;
        this.lastPlayerConnectedNickname = lastPlayerConnectedNickname;
    }

    /**
     * Constructs a {@code GameStartedEvent} from JSON data, used by Jackson upon receiving a message.
     *
     * @param description the description of the event.
     * @param playerOrder the list of player nicknames representing the turn order.
     */

    @JsonCreator
    public GameStartedEvent(
            @JsonProperty("description") String description,
            @JsonProperty("playerOrder") ArrayList<String> playerOrder) {
        super(description);
        this.playerOrder = playerOrder;
    }

    /**
     * Applies the effect of this event to the provided client model, initializing players' starting attributes
     * such as food and prestige points, and notifying the view observers.
     *
     * @param model the {@link ClientModel} representing the client-side state of the game.
     */

    @Override
    public void effect(ClientModel model) {
//        model.setMessage(description);

        ClientPlayer player;
        model.setPlayerOrderInTurnOrderCard(playerOrder);
        for (int i = 0; i < playerOrder.size(); i++) {
            String nickname = playerOrder.get(i);
            player = model.getPlayerByNickname(nickname);
            player.setNumFood(initialFood[i]);
            player.setNumPrestigePoints(0);
            model.updatePlayerPosition(nickname, null);
        }
        model.setPlaceState();
        model.notifyObservers(new GameStartViewEvent(description));
    }

    /**
     * Bufferizes the event into the network layer to be sent to the last connected player
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
