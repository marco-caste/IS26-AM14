package it.polimi.ingsw.am14.model.cards.buildings.visitors;

import it.polimi.ingsw.am14.model.cards.buildings.types.PaintEventBuilding;
import it.polimi.ingsw.am14.model.gameLogic.*;
/**
 * A visitor that handles the evaluation of paint event buildings.
 * It uses the current {@link Game} and {@link Player} context to apply the paint event effects.
 */
public class PaintEventVisitor implements BuildingVisitor {
    private Player currentPlayer;
    private final Game game;
    /**
     * Constructs a new {@code PaintEventVisitor} with the specified game and player.
     *
     * @param g the current {@link Game} instance
     * @param p the {@link Player} applying the paint event effect
     */
    public PaintEventVisitor(Game g, Player p) {
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
     * Visits a paint event building and applies its specific effect to the current player.
     *
     * @param b the {@link PaintEventBuilding} to visit
     */
    @Override
    public void visit(PaintEventBuilding b) {
        b.applyEffect(currentPlayer, game);
    }
}
