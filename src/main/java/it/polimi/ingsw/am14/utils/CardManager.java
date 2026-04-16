package it.polimi.ingsw.am14.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am14.model.cards.deckCards.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// this class reads the JSON file which is organized like this:
// minPlayers -> era -> cardType (Characters/Events) -> cardName (Artist, HuntEvent, ...) -> list of cards with that name

public abstract class CardManager {

    // class declaration red in testing: no calls to class constructor in project code

    public static Map<Integer, Map<String, Map<String, ArrayList<Map<String, Object>>>>> turnJsonIntoRawMap (int numPlayers) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        // Read the JSON file and turns it into a String
        String json = new String(Files.readAllBytes(Paths.get("resources/DeckCards.json")));

        // tempMap is the map directly read from the JSON file, with String keys; it will be processed to create the final result map
        Map<String, Map<String, Map<String, Map<String, ArrayList<Map<String, Object>>>>>> tempMap =
                objectMapper.readValue(json,
                        new TypeReference<Map<String, Map<String, Map<String, Map<String, ArrayList<Map<String, Object>>>>>>>(){});

        // result is the final map, with Integer keys for era and string keys for card types
        // the structure is: era -> card type -> card name -> list of cards
        Map<Integer, Map<String, Map<String, ArrayList<Map<String, Object>>>>> result = new HashMap<>();


        // cycle on all the entryset of tempMap to get everything
        for (Map.Entry<String, Map<String, Map<String, Map<String, ArrayList<Map<String, Object>>>>>> playerEntry : tempMap.entrySet()) {
            int minPlayers = Integer.parseInt(playerEntry.getKey());

            // it discards all the cards that require more players than the current players playing the game
            if (minPlayers > numPlayers) {
                continue;
            }

            // cycle on all the entryset of the map of eras for a specific minPlayers value
            for (Map.Entry<String, Map<String, Map<String, ArrayList<Map<String, Object>>>>> eraEntry : playerEntry.getValue().entrySet()) {
                int era = Integer.parseInt(eraEntry.getKey());

                // if the era is not already in the result map, add it with an empty map as value
                if (!result.containsKey(era)) {
                    result.put(era, new HashMap<>());
                }

                // if the era is already in the result map, we need to add the cards of that era
                Map<String, Map<String, ArrayList<Map<String, Object>>>> existingTypeMap = result.get(era);

                // cycle on all the entryset of the map of card types(event or character) for a specific era
                for (Map.Entry<String, Map<String, ArrayList<Map<String, Object>>>> typeEntry : eraEntry.getValue().entrySet()) {

                    // if the card type is not already in the map of that era, add it with an empty map as value
                    if (!existingTypeMap.containsKey(typeEntry.getKey())) {
                        existingTypeMap.put(typeEntry.getKey(), new HashMap<>());
                    }

                    Map<String, ArrayList<Map<String, Object>>> cardTypeMap = existingTypeMap.get(typeEntry.getKey());

                    // cycle on all the card names for a specific card type
                    for (Map.Entry<String, ArrayList<Map<String, Object>>> cardNameEntry : typeEntry.getValue().entrySet()) {
                        String cardType = cardNameEntry.getKey();

                        // if the card name is not already in the map, add it with an empty list as value
                        if (!cardTypeMap.containsKey(cardType)) {
                            cardTypeMap.put(cardType, new ArrayList<>());
                        }
                        // add all the cards with this name to the list
                        cardTypeMap.get(cardType).addAll(cardNameEntry.getValue());
                    }
                }
            }
        }

