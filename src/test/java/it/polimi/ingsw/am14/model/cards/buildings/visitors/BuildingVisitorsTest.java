package it.polimi.ingsw.am14.model.cards.buildings.visitors;

import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.buildings.effects.EffectStrategy;
import it.polimi.ingsw.am14.model.cards.buildings.types.*;
import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class BuildingVisitorsTest {

    private Player playerOne;
    private Player playerTwo;

    private void assertCurrentPlayerApi(Object visitor) {
        try {
            Method getter = visitor.getClass().getMethod("getCurrentPlayer");
            Method setter = visitor.getClass().getMethod("setCurrentPlayer", Player.class);

            assertSame(playerOne, getter.invoke(visitor));
            setter.invoke(visitor, playerTwo);
            assertSame(playerTwo, getter.invoke(visitor));
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError("Visitor API get/setCurrentPlayer should be available", ex);
        }
    }

    private static class SpyEffect implements EffectStrategy {
        private int calls;
        private Player lastPlayer;

        @Override
        public void applyEffect(Player player, Game game) {
            calls++;
            lastPlayer = player;
        }
    }

    @BeforeEach
    void setUp() {
        playerOne = new Player("P1", TotemColor.WHITE);
        playerTwo = new Player("P2", TotemColor.BLUE);
    }

    @Test
    void endGameVisitorVisitsAndUsesCurrentPlayer() {
        SpyEffect effect = new SpyEffect();
        Building building = new EndGameBuilding(1, 1, 1, 1, effect);
        EndGameVisitor visitor = new EndGameVisitor(null, playerOne);

        assertCurrentPlayerApi(visitor);
        visitor.setCurrentPlayer(playerTwo);
        building.accept(visitor);

        assertEquals(1, effect.calls);
        assertSame(playerTwo, effect.lastPlayer);
    }

    @Test
    void huntEventVisitorVisitsAndUsesCurrentPlayer() {
        SpyEffect effect = new SpyEffect();
        Building building = new HuntEventBuilding(1, 1, 1, 2, effect);
        HuntEventVisitor visitor = new HuntEventVisitor(null, playerOne);

        assertCurrentPlayerApi(visitor);
        visitor.setCurrentPlayer(playerTwo);
        building.accept(visitor);

        assertEquals(1, effect.calls);
        assertSame(playerTwo, effect.lastPlayer);
    }

    @Test
    void immediateVisitorVisitsAndUsesCurrentPlayer() {
        SpyEffect effect = new SpyEffect();
        Building building = new ImmediateBuilding(1, 1, 1, 3, effect);
        ImmediateVisitor visitor = new ImmediateVisitor(null, playerOne);

        assertCurrentPlayerApi(visitor);
        visitor.setCurrentPlayer(playerTwo);
        building.accept(visitor);

        assertEquals(1, effect.calls);
        assertSame(playerTwo, effect.lastPlayer);
    }

    @Test
    void inventorsVisitorVisitsAndUsesCurrentPlayer() {
        SpyEffect effect = new SpyEffect();
        Building building = new InventorsBuilding(1, 1, 1, 4, effect);
        InventorsVisitor visitor = new InventorsVisitor(null, playerOne);

        assertCurrentPlayerApi(visitor);
        visitor.setCurrentPlayer(playerTwo);
        building.accept(visitor);

        assertEquals(1, effect.calls);
        assertSame(playerTwo, effect.lastPlayer);
    }

    @Test
    void paintEventVisitorVisitsAndUsesCurrentPlayer() {
        SpyEffect effect = new SpyEffect();
        Building building = new PaintEventBuilding(1, 1, 1, 5, effect);
        PaintEventVisitor visitor = new PaintEventVisitor(null, playerOne);

        assertCurrentPlayerApi(visitor);
        visitor.setCurrentPlayer(playerTwo);
        building.accept(visitor);

        assertEquals(1, effect.calls);
        assertSame(playerTwo, effect.lastPlayer);
    }

    @Test
    void setVisitorVisitsAndUsesCurrentPlayer() {
        SpyEffect effect = new SpyEffect();
        Building building = new SetBuilding(1, 1, 1, 6, effect);
        SetVisitor visitor = new SetVisitor(null, playerOne);

        assertCurrentPlayerApi(visitor);
        visitor.setCurrentPlayer(playerTwo);
        building.accept(visitor);

        assertEquals(1, effect.calls);
        assertSame(playerTwo, effect.lastPlayer);
    }

    @Test
    void shamanBonusEventVisitorVisitsAndUsesCurrentPlayer() {
        SpyEffect effect = new SpyEffect();
        Building building = new ShamanBonusEventBuilding(1, 1, 1, 7, effect);
        ShamanBonusEventVisitor visitor = new ShamanBonusEventVisitor(null, playerOne);

        assertCurrentPlayerApi(visitor);
        visitor.setCurrentPlayer(playerTwo);
        building.accept(visitor);

        assertEquals(1, effect.calls);
        assertSame(playerTwo, effect.lastPlayer);
    }

    @Test
    void shamanMalusEventVisitorVisitsAndUsesCurrentPlayer() {
        SpyEffect effect = new SpyEffect();
        Building building = new ShamanMalusEventBuilding(1, 1, 1, 8, effect);
        ShamanMalusEventVisitor visitor = new ShamanMalusEventVisitor(null, playerOne);

        assertCurrentPlayerApi(visitor);
        visitor.setCurrentPlayer(playerTwo);
        building.accept(visitor);

        assertEquals(1, effect.calls);
        assertSame(playerTwo, effect.lastPlayer);
    }

    @Test
    void sustenanceEventVisitorVisitsAndUsesCurrentPlayer() {
        SpyEffect effect = new SpyEffect();
        Building building = new SustenanceEventBuilding(1, 1, 1, 9, effect);
        SustenanceEventVisitor visitor = new SustenanceEventVisitor(null, playerOne);

        assertCurrentPlayerApi(visitor);
        visitor.setCurrentPlayer(playerTwo);
        building.accept(visitor);

        assertEquals(1, effect.calls);
        assertSame(playerTwo, effect.lastPlayer);
    }
}

