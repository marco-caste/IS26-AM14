package it.polimi.ingsw.am14.network.messages.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.exceptions.IllegalGameIdException;
import it.polimi.ingsw.am14.exceptions.IllegalNicknameException;
import it.polimi.ingsw.am14.exceptions.WrongStateException;

/**
 * Request sent by a client to establish a connection to the server.
 * Inherits from {@link ClientRequest}.
 * This is typically the first request sent to register a player's nickname in the lobby.
 */

public class ConnectClientRequest extends ClientRequest{

    /**
     * Constructs a {@code ConnectClientRequest} from JSON data, used by Jackson upon receiving a message.
     *
     * @param nickname the nickname chosen by the client trying to connect.
     */

    // mferr
    @JsonCreator
    public ConnectClientRequest(
            @JsonProperty("nickname") String nickname) {
        super("connect client request", nickname);
    }

    /**
     * Dispatches the connect request to the server's lobby controller.
     * Registers the incoming user with the provided nickname.
     *
     * @param controller the {@link LobbyController} responsible for handling the connection logic.
     * @throws IllegalNicknameException if the chosen nickname is already taken or invalid.
     * @throws IllegalGameIdException   if there is an issue with the game ID state.
     * @throws WrongStateException      if the server is not in a suitable state to accept connections.
     */

    // mferr
    @Override
    public void dispatchRequest(LobbyController controller) throws IllegalNicknameException, IllegalGameIdException, WrongStateException {
        controller.connectUser(this.nickname);
    }
}
