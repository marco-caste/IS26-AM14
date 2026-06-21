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

class PickState extends GameState {

    boolean wasAdditionalTopPick ;
    Card selectedCard;
    int row;

    // TODO: dobbiamo andare a togliere l'optional per card in quanto non più necessario. Se lo tolgo qui ho un errore nel clientController

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

        System.out.println("Current player: " + currentPlayer.getNickname());
        System.out.println("Player trying to pick: " + player.getNickname());
        System.out.println("Current player index: " + context.getCurrentPlayerIndex());
        System.out.println("Card : " + selectedCard);
        System.out.println(context.getNumPlayers());

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



    @Override
    protected void nextState(Game context) throws SQLException, IllegalPickException, ForcedPickException {
        context.setCurrentState(new ControlState());
        context.getCurrentState().controlPick(context,wasAdditionalTopPick, selectedCard, row);
    }

    @Override
    protected String getStateName() {
        return "PICK";
    }

}
