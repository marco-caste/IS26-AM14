package it.polimi.ingsw.am14.network.messages.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.exceptions.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.SQLException;
/**
 * Request sent by a client to pick a card during their turn.
 * Inherits from {@link ClientRequest}.
 * This request includes the ID of the card to be picked and the row it belongs to.
 */
public class PickCardClientRequest extends ClientRequest {

    private final int cardId;
    private final int row;

    /**
     * Constructs a {@code PickCardClientRequest} from JSON data, used by Jackson upon receiving a message.
     *
     * @param nickname the nickname of the client requesting to pick a card.
     * @param cardId   the unique identifier of the card to pick.
     * @param row      the index of the row where the card is positioned.
     */

    @JsonCreator
    public PickCardClientRequest(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("cardID") int cardId,
            @JsonProperty("row") int row) {
        super("pick card client request", nickname);
        this.cardId = cardId;
        this.row = row;
    }

    /**
     * Dispatches the pick card request to the server's lobby controller.
     * Prompts the controller to process the picking action for the specified user, card, and row.
     *
     * @param controller the {@link LobbyController} responsible for handling the game logic.
     * @throws IllegalNicknameException  if the client's nickname is invalid.
     * @throws WrongStateException       if the game is not in a state allowing a card pick.
     * @throws WrongTurnException        if it is not the requesting player's turn.
     * @throws UnknownPositionException  if the specified row is invalid.
     * @throws IllegalPickException      if picking the specified card is against the game rules.
     * @throws ForcedPickException       if the player is attempting to pick a card when forced to do another action.
     * @throws IllegalPlacementException if there is a placement error linked to this pick.
     * @throws UnknownNicknameException  if the nickname does not correspond to an active player.
     * @throws SQLException              if a database error occurs during the operation.
     */
    @Override
    public void dispatchRequest(LobbyController controller) throws IllegalNicknameException, WrongStateException, WrongTurnException, UnknownPositionException, IllegalPickException, ForcedPickException, IllegalPlacementException, UnknownNicknameException, SQLException {
        controller.pickCard(this.nickname, this.cardId, this.row);
    }
}
