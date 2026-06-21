package it.polimi.ingsw.am14.model.cards.buildings.visitors;

import it.polimi.ingsw.am14.model.cards.buildings.types.*;
/**
 * Interface that implements the Visitor pattern for different types of buildings.
 * It provides an empty default implementation for each visit method.
 */
public interface BuildingVisitor {
    /**
     * Visits an end game building.
     *
     * @param building the {@link EndGameBuilding} to visit
     */
    default void visit(EndGameBuilding building) {}
    /**
     * Visits a hunt event building.
     *
     * @param building the {@link HuntEventBuilding} to visit
     */
    default void visit(HuntEventBuilding building) {}
    /**
     * Visits an immediate building.
     *
     * @param building the {@link ImmediateBuilding} to visit
     */
    default void visit(ImmediateBuilding building) {}
    /**
     * Visits an inventors building.
     *
     * @param building the {@link InventorsBuilding} to visit
     */
    default void visit(InventorsBuilding building) {}
    /**
     * Visits a paint event building.
     *
     * @param building the {@link PaintEventBuilding} to visit
     */
    default void visit(PaintEventBuilding building) {}
    /**
     * Visits a set building.
     *
     * @param building the {@link SetBuilding} to visit
     */
    default void visit(SetBuilding building) {}
    /**
     * Visits a shaman bonus event building.
     *
     * @param building the {@link ShamanBonusEventBuilding} to visit
     */
    default void visit(ShamanBonusEventBuilding building) {}
    /**
     * Visits a shaman malus event building.
     *
     * @param building the {@link ShamanMalusEventBuilding} to visit
     */
    default void visit(ShamanMalusEventBuilding building) {}
    /**
     * Visits a sustenance event building.
     *
     * @param building the {@link SustenanceEventBuilding} to visit
     */
    default void visit(SustenanceEventBuilding building) {}
}
