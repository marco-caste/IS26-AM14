package it.polimi.ingsw.am14.model.cards.buildings.visitors;

import it.polimi.ingsw.am14.model.cards.buildings.types.ShamanBonusEventBuilding;
import it.polimi.ingsw.am14.model.gameLogic.*;
/**
 * A visitor that handles the evaluation of shaman bonus event buildings.
 * It uses the current {@link Game} and {@link Player} context to apply the shaman bonus event effects.
 */
public class ShamanBonusEventVisitor implements BuildingVisitor {
    private Player currentPlayer;
    private final Game game;
    /**
     * Constructs a new {@code ShamanBonusEventVisitor} with the specified game and player.
     *
     * @param g the current {@link Game} instance
     * @param p the {@link Player} applying the shaman bonus event effect
     */
    public ShamanBonusEventVisitor(Game g, Player p) {
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
     * Visits a shaman bonus event building and applies its specific effect to the current player.
     *
     * @param b the {@link ShamanBonusEventBuilding} to visit
     */
    @Override
    public void visit(ShamanBonusEventBuilding b) {
        b.applyEffect(currentPlayer, game);
    }
}
