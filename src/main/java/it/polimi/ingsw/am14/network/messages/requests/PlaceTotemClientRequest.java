package it.polimi.ingsw.am14.network.messages.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.exceptions.*;

import java.sql.SQLException;

/**
 * Request sent by a client to place their totem on the starting card.
 * Inherits from {@link ClientRequest}.
 * This request includes the chosen position for the player's totem.
 */
public class PlaceTotemClientRequest extends ClientRequest {

    private final char position;

    /**
     * Constructs a {@code PlaceTotemClientRequest} from JSON data, used by Jackson upon receiving a message.
     *
     * @param nickname the nickname of the client requesting to place the totem.
     * @param position the character representing the chosen position to place the totem.
     */

    @JsonCreator
    public PlaceTotemClientRequest(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("position") char position) {
        super("place totem client request", nickname);
        this.position = position;
    }

    /**
     * Dispatches the place totem request to the server's lobby controller.
     * Prompts the controller to position the player's totem at the specified placement.
     *
     * @param controller the {@link LobbyController} responsible for handling the game logic.
     * @throws IllegalNicknameException  if the client's nickname is invalid.
     * @throws WrongStateException       if the game is not in a suitable state to place a totem.
     * @throws WrongTurnException        if it is not the requesting player's turn to place a totem.
     * @throws UnknownPositionException  if the specified position is invalid or unrecognized.
     * @throws IllegalPlacementException if placing the totem at the specified position is not allowed.
     * @throws UnknownNicknameException  if the nickname does not correspond to an active player.
     * @throws SQLException              if a database error occurs during the operation.
     * @throws IllegalPickException      if there is an illegal pick associated with this action.
     * @throws ForcedPickException       if the player is attempting to place a totem when forced to pick instead.
     */

    public void dispatchRequest(LobbyController controller) throws IllegalNicknameException, WrongStateException, WrongTurnException, UnknownPositionException, IllegalPlacementException, UnknownNicknameException, SQLException, IllegalPickException, ForcedPickException {
        controller.placeTotem(this.nickname, this.position);
    }
}
