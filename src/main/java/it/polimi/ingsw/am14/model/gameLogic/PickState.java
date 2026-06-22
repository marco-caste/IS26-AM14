package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.buildings.visitors.ImmediateVisitor;
import it.polimi.ingsw.am14.model.cards.buildings.visitors.SetVisitor;
import it.polimi.ingsw.am14.exceptions.ForcedPickException;
import it.polimi.ingsw.am14.exceptions.IllegalPickException;
import it.polimi.ingsw.am14.exceptions.WrongTurnException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import static it.polimi.ingsw.am14.utils.Constants.*;
/**
 * State implementation that manages the active drafting phase where players interactively
 * claim or purchase cards from the game board layout.
 * <p>
 * This class validates whether the requesting player is legally permitted to execute a pick based
 * on current turn orders or specific end-of-round extra pick allowances (e.g., additional top row picks).
 * It handles the subtraction of available pick actions, updates board slots, delegates resource
 * consumption for buildings, triggers card effects, and checks for tribe workforce set completions via
 * targeted Visitor classes.
 * </p>
 */
class PickState extends GameState {

    private boolean wasAdditionalTopPick ;
    private Card selectedCard;
    private int row;

    // TODO: dobbiamo andare a togliere l'optional per card in quanto non più necessario. Se lo tolgo qui ho un errore nel clientController
    /**
     * Executes a legal card drafting action requested by a player.
     * <p>
     * The processing pipeline operates as follows:
     * </p>
     * <ol>
     * <li>Extracts the card payload from the network-safe container wrapping the input data.</li>
     * <li>Resolves whether an exceptional turn override is active (players claiming bonus top row allocations).</li>
     * <li>Enforces chronological turn integrity against the top-most unfilled totem slot on the tracking pathway.</li>
     * <li>Validates quantitative limits to ensure the user possesses remaining action tokens for the requested row.</li>
     * <li>Branches logic based on row constants to handle character collection vs structural building purchases, updating models accordingly.</li>
     * </ol>
     *
     * @param context       the central {@link Game} context manager directing rules and status flows
     * @param player        the {@link Player} attempting to claim the structural layout item
     * @param safeCard      an {@link Optional} containing the target card reference (kept for client controller interface safety)
     * @param rowChosen     the board row identifier from which the item is being taken
     * @throws IllegalPickException if the selected item violates row restrictions or remaining actions equal zero
     * @throws ForcedPickException  if the target configuration conflicts with explicit game selection constraints
     * @throws WrongTurnException    if the executing identity's turn token does not align with the current active slot layout
     * @throws SQLException         if state logging or network serialization triggers a transactional problem
     */
    protected void pickCard(Game context, Player player, Optional<Card>  safeCard, int rowChosen)
            throws IllegalPickException, ForcedPickException, WrongTurnException, SQLException {

        selectedCard = safeCard.get();
        row = rowChosen;

        // In questo progetto `null` equivale a skip: lo normalizziamo a Optional vuoto.
        TotemSlot currentSlot = findFirstPlayerTotemOnOffersPath(context);
        Player currentPlayer = (currentSlot == null ? null : currentSlot.getPlayer());
        wasAdditionalTopPick = false;

        // exceptional pick due to additional pick building. This check is needed and is put first bacause the player with the additional top pick is not present
        // on the offers path and thus the check related to the player turn will be false.
        if (isExceptionalAddTopPickTurn(player, context) && (currentPlayer == null)){ // il null ci garantisce che l'additional pick avvenga quando tutti hanno già piccato
            handleExceptionalPick(selectedCard, row, context, player);
            return;
        }

        // it's not player's turn
        if (!player.equals(currentPlayer) && context.getCurrentPlayerIndex() != context.getNumPlayers()) {
            throw new WrongTurnException("It's not " + player.getNickname() + "'s turn to pick a card");
        }


        validateRowPicksAvailability(player, row);

        switch (row) {
            case TOP_DECKABLE_ROW:
                player.removeSingleTopPick();
                pickDeckCardAndApplyEffects(selectedCard, context, player);
                context.getBoard().removeFromTopDeckableRow(selectedCard);
                break;
            case TOP_BUILDING_ROW:
                player.removeSingleTopPick();
                pickBuildingAndApplyImmediateEffects((Building) selectedCard, context, player);
                context.getBoard().removeFromTopBuildingRow(selectedCard);
                break;
            case BOTTOM_DECKABLE_ROW:
                player.removeSingleBottomPick();
                pickDeckCardAndApplyEffects(selectedCard, context, player);
                context.getBoard().removeFromBottomDeckableRow(selectedCard);
                break;
            case BOTTOM_BUILDING_ROW:
                player.removeSingleBottomPick();
                pickBuildingAndApplyImmediateEffects((Building) selectedCard, context, player);
                context.getBoard().removeFromBottomBuildingRow(selectedCard);
                break;
            default:
                throw new IllegalPickException("You can't pick this card");
        }

        nextState(context);

    }



