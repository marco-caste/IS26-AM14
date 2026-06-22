package it.polimi.ingsw.am14.network.messages.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.clientModelController.ClientModel;
import it.polimi.ingsw.am14.clientModelController.ClientPlayer;
import it.polimi.ingsw.am14.clientModelController.ClientController;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.view.viewEvents.LoginViewEvent;

/**
 * Event representing a successful login by a player.
 * Inherits from {@link ServerEvent}.
 * It contains the nickname of the player that successfully connected to the server.
 */

public class LoginSuccesfulEvent extends ServerEvent {

    private final String playerNickname;

    /**
     * Constructs a {@code LoginSuccesfulEvent} triggered by the server when a player successfully logs in.
     *
     * @param playerNickname the nickname of the player who logged in.
     */

    public LoginSuccesfulEvent(String playerNickname) {
        super("");
        this.playerNickname = playerNickname;
        this.description = "Player \"" + playerNickname + "\" has logged in successfully.";
    }

    /**
     * Constructs a {@code LoginSuccesfulEvent} from JSON data, used by Jackson upon receiving a message.
     *
     * @param description    the description of the event.
     * @param playerNickname the nickname of the player who logged in.
     */

    @JsonCreator
    public LoginSuccesfulEvent(
            @JsonProperty("description") String description,
            @JsonProperty("nickname") String playerNickname) {
        super(description);
        this.playerNickname = playerNickname;
    }

    /**
     * Applies the effect of this event to the provided client model, setting the home player
     * and notifying the view observers of the successful login.
     *
     * @param model the {@link ClientModel} representing the client-side state of the application.
     */

    @Override
    public void effect(ClientModel model) {
        model.setHomePlayer(new ClientPlayer(playerNickname));
//        model.setMessage(description);
        model.notifyObservers(new LoginViewEvent(description));
    }

    /**
     * Bufferizes the event into the network layer to be sent exclusively
     * to the player who just logged in.
     *
     * @param network the {@link NetworkLayer} used to send the event.
     */

    @Override
    public void bufferize(NetworkLayer network) {
        network.addToEventBuffer(network.getClientBy(playerNickname), this);
    }

    /**
     * This method is called by the ClientController after the event has been processed.
     * It initializes the ping mechanism for the connected player to maintain the connection alive.
     *
     * @param controller the {@link ClientController} that processed the event.
     */

    @Override
    public void onEventProcessed(ClientController controller) {
        controller.initializePing(playerNickname);
    }
}