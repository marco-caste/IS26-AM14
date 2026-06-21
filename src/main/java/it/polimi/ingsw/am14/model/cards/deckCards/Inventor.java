package it.polimi.ingsw.am14.model.cards.deckCards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.cards.buildings.*;
import it.polimi.ingsw.am14.model.cards.buildings.visitors.InventorsVisitor;
import it.polimi.ingsw.am14.model.gameLogic.*;

import java.util.ArrayList;
/**
 * Represents an Inventor character card within the game.
 * <p>
 * This class extends {@link CharacterCard} and introduces a specialized mechanic tied to an {@link InventionType}.
 * When an inventor is picked, it evaluates potential synergistic combos with existing inventors and
 * buildings in the player's tribe before officially joining the roster.
 * </p>
 */
public class Inventor extends CharacterCard{

    private final InventionType inventionType;
    /**
     * Constructs a new {@code Inventor} card with the specified age, identifier, and invention type.
     * This constructor parses the incoming string to match the corresponding {@link InventionType} enum value.
     *
     * @param age           the historical age/era this card belongs to (passed to super)
     * @param id            the unique identifier of the card (passed to super)
     * @param inventionType the string representation of the invention type (case-insensitive)
     */
    @JsonCreator
    public Inventor(
            @JsonProperty("age") int age,
            @JsonProperty("id") int id,
            @JsonProperty("inventionType") String inventionType
    ) {
        super(age, id);
        this.inventionType = InventionType.valueOf(inventionType.toUpperCase());
    }

    public InventionType getInvention(){
        return inventionType;
    }


    // called on pick

    private void checkInventorPairBuilding(Player player, Game context){

        if(player.getTribe().getInventors().stream()
                .noneMatch(x-> x.getInvention().equals(this.inventionType))){
            return;
        }
        // if we don't have the inventor match we don't need to check if we have the building match, so we return immediately

        InventorsVisitor visitor = new InventorsVisitor(context, player);
        ArrayList<Building> buildings = player.getTribe().getBuildings();
        for (Building building : buildings) {
            building.accept(visitor);
        }
    }

    /**
     * Applies the inventor's specific effect upon being picked.
     * <p>
     * First, it evaluates the player's tribe assets for inventor-building combinations
     * using {@link #checkInventorPairBuilding(Player, Game)}. Afterward, it registers
     * this inventor instance into the player's tribe.
     * </p>
     *
     * @param player  the {@link Player} who is playing or activating the card
     * @param context the current {@link Game} context
     */
    @Override
    public void applyEffect(Player player, Game context) {
        checkInventorPairBuilding(player, context);
        player.getTribe().addInventor(this);

    }



}