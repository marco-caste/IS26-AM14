package it.polimi.ingsw.am14.model.cards.buildings.visitors;

import it.polimi.ingsw.am14.model.cards.buildings.types.SetBuilding;
import it.polimi.ingsw.am14.model.gameLogic.*;
/**
 * A visitor that handles the evaluation of set buildings.
 * It uses the current {@link Game} and {@link Player} context to apply the set effects.
 */
public class SetVisitor implements BuildingVisitor {
    private Player currentPlayer;
    private final Game game;
    /**
     * Constructs a new {@code SetVisitor} with the specified game and player.
     *
     * @param g the current {@link Game} instance
     * @param p the {@link Player} applying the set effect
     */
    public SetVisitor(Game g,  Player p) {
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
     * Visits a set building and applies its specific effect to the current player.
     *
     * @param b the {@link SetBuilding} to visit
     */
    @Override
    public void visit(SetBuilding b) {
        b.applyEffect(currentPlayer, game);
    }
}
