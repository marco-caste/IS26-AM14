package it.polimi.ingsw.am14.model.cards.buildings.types;

import it.polimi.ingsw.am14.model.cards.buildings.visitors.BuildingVisitor;
import it.polimi.ingsw.am14.model.cards.buildings.effects.EffectStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuildingTypesTest {

    private VisitorProbe visitor;
    private final EffectStrategy noOpEffect = (player, game) -> { };

    @BeforeEach
    void setUp() {
        visitor = new VisitorProbe();
    }

    @Test
    void endGameBuildingAcceptDispatchesToEndGameVisit() {
        new EndGameBuilding(1, 1, 1, 1, noOpEffect).accept(visitor);

        assertEquals(1, visitor.endGameVisits);
        assertEquals(1, visitor.totalVisits);
    }

    @Test
    void huntEventBuildingAcceptDispatchesToHuntEventVisit() {
        new HuntEventBuilding(1, 1, 1, 2, noOpEffect).accept(visitor);

        assertEquals(1, visitor.huntVisits);
        assertEquals(1, visitor.totalVisits);
    }

    @Test
    void immediateBuildingAcceptDispatchesToImmediateVisit() {
        new ImmediateBuilding(1, 1, 1, 3, noOpEffect).accept(visitor);

        assertEquals(1, visitor.immediateVisits);
        assertEquals(1, visitor.totalVisits);
    }

    @Test
    void inventorsBuildingAcceptDispatchesToInventorsVisit() {
        new InventorsBuilding(1, 1, 1, 4, noOpEffect).accept(visitor);

        assertEquals(1, visitor.inventorsVisits);
        assertEquals(1, visitor.totalVisits);
    }

    @Test
    void paintEventBuildingAcceptDispatchesToPaintEventVisit() {
        new PaintEventBuilding(1, 1, 1, 5, noOpEffect).accept(visitor);

        assertEquals(1, visitor.paintVisits);
        assertEquals(1, visitor.totalVisits);
    }

    @Test
    void setBuildingAcceptDispatchesToSetVisit() {
        new SetBuilding(1, 1, 1, 6, noOpEffect).accept(visitor);

        assertEquals(1, visitor.setVisits);
        assertEquals(1, visitor.totalVisits);
    }

    @Test
    void shamanBonusEventBuildingAcceptDispatchesToShamanBonusVisit() {
        new ShamanBonusEventBuilding(1, 1, 1, 7, noOpEffect).accept(visitor);

        assertEquals(1, visitor.shamanBonusVisits);
        assertEquals(1, visitor.totalVisits);
    }

    @Test
    void shamanMalusEventBuildingAcceptDispatchesToShamanMalusVisit() {
        new ShamanMalusEventBuilding(1, 1, 1, 8, noOpEffect).accept(visitor);

        assertEquals(1, visitor.shamanMalusVisits);
        assertEquals(1, visitor.totalVisits);
    }

    @Test
    void sustenanceEventBuildingAcceptDispatchesToSustenanceVisit() {
        new SustenanceEventBuilding(1, 1, 1, 9, noOpEffect).accept(visitor);

        assertEquals(1, visitor.sustenanceVisits);
        assertEquals(1, visitor.totalVisits);
    }

    @Test
    void passiveBuildingAcceptDoesNotDispatch() {
        new PassiveBuilding(1, 1, 1, 10, noOpEffect).accept(visitor);

        assertEquals(0, visitor.totalVisits);
    }

    private static class VisitorProbe implements BuildingVisitor {
        private int totalVisits;
        private int endGameVisits;
        private int huntVisits;
        private int immediateVisits;
        private int inventorsVisits;
        private int paintVisits;
        private int setVisits;
        private int shamanBonusVisits;
        private int shamanMalusVisits;
        private int sustenanceVisits;

        @Override
        public void visit(EndGameBuilding building) {
            totalVisits++;
            endGameVisits++;
        }

        @Override
        public void visit(HuntEventBuilding building) {
            totalVisits++;
            huntVisits++;
        }

        @Override
        public void visit(ImmediateBuilding building) {
            totalVisits++;
            immediateVisits++;
        }

        @Override
        public void visit(InventorsBuilding building) {
            totalVisits++;
            inventorsVisits++;
        }

        @Override
        public void visit(PaintEventBuilding building) {
            totalVisits++;
            paintVisits++;
        }

        @Override
        public void visit(SetBuilding building) {
            totalVisits++;
            setVisits++;
        }

        @Override
        public void visit(ShamanBonusEventBuilding building) {
            totalVisits++;
            shamanBonusVisits++;
        }

        @Override
        public void visit(ShamanMalusEventBuilding building) {
            totalVisits++;
            shamanMalusVisits++;
        }

        @Override
        public void visit(SustenanceEventBuilding building) {
            totalVisits++;
            sustenanceVisits++;
        }
    }
}

