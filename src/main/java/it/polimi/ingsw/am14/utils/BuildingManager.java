package it.polimi.ingsw.am14.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.buildings.effects.EffectStrategy;
import it.polimi.ingsw.am14.model.cards.buildings.types.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class BuildingManager {

    public static Map<Integer, ArrayList<Building>> turnJsonIntoBuildingMap() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = new String(Files.readAllBytes(Paths.get("resources/Buildings.json")));

        Map<String, ArrayList<Map<String, Object>>> tempMap =
                objectMapper.readValue(json,
                        new TypeReference<Map<String, ArrayList<Map<String, Object>>>>() {});

        // final result: era -> list of Building
        Map<Integer, ArrayList<Building>> result = new HashMap<>();

        // iterate eras
        for (Map.Entry<String, ArrayList<Map<String, Object>>> eraEntry : tempMap.entrySet()) {
            int era = Integer.parseInt(eraEntry.getKey());

            if (!result.containsKey(era)) {
                result.put(era, new ArrayList<>());
            }

            ArrayList<Building> buildingList = result.get(era);

            // iterate raw buildings of that era
            for (Map<String, Object> rawBuilding : eraEntry.getValue()) {
                Building building = buildBuilding(rawBuilding);
                buildingList.add(building);
            }
        }

        return result;
    }

    private static Building buildBuilding(Map<String, Object> rawBuilding) {
        String type = rawBuilding.get("type").toString();
        int age = (int) rawBuilding.get("age");
        int id = (int) rawBuilding.get("id");
        int cost = (int) rawBuilding.get("cost");
        int prestigePoints = (int) rawBuilding.get("prestigePoints");
        String strategyKey = (String) rawBuilding.get("effectStrategy");
        EffectConstructor constructor = new EffectConstructor();
        EffectStrategy effect = constructor.create(strategyKey);

        switch (type) {
            case "EndGameBuilding":
                return new EndGameBuilding(cost, prestigePoints, age, id, effect);
            case "HuntEventBuilding":
                return new HuntEventBuilding(cost, prestigePoints, age, id, effect);
            case "ImmediateBuilding":
                return new ImmediateBuilding(cost, prestigePoints, age, id, effect);
            case "InventorsBuilding":
                return new InventorsBuilding(cost, prestigePoints, age, id, effect);
            case "PaintEventBuilding":
                return new PaintEventBuilding(cost, prestigePoints, age, id, effect);
            case "PassiveBuilding":
                return new PassiveBuilding(cost, prestigePoints, age, id, effect);
            case "SetBuilding":
                return new SetBuilding(cost, prestigePoints, age, id, effect);
            case "ShamanBonusEventBuilding":
                return new ShamanBonusEventBuilding(cost, prestigePoints, age, id, effect);
            case "ShamanMalusEventBuilding":
                return new ShamanMalusEventBuilding(cost, prestigePoints, age, id, effect);
            case "SustenanceEventBuilding":
                return new SustenanceEventBuilding(cost, prestigePoints, age, id, effect);
            default:
                throw new IllegalArgumentException("Tipo di edificio non riconosciuto: " + type);
        }
    }
}