        return result;
    }

    public static Map<Integer, Map<String, Map<String, ArrayList<DeckCard>>>> instantiateCardMap(Map<Integer, Map<String, Map<String, ArrayList<Map<String, Object>>>>> rawMap) {
        Map<Integer, Map<String, Map<String, ArrayList<DeckCard>>>> result = new HashMap<>();

        // era
        for (Map.Entry<Integer, Map<String, Map<String, ArrayList<Map<String, Object>>>>> eraEntry : rawMap.entrySet()) {
            Integer era = eraEntry.getKey();

            if (!result.containsKey(era)) {
                result.put(era, new HashMap<>());
            }

            Map<String, Map<String, ArrayList<DeckCard>>> existingTypeMap = result.get(era);

            // tipologia (characters/events)
            for (Map.Entry<String, Map<String, ArrayList<Map<String, Object>>>> typeEntry : eraEntry.getValue().entrySet()) {
                String type = typeEntry.getKey();

                if (!existingTypeMap.containsKey(type)) {
                    existingTypeMap.put(type, new HashMap<>());
                }

                Map<String, ArrayList<DeckCard>> existingNameMap = existingTypeMap.get(type);

                // nome (Artist, Builder, HuntEvent, ...)
                for (Map.Entry<String, ArrayList<Map<String, Object>>> nameEntry : typeEntry.getValue().entrySet()) {
                    String nome = nameEntry.getKey();

                    if (!existingNameMap.containsKey(nome)) {
                        existingNameMap.put(nome, new ArrayList<>());
                    }

                    ArrayList<DeckCard> concreteCards = existingNameMap.get(nome);

                    // rawData di ogni carta
                    for (Map<String, Object> rawData : nameEntry.getValue()) {
                        DeckCard card = buildCard(type, nome, rawData);
                        concreteCards.add(card);
                    }
                }
            }
        }

        return result;
    }

    private static DeckCard buildCard(String type, String nome, Map<String, Object> rawData) {
        switch (type) {
            case "Characters":
                switch (nome) {
                    case "Artist":
                        return new Artist((int) rawData.get("age"), (int) rawData.get("id"));
                    case "Builder":
                        return new Builder((int) rawData.get("age"), (int) rawData.get("id"), (int) rawData.get("buildDiscount"), (int) rawData.get("prestigePoints"));
                    case "Shaman":
                        return new Shaman((int) rawData.get("age"), (int) rawData.get("id"), (int) rawData.get("numStars"));
                    case "Gatherer":
                        return new Gatherer((int) rawData.get("age"), (int) rawData.get("id"), (int) rawData.get("discount"));
                    case "Inventor":
                        return new Inventor((int) rawData.get("age"), (int) rawData.get("id"), InventionType.valueOf(rawData.get("invention").toString()));
                    case "Hunter":
                        return new Hunter((int) rawData.get("age"), (int) rawData.get("id"), (boolean) rawData.get("food"));
                    default:
                        throw new IllegalArgumentException("Invalid card type: " + type);

                }

            case "Events":
                switch (nome) {
                    case "HuntEvent":
                        return new HuntEvent((int) rawData.get("age"), (int) rawData.get("id"));
                    case "PaintEvent":
                        return new PaintEvent((int) rawData.get("age"), (int) rawData.get("id"));
                    case "ShamanEvent":
                        return new ShamanEvent((int) rawData.get("age"), (int) rawData.get("id"));
                    case "SustenanceEvent":
                        return new SustenanceEvent((int) rawData.get("age"), (int) rawData.get("id"));
                    default:
                        throw new IllegalArgumentException("Invalid card type: " + type);
                }
            default:
            throw new IllegalArgumentException("Tipologia non gestita: " + type);
        }
    }

    public static Map<Integer, ArrayList<DeckCard>> buildDecksFromCardMap(Map<Integer, Map<String, Map<String, ArrayList<DeckCard>>>> cardMap) {
        Map<Integer, ArrayList<DeckCard>> decks = new HashMap<>();

        for (Map.Entry<Integer, Map<String, Map<String, ArrayList<DeckCard>>>> eraEntry : cardMap.entrySet()) {
            Integer era = eraEntry.getKey();

            if (!decks.containsKey(era)) {
                decks.put(era, new ArrayList<>());
            }

            ArrayList<DeckCard> eraDeck = decks.get(era);

            // tipologia: characters/events
            for (Map.Entry<String, Map<String, ArrayList<DeckCard>>> typeEntry : eraEntry.getValue().entrySet()) {

                // nome: Artist, HuntEvent, ...
                for (Map.Entry<String, ArrayList<DeckCard>> nameEntry : typeEntry.getValue().entrySet()) {
                    eraDeck.addAll(nameEntry.getValue());
                }
            }
        }

        return decks;
    }
}