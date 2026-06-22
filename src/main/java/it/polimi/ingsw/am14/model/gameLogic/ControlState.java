package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.network.messages.events.CardPickedEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

/**
 * State implementation that handles the control, validation, and orchestration logic immediately
 * following a player's card pick phase.
 * <p>
 * This class acts as a state router within the State Pattern of the game framework. It evaluates
 * player pick balances across different board rows (Top/Bottom, Character/Building), processes
 * extra picks (such as {@code wasAdditionalPick} and special abilities), handles player totem rotations
 * on the {@code OffersPath}, updates turn orders, and safely decides whether the game should prompt
 * the next eligible player or transition toward round or game termination.
 * </p>
 */

class ControlState extends GameState {

    private boolean goToEndRound;
    private Card selectedCard;
    private int row;
    private Player pickingPlayer;
    /**
     * Executes the primary control cycle after a player attempts or completes a card pick.
     * <p>
     * The routine evaluates the pick under two main flows:
     * <ul>
     *   <li><b>Additional Pick Flow:</b> Instantly finalized and routed to update models if the choice
     *       stems from a temporary additional placement condition.</li>
     *   <li><b>Standard Round Flow:</b> Dynamically reads player totems on the track, checks affordable
     *       buildings or pickable characters via custom constraints, shifts turn counters, and computes
     *       whether control remains with the current player or moves down the turn queue.</li>
     * </ul>
     * </p>
     *
     * @param context           the central {@link Game} orchestrator managing rules and states
     * @param wasAdditionalPick {@code true} if this step covers an extra top pick; {@code false} for regular drafts
     * @param card              the targeted {@link Card} artifact involved in the action
     * @param rowChosen         the row index where the selection was performed
     * @throws SQLException     if a database or state persistence error occurs during state transitions
     */
    @Override
    protected void controlPick(Game context, boolean wasAdditionalPick, Card card, int rowChosen) throws SQLException {

        goToEndRound = false;
        selectedCard=card;
        row = rowChosen;

        // we don't need to check anything in this case --> mandatory since the checks are not designed to work for addTopPick
        if(wasAdditionalPick){
            goToEndRound = true;

            context.getPlayers().stream()
                    .filter(player -> player.getAddTopPick() == 1)
                    .findFirst()
                    .ifPresent(player -> {
                        update(player, selectedCard, row, context,true);
                        try {
                            nextState(context);
                        } catch (SQLException _) {}
                    });

            return;
        }

        // it's never null because controlPick is called if and only if there is atLeast a player on the offersPath
        pickingPlayer = Objects.requireNonNull(findFirstPlayerTotemOnOffersPath(context)).getPlayer();
        char playerTotemPosition = Objects.requireNonNull(findFirstPlayerTotemOnOffersPath(context)).getPosition();


        //Check Phase Start
        int availableTopCharacters = countAvailableCharacterCardsFromTopRow(context, pickingPlayer);
        int availableBottomCharacters = countAvailableCharacterCardsFromBottomRow(context, pickingPlayer);

        // if the current player has still picks left, we check if he can pick from both rows, otherwise we find the next player in turn order
        if (pickingPlayer.getNumTopPicksLeft() != 0 || pickingPlayer.getNumBottomPicksLeft() != 0) {
            boolean currentPlayerHasMorePicks = determineIfCurrentPlayerCanContinuePickingFromBothRows(pickingPlayer, context, availableTopCharacters, availableBottomCharacters, playerTotemPosition);
            if (!currentPlayerHasMorePicks) {
                findAndActivateNextPlayerInTurnOrder(context, availableTopCharacters, availableBottomCharacters, playerTotemPosition);
            }

        } else {  // we must analyze what happens after the pick of the current player who just finished their picks
            finalizeCurrentPlayerTurn(context, pickingPlayer, playerTotemPosition); // return the current player to the turnOrderCard


            if (context.getCurrentPlayerIndex() == context.getNumPlayers()) { // if the player was the last one

                handleAllPlayersFinishedTheirPicksPhase(context, availableTopCharacters); // check if a state transition is needed

            } else  {  // if the player is not the last one --> check if the next player can still pick
                findAndActivateNextPlayerInTurnOrder(context, availableTopCharacters, availableBottomCharacters, playerTotemPosition);
            }
        }

        nextState(context);

    }



    private int countAvailableCharacterCardsFromTopRow(Game context, Player player) {
        return Math.toIntExact(context.getBoard().getTopDeckableRow().stream()
                .filter(card -> card.isPickable(player))
                .count());
    }

    private int countAvailableCharacterCardsFromBottomRow(Game context, Player player) {
        return Math.toIntExact(context.getBoard().getBottomDeckableRow().stream()
                .filter(card -> card.isPickable(player))
                .count());
    }



    private void finalizeCurrentPlayerTurn(Game context, Player player, Character playerTotemPosition) {
        context.incrementCurrentPlayerIndexBy(1);
        context.getBoard().getOffersPath().offersPathGet(playerTotemPosition).setPlayerTotem(null);
        context.getBoard().getTurnOrderCard().addPlayer(player);
        this.update(player, selectedCard, row, context, true);
    }


    private boolean canPlayerAffordAnyBuildingInTopRow(Player player, Game context, int availableTopCharacters) {
        availableTopCharacters += Math.toIntExact(context.getBoard().getTopBuildingRow().stream()
                .filter(building -> building.getCost() <= player.getNumFood()).count());
        return availableTopCharacters > 0 && player.getNumTopPicksLeft() + player.getAddTopPick() > 0;
    }

