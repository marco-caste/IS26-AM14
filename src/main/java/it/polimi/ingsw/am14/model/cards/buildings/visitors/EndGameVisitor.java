package it.polimi.ingsw.am14.model.cards.buildings.visitors;

import it.polimi.ingsw.am14.model.cards.buildings.types.EndGameBuilding;
import it.polimi.ingsw.am14.model.gameLogic.*;
/**
 * A visitor that handles the evaluation of end-game buildings.
 * It uses the current {@link Game} and {@link Player} context to apply the final effects.
 */
public class EndGameVisitor implements BuildingVisitor {
    private Player currentPlayer;
    private final Game game;
    /**
     * Constructs a new {@code EndGameVisitor} with the specified game and player.
     *
     * @param g the current {@link Game} instance
     * @param p the {@link Player} applying the end-game effect
     */
    public EndGameVisitor(Game g, Player p) {
        this.currentPlayer = p;
        this.game = g;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    /**
     * Visits an end-game building and applies its specific effect to the current player.
     *
     * @param b the {@link EndGameBuilding} to visit
     */
    @Override
    public void visit(EndGameBuilding b) {
        b.applyEffect(currentPlayer, game);
    }
}
