package it.polimi.ingsw.am14.model.cards.buildings.visitors;

import it.polimi.ingsw.am14.model.cards.buildings.types.SustenanceEventBuilding;
import it.polimi.ingsw.am14.model.gameLogic.*;
/**
 * A visitor that handles the evaluation of sustenance event buildings.
 * It uses the current {@link Game} and {@link Player} context to apply the sustenance event effects.
 */
public class SustenanceEventVisitor implements BuildingVisitor {
    private Player currentPlayer;
    private final Game game;
    /**
     * Constructs a new {@code SustenanceEventVisitor} with the specified game and player.
     *
     * @param g the current {@link Game} instance
     * @param p the {@link Player} applying the sustenance event effect
     */
    public SustenanceEventVisitor(Game g,  Player p) {
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
     * Visits a sustenance event building and applies its specific effect to the current player.
     *
     * @param b the {@link SustenanceEventBuilding} to visit
     */
    @Override
    public void visit(SustenanceEventBuilding b) {
        b.applyEffect(currentPlayer, game);
    }
}
