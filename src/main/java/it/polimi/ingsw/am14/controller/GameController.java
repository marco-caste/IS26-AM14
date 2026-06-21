package it.polimi.ingsw.am14.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.exceptions.*;
import it.polimi.ingsw.am14.model.gameLogic.Game;

import java.sql.SQLException;

public class GameController {
    private final Game game;

    @JsonCreator
    public GameController(
            @JsonProperty("game") Game game) {
        this.game = game;
    }

    public void pickCard(String nickname, int cardId, int row) throws WrongStateException, WrongTurnException, UnknownPositionException, IllegalPlacementException, UnknownNicknameException, IllegalPickException, ForcedPickException, SQLException {
        synchronized (game) {
            game.pickCard(nickname, cardId, row);
        }
    }

    public void placeTotem(String nickname, char position) throws WrongStateException, WrongTurnException, UnknownPositionException, IllegalPlacementException, UnknownNicknameException, SQLException, IllegalPickException, ForcedPickException {
        synchronized (game) {
            game.placeTotem(nickname, position);
        }
    }

    public void onPlayerConnection(String nickname, String totemColor) throws WrongStateException {
        synchronized (game) {
            game.onPlayerConnection(nickname, totemColor);
        }
    }

    public Game getGame() {
        return game;
    }
}