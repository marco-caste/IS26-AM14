package it.polimi.ingsw.am14.model.cards.buildings.visitors;

import it.polimi.ingsw.am14.model.cards.buildings.types.InventorsBuilding;
import it.polimi.ingsw.am14.model.gameLogic.*;
/**
 * A visitor that handles the evaluation of inventors buildings.
 * It uses the current {@link Game} and {@link Player} context to apply the inventors effects.
 */
public class InventorsVisitor implements BuildingVisitor {
    private Player currentPlayer;
    private final Game game;
    /**
     * Constructs a new {@code InventorsVisitor} with the specified game and player.
     *
     * @param g the current {@link Game} instance
     * @param p the {@link Player} applying the inventors effect
     */
    public InventorsVisitor(Game g,   Player p) {
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
     * Visits an inventors building and applies its specific effect to the current player.
     *
     * @param b the {@link InventorsBuilding} to visit
     */
    @Override
    public void visit(InventorsBuilding b) {
        b.applyEffect(currentPlayer, game);
    }
}
