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


class EndGameState extends GameState {

    // TODO: of unknown necessity
    @Override
    protected void nextState(Game context) {}

    /**
     * <p>
     *     calculates the final ranking of each player, according to the rules
     *     - solves all the events on the board (both top and bottom rows)
     *     - for each player, calculates the prestige points according to the rules
     *     - sorts the players in a final list
     * </p>
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
                        if (p1.getNumFood() > p2.getNumFood())
                            return 1;
                        if (p1.getNumFood() < p2.getNumFood())
                            return -1;
                        else
                            return 0;
                    }
                }).toList();

        // observer pattern: sends event to event buffer
        context.notifyObservers(
                new EndGameEvent(context.getBoard(), ranking, context.getGameId())
        );

        // at the end, we will display a ranking of players, in the form of
        // player - prestigePoints - foodAvailable
        // sorted by prestigePoints and food.

        //database saving and showing of the global ranking
        for(Player p : players) {
            MyJDBC.saveToDB(context.getGameId(), p.getNickname(), p.getNumPrestigePoints(), context.getNumPlayers());
        }
        List<String> rank = MyJDBC.readRanking(context.getNumPlayers());

        //TODO capire come vogliamo che venga mostrata la classifica globale, se con un evento o con un altro metodo
    }
}
