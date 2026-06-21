package it.polimi.ingsw.am14.network.messages.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.exceptions.IllegalGameIdException;
import it.polimi.ingsw.am14.exceptions.WrongStateException;

import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * Request sent by a client to join an existing game.
 * Inherits from {@link ClientRequest}.
 * This request includes the ID of the game the player wants to join and their chosen totem color.
 */
public class JoinGameClientRequest extends ClientRequest {

    private final String totemColor;
    private final int gameId;

    /**
     * Constructs a {@code JoinGameClientRequest} from JSON data, used by Jackson upon receiving a message.
     *
     * @param nickname   the nickname of the client requesting to join the game.
     * @param totemColor the string representing the totem color chosen by the player.
     * @param gameId     the unique identifier of the game the player wishes to join.
     */

    @JsonCreator
    public JoinGameClientRequest(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("totemColor") String totemColor,
            @JsonProperty("gameId") int gameId) {
        super("join game client request", nickname);
        this.gameId = gameId;
        this.totemColor = totemColor;
    }

    /**
     * Dispatches the join game request to the server's lobby controller.
     * Attempts to add the player to the specified game with their chosen totem color.
     *
     * @param controller the {@link LobbyController} responsible for handling the join logic.
     * @throws WrongStateException    if the requested game is already full, already started, or not in a suitable state to be joined.
     * @throws IllegalGameIdException if the provided game ID does not exist.
     */

    public void dispatchRequest(LobbyController controller) throws WrongStateException, IllegalGameIdException {
        controller.joinGame(this.nickname, this.gameId, this.totemColor);
    }
}
