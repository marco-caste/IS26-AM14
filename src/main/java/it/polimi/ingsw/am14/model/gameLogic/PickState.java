package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.network.messages.events.CardPickedEvent;
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

    public static final class TotemSlot {
        private final Player player;
        private final char position;

        private TotemSlot(Player player, char position) {
            this.player = player;
            this.position = position;
        }
    }

    // TODO: RIMUOVERE CHECK DELLA CARTA PRESENTE SULLA BOARD

    @Override
    protected void pickCard(Game context, Player player, Optional<Card> card, int row)
            throws IllegalPickException, ForcedPickException, WrongTurnException, SQLException {

        // In questo progetto `null` equivale a skip: lo normalizziamo a Optional vuoto.
        Optional<Card> safeCard = Optional.ofNullable(card).orElse(Optional.empty());
        TotemSlot currentSlot = findFirstPlayerTotemOnOffersPath(context);
        Player currentPlayer = currentSlot == null ? null : currentSlot.player;
        Character playerTotemPosition = currentSlot == null ? null : currentSlot.position;

        // it's not player's turn
        if (!player.equals(currentPlayer) && context.getCurrentPlayerIndex() != context.getNumPlayers()) {
            throw new WrongTurnException("It's not " + player.getNickname() + "'s turn to pick a card");
        }

        int availableTopCharacters = countAvailableCharacterCardsFromTopRow(context, player);
        int availableBottomCharacters = countAvailableCharacterCardsFromBottomRow(context, player);

        // skip
        if (safeCard.isEmpty()) {
            handlePlayerSkipAction(player, context, availableTopCharacters, availableBottomCharacters, playerTotemPosition);
            return;
        }

        Card selectedCard = safeCard.get();
        if (!selectedCard.isPickable(player)){
            throw new IllegalPickException("You can't pick this card");
        }

        // exceptional pick due to additional pick building
        if (isExceptionalAddTopPickTurn(player, context)) {
            handleExceptionalPick(selectedCard, row, context, player);
            return;
        }

        validateRowPicksAvailability(player, row);

        switch (row) {
            case TOP_DECKABLE_ROW:
                player.removeSingleTopPick();
                pickDeckCardAndApplyEffects(selectedCard, context, player);
                context.getBoard().removeFromTopDeckableRow(selectedCard);
                availableTopCharacters--;
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
                availableBottomCharacters--;
                break;
            case BOTTOM_BUILDING_ROW:
                player.removeSingleBottomPick();
                pickBuildingAndApplyImmediateEffects((Building) selectedCard, context, player);
                context.getBoard().removeFromBottomBuildingRow(selectedCard);
                break;
            default:
                throw new IllegalPickException("You can't pick this card");
        }

        // CHECK PLAYER SUCCESSIVO + CLIENT UPDATE:

        // Il prossimo pick puo restare al player corrente o passare al successivo.
        if (player.getNumTopPicksLeft() != 0 || player.getNumBottomPicksLeft() != 0) {
            boolean currentPlayerHasMorePicks = determineIfCurrentPlayerCanContinuePickingFromBothRows(player, context, availableTopCharacters, availableBottomCharacters, playerTotemPosition);
            if (!currentPlayerHasMorePicks) {
                findAndActivateNextPlayerInTurnOrder(context, availableTopCharacters, availableBottomCharacters, playerTotemPosition);
            }
            // client update
            this.update(player, selectedCard, row, context, false);
        } else {  // dobbiamo analizzare cosa avviene dopo il pick del player corrente che ha appena finito i pick
            finalizeCurrentPlayerTurn(context, player, playerTotemPosition); // riportiamo il player corrente nella turnOrderCard
            // client update
            this.update(player, selectedCard, row, context, true);

            if (context.getCurrentPlayerIndex() == context.getNumPlayers()) { // se il player era l'ultimo
                handleAllPlayersFinishedTheirPicksPhase(context, availableTopCharacters); // verifico se devo fare la transizione di stato

            } else  {  // se il player non è l'ultimo --> verifico se il prossimo player può anora pickare
                findAndActivateNextPlayerInTurnOrder(context, availableTopCharacters, availableBottomCharacters, playerTotemPosition);
            }
        }
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

    private void handlePlayerSkipAction(Player player,
                            Game context,
                            int availableTopCharacters,
                            int availableBottomCharacters,
                            Character playerTotemPosition) throws ForcedPickException, SQLException {
        if (!((availableTopCharacters == 0 || player.getNumTopPicksLeft() == 0)
                && (availableBottomCharacters == 0 || player.getNumBottomPicksLeft() == 0))) {
            throw new ForcedPickException("You can't skip the turn you have to pick the remaining character Card First");
        }

        player.setNumTopPicksLeft(0);
        player.setNumBottomPicksLeft(0);

        if (context.getCurrentPlayerIndex() < context.getNumPlayers()) {
            finalizeCurrentPlayerTurn(context, player, playerTotemPosition);
            // client update
            this.update(player, null, -1, context, true);
            findAndActivateNextPlayerInTurnOrder(context, availableTopCharacters, availableBottomCharacters, playerTotemPosition);
        } else {
            // client update
            this.update(player, null, -1, context, true);
            nextState(context);
        }
    }

    private boolean isExceptionalAddTopPickTurn(Player player, Game context) {
        return player.getNumTopPicksLeft() + player.getNumBottomPicksLeft() == 0
                && player.getAddTopPick() == 1;
    }


    private void handleExceptionalPick(Card card, int row, Game context, Player player) throws IllegalPickException, SQLException {
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

        // client update
        this.update(player, card, row, context, true);

        nextState(context);
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

    private void finalizeCurrentPlayerTurn(Game context, Player player, Character playerTotemPosition) {
        context.incrementCurrentPlayerIndexBy(1);
        context.getBoard().getOffersPath().offersPathGet(playerTotemPosition).setPlayerTotem(null);
        context.getBoard().getTurnOrderCard().addPlayer(player);
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

    private boolean determineIfCurrentPlayerCanContinuePickingFromBothRows(Player player, Game context, int availableTopCharacters, int availableBottomCharacters, Character playerTotem) throws SQLException {
        boolean canPickFromTopRow = canPlayerAffordAnyBuildingInTopRow(player, context, availableTopCharacters);
        boolean canPickFromBottomRow = canPlayerAffordAnyBuildingInBottomRow(player, context, availableBottomCharacters);

        if (canPickFromTopRow || canPickFromBottomRow) {
            return true;
        }

        finalizeCurrentPlayerTurn(context, player, playerTotem);
        return handleAllPlayersFinishedTheirPicksPhase(context, availableTopCharacters);
    }


    private void findAndActivateNextPlayerInTurnOrder(Game context, int availableTopCharacters, int availableBottomCharacters, Character playerTotemPosition) throws SQLException {
        Character currentTotemPosition = playerTotemPosition;
        while (context.getCurrentPlayerIndex() < context.getNumPlayers()) {
            TotemSlot nextSlot = findNextPlayerTotemAfterPosition(context, currentTotemPosition);

            boolean currentPlayerCanStillPick = determineIfCurrentPlayerCanContinuePickingFromBothRows(nextSlot.player, context, availableTopCharacters, availableBottomCharacters, nextSlot.position);
            if (currentPlayerCanStillPick) {
                return;
            }
            currentTotemPosition = nextSlot.position;
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

    private boolean handleAllPlayersFinishedTheirPicksPhase(Game context, int availableTopCharacters) throws SQLException {
        // Quando tutti i player hanno finito i loro pick normali, cerchiamo se c'è un player con pick aggiuntivo
        // Se lo troviamo e può ancora pickare dalla riga superiore, gli diamo la possibilità di farlo
        // Altrimenti, passiamo alla fase successiva (fine del turno di pick)
        if (context.getCurrentPlayerIndex() == context.getNumPlayers()) {
            Optional<Player> playerWithAdditionalTopPick = context.getPlayers().stream()
                    .filter(player -> player.getAddTopPick() == 1)
                    .findFirst();

            if (playerWithAdditionalTopPick.isPresent()) {
                Player playerWithExtraTop = playerWithAdditionalTopPick.get();
                if (!canPlayerAffordAnyBuildingInTopRow(playerWithExtraTop, context, availableTopCharacters)) {
                    nextState(context);
                }
            } else {
                nextState(context);
            }


            return true;
        }
        return false;
    }

    private void update(Player player, Card card, int row, Game context, boolean wasLastPick) {
        Optional<ArrayList<Player>> currentTurnOrderCard =
                !wasLastPick ? Optional.empty() :
                Optional.of(context.getBoard().getTurnOrderCard().getPlayerOrder());

        // observer pattern: sends event to event buffer
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

    @Override
    protected void nextState(Game context) throws SQLException {
        context.setCurrentPlayerIndex(0);
        if (context.getCurrentRound() == 1) {  //TODO
            context.setCurrentState(new EndGameState());
            context.getCurrentState().endRound(context);
        } else {
            context.setCurrentState(new EndRoundState());
            context.getCurrentState().endRound(context);
        }
    }

    @Override
    protected String getStateName() {
        return "PICK";
    }
}
