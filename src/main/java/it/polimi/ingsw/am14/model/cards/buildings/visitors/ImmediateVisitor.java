package it.polimi.ingsw.am14.model.cards.buildings.visitors;

import it.polimi.ingsw.am14.model.cards.buildings.types.HuntEventBuilding;
import it.polimi.ingsw.am14.model.cards.buildings.types.ImmediateBuilding;
import it.polimi.ingsw.am14.model.gameLogic.*;
/**
 * A visitor that handles the evaluation of immediate buildings.
 * It uses the current {@link Game} and {@link Player} context to apply immediate effects.
 */
public class ImmediateVisitor implements BuildingVisitor {
    private Player currentPlayer;
    private final Game game;
    /**
     * Constructs a new {@code ImmediateVisitor} with the specified game and player.
     *
     * @param g the current {@link Game} instance
     * @param p the {@link Player} applying the immediate effect
     */
    public ImmediateVisitor(Game g,  Player p) {
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
     * Visits an immediate building and applies its specific effect to the current player.
     *
     * @param b the {@link ImmediateBuilding} to visit
     */
    @Override
    public void visit(ImmediateBuilding b) {
        b.applyEffect(currentPlayer, game);
    }
}
