package it.polimi.ingsw.am14.utils;

import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.buildings.types.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BuildingManagerTest {

    @Test
    void turnJsonIntoBuildingMap_loadsAllErasWithExpectedSizes() throws Exception {
        Map<Integer, ArrayList<Building>> map = BuildingManager.turnJsonIntoBuildingMap();

        assertNotNull(map);
        assertTrue(map.containsKey(1));
        assertTrue(map.containsKey(2));
        assertTrue(map.containsKey(3));

        // Dal JSON:
        // era 1 -> 6 building
        // era 2 -> 7 building
        // era 3 -> 8 building
        assertEquals(6, map.get(1).size());
        assertEquals(7, map.get(2).size());
        assertEquals(8, map.get(3).size());
    }

    @Test
    void turnJsonIntoBuildingMap_createsExpectedConcreteTypes() throws Exception {
        Map<Integer, ArrayList<Building>> map = BuildingManager.turnJsonIntoBuildingMap();

        // Era 1: primo è SetBuilding (id 96)
        Building b1 = map.get(1).get(0);
        assertInstanceOf(SetBuilding.class, b1);

        // Era 2: uno dei building è HuntEventBuilding (id 105, in posizione 3 nel tuo json era2)
        Building b2 = map.get(2).get(3);
        assertInstanceOf(HuntEventBuilding.class, b2);

        // Era 3: secondo è PassiveBuilding (id 110)
        Building b3 = map.get(3).get(1);
        assertInstanceOf(PassiveBuilding.class, b3);
    }

    @Test
    void buildBuilding_invalidType_throwsIllegalArgumentException() throws Exception {
        Method m = BuildingManager.class.getDeclaredMethod("buildBuilding", Map.class);
        m.setAccessible(true);

        Map<String, Object> raw = Map.of(
                "type", "UnknownBuilding",
                "age", 1,
                "id", 999,
                "cost", 1,
                "prestigePoints", 1,
                "effectStrategy", "Flat25Prestige"
        );

        InvocationTargetException ex = assertThrows(
                InvocationTargetException.class,
                () -> m.invoke(null, raw)
        );

        assertInstanceOf(IllegalArgumentException.class, ex.getCause());
        assertTrue(ex.getCause().getMessage().contains("Tipo di edificio non riconosciuto"));
    }

    @Test
    void buildBuilding_invalidEffectStrategy_throwsIllegalArgumentException() throws Exception {
        Method m = BuildingManager.class.getDeclaredMethod("buildBuilding", Map.class);
        m.setAccessible(true);

        Map<String, Object> raw = Map.of(
                "type", "ImmediateBuilding",
                "age", 1,
                "id", 1000,
                "cost", 2,
                "prestigePoints", 3,
                "effectStrategy", "NotExistingStrategy"
        );

        InvocationTargetException ex = assertThrows(
                InvocationTargetException.class,
                () -> m.invoke(null, raw)
        );

        assertInstanceOf(IllegalArgumentException.class, ex.getCause());
        assertTrue(ex.getCause().getMessage().contains("Strategy non supportata"));
    }
}