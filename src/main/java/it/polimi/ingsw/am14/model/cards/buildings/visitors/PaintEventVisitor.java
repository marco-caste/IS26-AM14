package it.polimi.ingsw.am14.model.cards.buildings.visitors;

import it.polimi.ingsw.am14.model.cards.buildings.types.PaintEventBuilding;
import it.polimi.ingsw.am14.model.gameLogic.*;

public class PaintEventVisitor implements BuildingVisitor {
    private Player currentPlayer;
    private final Game game;

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

    @Override
    public void visit(PaintEventBuilding b) {
        b.applyEffect(currentPlayer, game);
    }
}
