package it.polimi.ingsw.am14.model.cards.buildings.visitors;

import it.polimi.ingsw.am14.model.cards.buildings.types.ShamanBonusEventBuilding;
import it.polimi.ingsw.am14.model.gameLogic.*;

public class ShamanBonusEventVisitor implements BuildingVisitor {
    private Player currentPlayer;
    private final Game game;

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

    @Override
    public void visit(ShamanBonusEventBuilding b) {
        b.applyEffect(currentPlayer, game);
    }
}
