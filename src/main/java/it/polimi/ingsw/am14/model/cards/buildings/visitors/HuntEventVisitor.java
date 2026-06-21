package it.polimi.ingsw.am14.model.cards.buildings.visitors;

import it.polimi.ingsw.am14.model.cards.buildings.types.HuntEventBuilding;
import it.polimi.ingsw.am14.model.gameLogic.*;
/**
 * A visitor that handles the evaluation of hunt event buildings.
 * It uses the current {@link Game} and {@link Player} context to apply the hunt event effects.
 */
public class HuntEventVisitor implements BuildingVisitor {
    private Player currentPlayer;
    private final Game game;
    /**
     * Constructs a new {@code HuntEventVisitor} with the specified game and player.
     *
     * @param g the current {@link Game} instance
     * @param p the {@link Player} applying the hunt event effect
     */
    public HuntEventVisitor(Game g,  Player p) {
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
     * Visits a hunt event building and applies its specific effect to the current player.
     *
     * @param b the {@link HuntEventBuilding} to visit
     */
    @Override
    public void visit(HuntEventBuilding b) {
        b.applyEffect(currentPlayer, game);
    }
}