    private TotemSlot findFirstPlayerTotemOnOffersPath(Game context) {
        for (Character key : context.getBoard().getOffersPath().offersPathKeySet()) {
            if (context.getBoard().getOffersPath().offersPathGet(key).getPlayerTotem() != null) {
                return new TotemSlot(context.getBoard().getOffersPath().offersPathGet(key).getPlayerTotem(), key);
            }
        }
        return null;
    }


    private void applyBuildingEffectsIfSetsCompleted(Player player, Game context) {

        ArrayList<Integer> tribeSize = new ArrayList<>();
        tribeSize.add(player.getTribe().getHuntersSize());
        tribeSize.add(player.getTribe().getInventorsSize());
        tribeSize.add(player.getTribe().getShamansSize());
        tribeSize.add(player.getTribe().getGatherersSize());
        tribeSize.add(player.getTribe().getArtistsSize());
        tribeSize.add(player.getTribe().getBuildersSize());

        int min = tribeSize.stream().min(Integer::compare).orElse(0);
        int numSetCompleted = min - player.getNumSetCompleted();

        if (numSetCompleted > 0) {
            player.addToSetCompleted(numSetCompleted);
            //BUILDING CHECK
            SetVisitor visitor = new SetVisitor(context, player);
            ArrayList<Building> buildings = player.getTribe().getBuildings();
            for (Building building : buildings) {
                building.accept(visitor);
            }
        }
    }

    private void pickBuildingAndApplyImmediateEffects(Building building, Game context, Player player) {
        building.buy(player);
        ImmediateVisitor visitor = new ImmediateVisitor(context, player);
        building.accept(visitor);
    }

    private void pickDeckCardAndApplyEffects(Card card, Game context, Player player) {
        card.applyEffect(player, context);
        applyBuildingEffectsIfSetsCompleted(player, context);
    }


    private void validateRowPicksAvailability(Player player, int row) throws IllegalPickException {
        switch (row) {
            case TOP_DECKABLE_ROW:
            case TOP_BUILDING_ROW:
                if (player.getNumTopPicksLeft() == 0) {
                    throw new IllegalPickException("You don't have any picks left in the top row");
                }
                break;
            case BOTTOM_DECKABLE_ROW:
            case BOTTOM_BUILDING_ROW:
                if (player.getNumBottomPicksLeft() == 0) {
                    throw new IllegalPickException("You don't have any picks left in the bottom row");
                }
                break;
            default:
                throw new IllegalPickException("You can't pick this card");
        }
    }

    private boolean isExceptionalAddTopPickTurn(Player player, Game context) {
        return player.getNumTopPicksLeft() + player.getNumBottomPicksLeft() == 0
                && player.getAddTopPick() == 1;
    }

    private void handleExceptionalPick(Card card, int row, Game context, Player player) throws IllegalPickException, SQLException, ForcedPickException {
        switch (row) {
            case TOP_DECKABLE_ROW:
                pickDeckCardAndApplyEffects(card, context, player);
                break;
            case TOP_BUILDING_ROW:
                pickBuildingAndApplyImmediateEffects((Building) card, context, player);
                break;
            default:
                throw new IllegalPickException("You can't pick this card");
        }

        wasAdditionalTopPick = true;
        nextState(context);

    }


    /**
     * Shifts control to the verification phase by generating a new state instance.
     * <p>
     * Forwards control markers smoothly into {@link ControlState} and launches
     * {@link ControlState#controlPick(Game, boolean, Card, int)} to compute next eligible turn orders.
     * </p>
     *
     * @param context               the central game monitoring instance {@link Game}
     * @throws SQLException         if system transaction propagation hits internal blockages
     * @throws IllegalPickException if sequential selection validation hits a formatting failure
     * @throws ForcedPickException  if downstream parameters detect validation veto exceptions
     */
    @Override
    protected void nextState(Game context) throws SQLException, IllegalPickException, ForcedPickException {
        context.setCurrentState(new ControlState());
        context.getCurrentState().controlPick(context,wasAdditionalTopPick, selectedCard, row);
    }
    /**
     * Exposes the canonical text identifier assigned to this component state.
     *
     * @return a text String representing the state mode ("PICK")
     */
    @Override
    protected String getStateName() {
        return "PICK";
    }

}
