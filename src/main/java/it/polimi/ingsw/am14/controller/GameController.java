package it.polimi.ingsw.am14.controller;

import it.polimi.ingsw.am14.exceptions.*;
import it.polimi.ingsw.am14.model.gameLogic.Game;

public class GameController {
    private Game game;
    public GameController(Game game) {
        this.game = game;
    }

    public void pickCard(String nickname, int cardId, int row) throws WrongStateException, WrongTurnException, UnknownPositionException, IllegalPlacementException, UnknownNicknameException, IllegalPickException, ForcedPickException {
        synchronized (game) {
            game.pickCard(nickname, cardId, row);
        }

        //update(CardPickedEvent(X));
    }

    public void placeTotem(String nickname, char position) throws WrongStateException, WrongTurnException, UnknownPositionException, IllegalPlacementException, UnknownNicknameException {
        synchronized (game) {
            game.placeTotem(nickname, position);
        }

        //update(TotemPlacedEvent(X));
    }

    public void onPlayerConnection(String nickname, String totemColor) throws WrongStateException {
        synchronized (game) {
            game.onPlayerConnection(nickname, totemColor);
        }

        //update(PlayerConnectedEvent(X));
    }
}