package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.events.CardPickedEvent;
import it.polimi.ingsw.am14.exceptions.ForcedPickException;
import it.polimi.ingsw.am14.exceptions.IllegalPickException;
import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.buildings.visitors.ImmediateVisitor;
import it.polimi.ingsw.am14.model.cards.buildings.visitors.SetVisitor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class ControlState extends GameState {

    @Override
    protected void nextState(Game context) {
        context.setCurrentState(new PositionState());
    }


    public void checkPick( Player player , Game context, Card selectedCard, int row , char playerTotemPosition, int availableTopCharacters, int availableBottomCharacters) throws SQLException {
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
        PickState.TotemSlot nextSlot = findNextPlayerTotemAfterPosition(context, currentTotemPosition);

        boolean currentPlayerCanStillPick = determineIfCurrentPlayerCanContinuePickingFromBothRows(nextSlot.player, context, availableTopCharacters, availableBottomCharacters, nextSlot.position);
        if (currentPlayerCanStillPick) {
            return;
        }
        currentTotemPosition = nextSlot.position;
    }
}

private PickState.TotemSlot findFirstPlayerTotemOnOffersPath(Game context) {
    for (Character key : context.getBoard().getOffersPath().offersPathKeySet()) {
        if (context.getBoard().getOffersPath().offersPathGet(key).getPlayerTotem() != null) {
            return new PickState.TotemSlot(context.getBoard().getOffersPath().offersPathGet(key).getPlayerTotem(), key);
        }
    }
    return null;
}

private PickState.TotemSlot findNextPlayerTotemAfterPosition(Game context, Character currentPosition) {
    for (Character key :  context.getBoard().getOffersPath().offersPathKeySet()) {
        if (key > currentPosition && context.getBoard().getOffersPath().offersPathGet(key).getPlayerTotem() != null) {
            return new PickState.TotemSlot(context.getBoard().getOffersPath().offersPathGet(key).getPlayerTotem(), key);
        }
    }
    return null;
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
                        context.getGameId()
                )
        );
    }









}
