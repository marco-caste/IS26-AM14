package it.polimi.ingsw.am14.network.messages.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.exceptions.IllegalNicknameException;
import it.polimi.ingsw.am14.exceptions.IllegalSetupException;
import it.polimi.ingsw.am14.exceptions.WrongStateException;
/**
 * Request sent by a client to create a new game.
 * Inherits from {@link ClientRequest}.
 * This request includes the desired number of players and the totem color chosen by the creator.
 */
public class CreateGameClientRequest extends ClientRequest {

    private final int numPlayers;
    private final String totemColor;

    /**
     * Constructs a {@code CreateGameClientRequest} from JSON data, used by Jackson upon receiving a message.
     *
     * @param nickname   the nickname of the client requesting to create the game.
     * @param numPlayers the number of players for the new game.
     * @param totemColor the string representing the totem color chosen by the creator.
     */

    @JsonCreator
    public CreateGameClientRequest(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("numPlayers") int numPlayers,
            @JsonProperty("totemColor") String totemColor) {
        super("create game client request", nickname);
        this.numPlayers = numPlayers;
        this.totemColor = totemColor;
    }

    /**
     * Dispatches the create game request to the server's lobby controller.
     * Initiates the creation of a new game room with the specified parameters.
     *
     * @param controller the {@link LobbyController} responsible for handling the creation logic.
     * @throws IllegalSetupException    if the chosen number of players or totem color is invalid.
     * @throws WrongStateException      if the server or client is not in a suitable state to create a game.
     * @throws IllegalNicknameException if the client's nickname is not valid or registered.
     */

    public void dispatchRequest(LobbyController controller) throws IllegalSetupException, WrongStateException, IllegalNicknameException {
        controller.createGame(this.nickname, this.numPlayers, this.totemColor);
    }
}
