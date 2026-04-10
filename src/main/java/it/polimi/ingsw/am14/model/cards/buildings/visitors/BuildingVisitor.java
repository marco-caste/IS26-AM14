package it.polimi.ingsw.am14.model.cards.buildings.visitors;

import it.polimi.ingsw.am14.model.cards.buildings.types.*;

public interface BuildingVisitor {
    default void visit(EndGameBuilding building) {}
    default void visit(HuntEventBuilding building) {}
    default void visit(ImmediateBuilding building) {}
    default void visit(InventorsBuilding building) {}
    default void visit(PaintEventBuilding building) {}
    default void visit(SetBuilding building) {}
    default void visit(ShamanBonusEventBuilding building) {}
    default void visit(ShamanMalusEventBuilding building) {}
    default void visit(SustenanceEventBuilding building) {}
}
