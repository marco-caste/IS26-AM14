package it.polimi.ingsw.am14.network.messages.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.clientModelController.ClientModel;
import it.polimi.ingsw.am14.clientModelController.ClientPlayer;
import it.polimi.ingsw.am14.clientModelController.ClientController;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.network.VirtualServer;
import it.polimi.ingsw.am14.view.viewEvents.LoginViewEvent;

import java.rmi.RemoteException;

public class LoginSuccesfulEvent extends ServerEvent {

    private final String playerNickname;

    public LoginSuccesfulEvent(String playerNickname) {
        super("");
        this.playerNickname = playerNickname;
        this.description = "Player \"" + playerNickname + "\" has logged in successfully.";
    }

    @JsonCreator
    public LoginSuccesfulEvent(
            @JsonProperty("description") String description,
            @JsonProperty("nickname") String playerNickname) {
        super(description);
        this.playerNickname = playerNickname;
    }

    @Override
    public void effect(ClientModel model) {
        model.setHomePlayer(new ClientPlayer(playerNickname));
//        model.setMessage(description);
        model.notifyObservers(new LoginViewEvent(description));
    }

    @Override
    public void bufferize(NetworkLayer network) {
        network.addToEventBuffer(network.getClientBy(playerNickname), this);
    }

    @Override
    public void onEventProcessed(ClientController controller) {
        controller.initializePing(playerNickname);
    }
}