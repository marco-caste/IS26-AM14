package it.polimi.ingsw.am14.model.cards.deckCards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.cards.buildings.*;
import it.polimi.ingsw.am14.model.cards.buildings.visitors.InventorsVisitor;
import it.polimi.ingsw.am14.model.gameLogic.*;

import java.util.ArrayList;

public class Inventor extends CharacterCard{

    private final InventionType invention;

    @JsonCreator
    public Inventor(
            @JsonProperty("age") int age,
            @JsonProperty("id") int id,
            @JsonProperty("inventionType") String invention
    ) {
        super(age, id);
        this.invention = InventionType.valueOf(invention.toUpperCase());
    }

    public InventionType getInvention(){
        return invention;
    }


    // called on pick

    /**
     * <p>
     *     checks if player p has the inventor pair building
     * </p>
     * @param player not null
     * @param context not null
     */
    private void checkInventorPairBuilding(Player player, Game context){

        if(player.getTribe().getInventors().stream()
                .noneMatch(x-> x.getInvention().equals(this.invention))){
            return;
        }
        // if we don't have the inventor match we don't need to check if we have the building match, so we return immediately

        InventorsVisitor visitor = new InventorsVisitor(context, player);
        ArrayList<Building> buildings = player.getTribe().getBuildings();
        for (Building building : buildings) {
            building.accept(visitor);
        }
    }


    @Override
    public void applyEffect(Player player, Game context) {
        checkInventorPairBuilding(player, context);
        player.getTribe().addInventor(this);

    }



}