package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.network.messages.events.TotemPlacedEvent;
import it.polimi.ingsw.am14.model.boardComponents.TrackCard;
import it.polimi.ingsw.am14.exceptions.IllegalPlacementException;
import it.polimi.ingsw.am14.exceptions.UnknownPositionException;
import it.polimi.ingsw.am14.exceptions.WrongTurnException;

/**
 * <p>
 *     Represents the state of the game where players are placing their totems on the board.
 *     In this state, players can place their totems on available track cards, and the game will handle the placement logic,
 *     including checking for valid positions, applying effects, and transitioning to the next state when all players have placed their totems.
 * </p>
 */
class PositionState extends GameState {

    Boolean isThereFirstFood = false;
    Player first = null;

    /**
     * {@inheritDoc}
     *
     * @param context the game context where the action is performed (must not be null)
     * @param player the player performing the placement (must not be null)
     * @param trackCardName single-character identifier of the target track card
     * @throws UnknownPositionException if the specified track identifier is invalid
     * @throws WrongTurnException if the provided player is not the current player
     * @throws IllegalPlacementException if the target track is already occupied or the placement is otherwise forbidden
     */

    @Override
    protected void placeTotem(Game context, Player player, char trackCardName) throws UnknownPositionException, WrongTurnException, IllegalPlacementException {

        String firstNickname = null;
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

        if (!expectedCurrentPlayer.equals(player)) {
            throw new WrongTurnException("It's not " + player.getNickname() + "'s turn");
        }

        if(destinationTrackCard.getPlayerTotem() != null) {
            throw new IllegalPlacementException("TrackCard already occupied by " + destinationTrackCard.getPlayerTotem().getNickname());
        }



        // places p on the desired TrackCard and applies its effects
        destinationTrackCard.setPlayerTotem(player);
        destinationTrackCard.effect();
        // removes p from the TurnOrderCard
        context.getBoard().getTurnOrderCard().removePlayer(currentPlayerIdx);
        // increases the currentPlayerIndex
        context.incrementCurrentPlayerIndexBy(1);


        if(context.getCurrentPlayerIndex() == 5){
            TrackCard foodTrackCard = context.getBoard().getOffersPath().offersPathGet(Character.toUpperCase('A'));
            first = foodTrackCard.getPlayerTotem();

            if(first != null){
                firstNickname = first.getNickname();
                isThereFirstFood = true;
                foodTrackCard.setPlayerTotem(null);
            }

        }


        if (context.getCurrentPlayerIndex() > context.getNumPlayers() - 1) {
            this.nextState(context);
        }



        context.notifyObservers(
                new TotemPlacedEvent(
                        player.getNickname(),
                        trackCardName,
                        context.getGameId(),
                        player.getNumFood(),
                        firstNickname
                )
        );

        // observer pattern: sends event to event buffer

    }

    // TODO: IL PROSSIMO STATO NON è PIù PICK STATE, MA CONTROLSTATE
    /**
     * {@inheritDoc}
     * <p>
     *     For this state, the next state is always {@link PickState}.
     * </p>
     * @param context not null
     */
    @Override

    protected void nextState(Game context) {
        // updatest current state
        context.setCurrentState(new PickState());
        // empties TurnOrderCard
        context.getBoard().getTurnOrderCard().clearPlayerOrder();

        if(isThereFirstFood) {
            context.getBoard().getTurnOrderCard().addPlayer(first);
            context.setCurrentPlayerIndex(1);
        }
        else {
            // sets currentPlayerIndex = 0
            context.setCurrentPlayerIndex(0);
        }
    }

    @Override
    protected String getStateName() {
        return "PLACE";
    }
}
