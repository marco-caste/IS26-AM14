package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.exceptions.*;
import it.polimi.ingsw.am14.model.cards.Card;

import java.util.Optional;

abstract class GameState {

    /**
     *
     * @param context not null
     * @param player not null
     * @param position any char
     * @throws WrongStateException by default
     * @throws UnknownPositionException if an override call throws it
     * @throws WrongTurnException if an override call throws it
     */
    protected void placeTotem(Game context, Player player, char position) throws WrongStateException, UnknownPositionException, WrongTurnException, IllegalPlacementException {
        throw new WrongStateException("Not in the right state to place a totem");
    }

    /**
     *
     * @param context not null
     * @param player not null
     * @param card not null
     * @param row between 0 and 3
     * @throws WrongStateException by default
     * @throws IllegalPickException if an override call throws it
     * @throws ForcedPickException if an override call throws it
     * @throws WrongTurnException if an override call throws it
     */
    protected void pickCard(Game context, Player player, Optional<Card> card, int row) throws WrongStateException, IllegalPickException, ForcedPickException, WrongTurnException {
        throw new WrongStateException("Not in the right state to pick a card");
    }

    /**
     *
     * @param context not null
     * @param nickname not null
     * @param totemColor not null
     * @throws WrongStateException by default
     */
    protected void onPlayerConnection(Game context, String nickname, String totemColor) throws WrongStateException {
        throw new WrongStateException("Not in the right state to handle a player connection");
    }

    /**
     *
     * @param context not null
     */
    protected abstract void nextState(Game context);
    // does nothing because only the model controls calls to this method

    /**
     *
     * @param context not null
     */
    protected void endRound(Game context) {
    }
}
