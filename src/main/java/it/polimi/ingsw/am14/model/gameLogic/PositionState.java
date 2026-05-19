package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.network.messages.events.TotemPlacedEvent;
import it.polimi.ingsw.am14.model.boardComponents.TrackCard;
import it.polimi.ingsw.am14.exceptions.IllegalPlacementException;
import it.polimi.ingsw.am14.exceptions.UnknownPositionException;
import it.polimi.ingsw.am14.exceptions.WrongTurnException;

class PositionState extends GameState {

    /**
     * <p>
     *     moves player p from the TurnOrderCard towards the required TrackCard:
     *     - sets trackCard.playerTotem to p
     *     - calls trackCard.effect()
     *     - increments game.currentPlayerIndex by 1
     *     - triggers a state transition if p has been the last player to position their totem
     * </p>
     * @param context not null
     * @param p not null
     * @param trackCardName any char
     * @throws UnknownPositionException if there is no TrackCard with name trackCardName
     * @throws WrongTurnException if it's not p's turn to play
     */
    @Override
    protected void placeTotem(Game context, Player p, char trackCardName) throws UnknownPositionException, WrongTurnException, IllegalPlacementException {

        // retrieves data from the context
        TrackCard destinationTrackCard = context.getBoard().getOffersPath().offersPathGet(Character.toUpperCase(trackCardName));
        int currentPlayerIdx = context.getCurrentPlayerIndex();

        if (destinationTrackCard == null) {
            throw new UnknownPositionException("There is no such trackCard with name " + trackCardName);
        }

        Player expectedCurrentPlayer;
        try {
             expectedCurrentPlayer = context.getBoard().getTurnOrderCard().getPlayer(currentPlayerIdx);
        } catch (NullPointerException e) {
            // never actually thrown, but needed for debug purposes
            throw new RuntimeException("[PositionState] " + e.getMessage());
        }

        if (!expectedCurrentPlayer.equals(p)) {
            throw new WrongTurnException("It's not " + p.getNickname() + "'s turn");
        }

        if(destinationTrackCard.getPlayerTotem() != null) {
            throw new IllegalPlacementException("TrackCard already occupied by " + destinationTrackCard.getPlayerTotem().getNickname());
        }

        // places p on the desired TrackCard and applies its effects
        destinationTrackCard.setPlayerTotem(p);
        destinationTrackCard.effect();
        // removes p from the TurnOrderCard
        context.getBoard().getTurnOrderCard().removePlayer(currentPlayerIdx);
        // increases the currentPlayerIndex
        context.incrementCurrentPlayerIndexBy(1);

        // observer pattern: sends event to event buffer
        context.notifyObservers(
                new TotemPlacedEvent(
                        p.getNickname(),
                        trackCardName,
                        context.getGameId()
                )
        );

        if (context.getCurrentPlayerIndex() > context.getNumPlayers() - 1) {
            this.nextState(context);
        }
    }

    /**
     * <p>
     *     changes the current State of the game:
     *     - sets context.currentState to a new PickState
     *     - sets context.currentPlayerIndex to 0
     *     - clears the TurnOrderCard's players array
     * </p>
     * @param context not null
     */
    @Override
    protected void nextState(Game context) {
        // updatest current state
        context.setCurrentState(new PickState());
        // empties TurnOrderCard
        context.getBoard().getTurnOrderCard().clearPlayerOrder();
        // sets currentPlayerIndex = 0
        context.setCurrentPlayerIndex(0);
    }

    @Override
    protected String getStateName() {
        return "PLACE";
    }
}
