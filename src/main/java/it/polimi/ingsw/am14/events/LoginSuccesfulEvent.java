package it.polimi.ingsw.am14.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.clientModel.ClientModel;
import it.polimi.ingsw.am14.clientModel.ClientPlayer;
import it.polimi.ingsw.am14.network.NetworkLayer;

public class LoginSuccesfulEvent extends ServerEvent {

    String playerNickname;

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

    // update Client Model
    @Override
    public void effect(ClientModel model) {
        model.setHomePlayer(new ClientPlayer(playerNickname));
        model.setMessage(description);
        model.notifyObservers("login");
    }

    @Override
    public void bufferize(NetworkLayer network) {
        network.addToEventBuffer(network.getClientBy(playerNickname), this);
    }
}
