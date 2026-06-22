package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.utils.MyJDBC;
import it.polimi.ingsw.am14.network.messages.events.EndGameEvent;
import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.buildings.visitors.EndGameVisitor;
import it.polimi.ingsw.am14.model.cards.deckCards.Builder;
import it.polimi.ingsw.am14.model.cards.deckCards.Inventor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *     Represents the state of the game when it has ended. This state is responsible for handling the end of the game, including calculating final scores and notifying observers with the end game event.
 * </p>
 */
class EndGameState extends GameState {

    @Override
    protected void nextState(Game context) {}

    /**
     * Overrides the endRound method to handle the end of the game, calculating final scores and notifying observers with the end game event.
     * @param context not null
     */
    @Override
    protected void endRound(Game context) throws SQLException {
        Board currentBoard = context.getBoard();

        currentBoard.getTurnOrderCard().resolveBonusOrMalus();

        currentBoard.switchDeckablesFromTop();

        currentBoard.idleEventsAndDiscardCharactersFromBottom();

        for (int i = currentBoard.toResolveOtherEventsRowSize() - 1; i >= 0; i--) {
            currentBoard.getFromResolveOtherEventsRow(i).applyEffect(null, context);
        }
        for (int i = 0; i < currentBoard.toResolveSustenanceEventsRowSize(); i++) {
            currentBoard.getFromResolveSustenanceEventRow(i).applyEffect(null, context);
        }
        currentBoard.discardEvents();

        ArrayList<Player> players = new ArrayList<>(context.getPlayers());

        for (Player p : players) {
            EndGameVisitor visitor = new EndGameVisitor(context, p);
            ArrayList<Builder> builders = new ArrayList<>(p.getTribe().getBuilders());
            ArrayList<Building> buildings = new ArrayList<>(p.getTribe().getBuildings());
            for (Building b : buildings) {
                b.accept(visitor);
                p.addToPrestigePoints(b.getPrestigePoints());
            }
            for (Builder b : builders) {
                p.addToPrestigePoints(b.getPrestigePoints());
            }
            p.addToPrestigePoints(
                    10 * (int) Math.floor((float) p.getTribe().getArtistsSize() / 2)
            );
            p.addToPrestigePoints(
                    p.getTribe().getInventors().stream().
                            collect(Collectors.groupingBy(Inventor::getInvention)).size() *
                            p.getTribe().getInventorsSize()
            );
        }

        List<Player> ranking = context.getPlayers().stream().
                sorted((p1, p2) -> {
                    if (p1.getNumPrestigePoints() > p2.getNumPrestigePoints())
                        return 1;
                    if (p1.getNumPrestigePoints() < p2.getNumPrestigePoints())
                        return -1;
                    else {
                        if(p1.getNumFood() > p2.getNumFood())
                            return 1;
                        if (p1.getNumFood() < p2.getNumFood())
                            return -1;
                        else
                            return 0;
                    }
                }).toList();

        //database saving and showing of the global ranking
        for(Player p : players) {
            MyJDBC.saveToDB(context.getGameId(), p.getNickname(), p.getNumPrestigePoints(), context.getNumPlayers());
        }
        List<String> globalRank = MyJDBC.readRanking(context.getNumPlayers());

        // observer pattern: sends event to event buffer
        context.notifyObservers(
                new EndGameEvent(context.getBoard(), ranking, context.getGameId(), globalRank)
        );

    }
}