    private boolean canPlayerAffordAnyBuildingInBottomRow(Player player, Game context, int availableBottomCharacters) {
        availableBottomCharacters += Math.toIntExact(context.getBoard().getBottomBuildingRow().stream()
                .filter(building -> building.getCost() <= player.getNumFood()).count());
        return availableBottomCharacters > 0 && player.getNumBottomPicksLeft() > 0;
    }


    private boolean determineIfCurrentPlayerCanContinuePickingFromBothRows(Player player, Game context, int availableTopCharacters, int availableBottomCharacters, Character playerTotem) {
        boolean canPickFromTopRow = canPlayerAffordAnyBuildingInTopRow(player, context, availableTopCharacters);
        boolean canPickFromBottomRow = canPlayerAffordAnyBuildingInBottomRow(player, context, availableBottomCharacters);

        if (canPickFromTopRow || canPickFromBottomRow) {
            this.update(player, selectedCard, row, context, false);
            return true;
        }

        // if they cannot pick --> conclude their turn
        finalizeCurrentPlayerTurn(context, player, playerTotem);

        return (handleAllPlayersFinishedTheirPicksPhase(context, availableTopCharacters));
    }



    private void findAndActivateNextPlayerInTurnOrder(Game context, int availableTopCharacters, int availableBottomCharacters, Character playerTotemPosition) {
        Character currentTotemPosition = playerTotemPosition;
        while (context.getCurrentPlayerIndex() < context.getNumPlayers()) {
            TotemSlot nextSlot = findNextPlayerTotemAfterPosition(context, currentTotemPosition);

            boolean currentPlayerCanStillPick = determineIfCurrentPlayerCanContinuePickingFromBothRows(Objects.requireNonNull(nextSlot).getPlayer(), context, availableTopCharacters, availableBottomCharacters, nextSlot.getPosition());
            if (currentPlayerCanStillPick) {
                return;
            }

            currentTotemPosition = nextSlot.getPosition();
        }
    }


    private TotemSlot findFirstPlayerTotemOnOffersPath(Game context) {
        for (Character key : context.getBoard().getOffersPath().offersPathKeySet()) {
            if (context.getBoard().getOffersPath().offersPathGet(key).getPlayerTotem() != null) {
                return new TotemSlot(context.getBoard().getOffersPath().offersPathGet(key).getPlayerTotem(), key);
            }
        }
        return null;
    }

    private TotemSlot findNextPlayerTotemAfterPosition(Game context, Character currentPosition) {
        for (Character key :  context.getBoard().getOffersPath().offersPathKeySet()) {
            if (key > currentPosition && context.getBoard().getOffersPath().offersPathGet(key).getPlayerTotem() != null) {
                return new TotemSlot(context.getBoard().getOffersPath().offersPathGet(key).getPlayerTotem(), key);
            }
        }
        return null;
    }



    private boolean handleAllPlayersFinishedTheirPicksPhase(Game context, int availableTopCharacters) {
        // When all players have finished their standard picks, we check if there is a player with an additional pick
        // If we find one and they can still pick from the top row, we give them the opportunity to do so
        // Otherwise, we move to the next phase (end of the pick turn)
        if (context.getCurrentPlayerIndex() == context.getNumPlayers()) {
            Optional<Player> playerWithAdditionalTopPick = context.getPlayers().stream()
                    .filter(player -> player.getAddTopPick() == 1)
                    .findFirst();

            if (playerWithAdditionalTopPick.isPresent()) {
                Player playerWithExtraTop = playerWithAdditionalTopPick.get();
                if (!canPlayerAffordAnyBuildingInTopRow(playerWithExtraTop, context, availableTopCharacters)) {
                    goToEndRound = true;
                }
            } else {
                goToEndRound= true;
            }


            return true;
        }
        return false;
    }


    private void update(Player player, Card card, int row, Game context, Boolean wasLastPick) {
        Optional<ArrayList<Player>> currentTurnOrderCard =
                !wasLastPick ? Optional.empty() :
                        Optional.of(context.getBoard().getTurnOrderCard().getPlayerOrder());

        // This must be included because determineIfCurrentPlayerCanContinuePickingFromBothRows is also called for players who are not picking during the control phase
        // therefore, the update will still be called for these non-picking players. In that case, however, we pass card = null since we do not need to register a pick
        // but potentially only an update of the turnOrderCard and trackCard in case not even the next player can skip.

        if(pickingPlayer!= null){
            if(!(pickingPlayer.equals(player))) {
                card = null;
            }
        }


        context.notifyObservers(
                new CardPickedEvent(
                        player.getNickname(),
                        card,
                        row,
                        player.getNumFood(),
                        player.getNumPrestigePoints(),
                        currentTurnOrderCard,
                        context.getGameId(),
                        player.getTotalBuildingDiscount(),
                        player.getTotalGatheringDiscount(),
                        player.getNumStars(),
                        wasLastPick

                )
        );
    }

    /**
     * Shifts control dynamically into the next state destination based on internal decision flags.
     * <p>
     * If {@code goToEndRound} evaluates to true, counters reset and control shifts to either
     * {@link EndGameState} (if round limit 10 is hit) or a generic {@link EndRoundState}.
     * Otherwise, loops context access smoothly back to a standard {@link PickState}.
     * </p>
     *
     * @param context       the tracking {@link Game} driver instance
     * @throws SQLException if a subsequent transaction context mapping encounters failures
     */
    @Override
    protected void nextState(Game context) throws SQLException {
        if (goToEndRound) {
            context.setCurrentPlayerIndex(0);
            if (context.getCurrentRound() == 10) {
                context.setCurrentState(new EndGameState());
                context.getCurrentState().endRound(context);
            } else {
                context.setCurrentState(new EndRoundState());
                context.getCurrentState().endRound(context);
            }
        }
        else
        {
            context.setCurrentState(new PickState());
        }
    }
}