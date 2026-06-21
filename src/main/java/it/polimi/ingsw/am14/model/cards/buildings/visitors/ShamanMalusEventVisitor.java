package it.polimi.ingsw.am14.model.cards.buildings.visitors;

import it.polimi.ingsw.am14.model.cards.buildings.types.ShamanMalusEventBuilding;
import it.polimi.ingsw.am14.model.gameLogic.*;
/**
 * A visitor that handles the evaluation of shaman malus event buildings.
 * It uses the current {@link Game} and {@link Player} context to apply the shaman malus event effects.
 */
public class ShamanMalusEventVisitor implements BuildingVisitor {
    private Player currentPlayer;
    private final Game game;
    /**
     * Constructs a new {@code ShamanMalusEventVisitor} with the specified game and player.
     *
     * @param g the current {@link Game} instance
     * @param p the {@link Player} applying the shaman malus event effect
     */
    public ShamanMalusEventVisitor(Game g,  Player p) {
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
     * Visits a shaman malus event building and applies its specific effect to the current player.
     *
     * @param b the {@link ShamanMalusEventBuilding} to visit
     */
    @Override
    public void visit(ShamanMalusEventBuilding b) {
        b.applyEffect(currentPlayer, game);
    }
}
