package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.network.messages.events.CardPickedEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;



class ControlState extends GameState {

    boolean goToEndRound;
    Card selectedCard;
    int row;
    Player pickingPlayer;

    @Override
    protected void controlPick(Game context, boolean wasAdditionalPick, Card card, int rowChosen) throws SQLException {

        goToEndRound = false;
        selectedCard=card;
        row = rowChosen;

        // we don't need to check anything in this case --> obbligatorio in quanto i controllo non sono pensati per funzionare nel caso dell addTopPick
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
        pickingPlayer = findFirstPlayerTotemOnOffersPath(context).getPlayer();
        char playerTotemPosition = findFirstPlayerTotemOnOffersPath(context).getPosition();


        //Check Phase Start
        int availableTopCharacters = countAvailableCharacterCardsFromTopRow(context, pickingPlayer);
        int availableBottomCharacters = countAvailableCharacterCardsFromBottomRow(context, pickingPlayer);

        // if the current player has still picks left, we check if he can pick from both rows, otherwise we find the next player in turn order
        if (pickingPlayer.getNumTopPicksLeft() != 0 || pickingPlayer.getNumBottomPicksLeft() != 0) {
            boolean currentPlayerHasMorePicks = determineIfCurrentPlayerCanContinuePickingFromBothRows(pickingPlayer, context, availableTopCharacters, availableBottomCharacters, playerTotemPosition);
            if (!currentPlayerHasMorePicks) {
                findAndActivateNextPlayerInTurnOrder(context, availableTopCharacters, availableBottomCharacters, playerTotemPosition);
            }

        } else {  // dobbiamo analizzare cosa avviene dopo il pick del player corrente che ha appena finito i pick
            finalizeCurrentPlayerTurn(context, pickingPlayer, playerTotemPosition); // riportiamo il player corrente nella turnOrderCard


            if (context.getCurrentPlayerIndex() == context.getNumPlayers()) { // se il player era l'ultimo

                handleAllPlayersFinishedTheirPicksPhase(context, availableTopCharacters); // verifico se devo fare la transizione di stato

            } else  {  // se il player non è l'ultimo --> verifico se il prossimo player può anora pickare
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
        System.out.println(" turnOrderCard : " + context.getBoard().getTurnOrderCard().getPlayerOrder().getFirst().getNickname());
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


    private boolean determineIfCurrentPlayerCanContinuePickingFromBothRows(Player player, Game context, int availableTopCharacters, int availableBottomCharacters, Character playerTotem) throws SQLException {
        boolean canPickFromTopRow = canPlayerAffordAnyBuildingInTopRow(player, context, availableTopCharacters);
        boolean canPickFromBottomRow = canPlayerAffordAnyBuildingInBottomRow(player, context, availableBottomCharacters);

        if (canPickFromTopRow || canPickFromBottomRow) {
            this.update(player, selectedCard, row, context, false);
            return true;
        }

        //se non può pickare --> concludo il suo turo
        finalizeCurrentPlayerTurn(context, player, playerTotem);

        return (handleAllPlayersFinishedTheirPicksPhase(context, availableTopCharacters));
    }



    private void findAndActivateNextPlayerInTurnOrder(Game context, int availableTopCharacters, int availableBottomCharacters, Character playerTotemPosition) throws SQLException {
        Character currentTotemPosition = playerTotemPosition;
        while (context.getCurrentPlayerIndex() < context.getNumPlayers()) {
            TotemSlot nextSlot = findNextPlayerTotemAfterPosition(context, currentTotemPosition);

            boolean currentPlayerCanStillPick = determineIfCurrentPlayerCanContinuePickingFromBothRows(nextSlot.getPlayer(), context, availableTopCharacters, availableBottomCharacters, nextSlot.getPosition());
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

        // va messo perchè determineIfCurrentPlayerCanContinuePickingFromBothRows viene chiamato anche per i player che non stanno pickando durante la fase di controllo
        // quindi per questi player che non stanno pickando verrà chiamato comunque l'update. In quel caso però passiamo la carta = null in quanto non dobbiamo registrare un pick
        // ma eventualemente solo un update della turnorderCard e trackCard nel caso nemmeno il player successivo possa skippare.

        if(pickingPlayer!= null){
            if(!(pickingPlayer.equals(player))) {
                card = null;
            }
        }

        System.out.println(" turnOrderCardInUpdate: " + currentTurnOrderCard);

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

