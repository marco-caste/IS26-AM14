package it.polimi.ingsw.am14.model.cards.deckCards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;

public class Gatherer extends CharacterCard{

    private final int  discount;
    // it's a positive number that represents
    // the discount on the gathering event

    @JsonCreator
    public Gatherer (
            @JsonProperty("age") int age,
            @JsonProperty("id") int id,
            @JsonProperty("discount") int discount
    ) {
        super(age, id);
        this.discount = discount;
    }

    @Override
    public void applyEffect(Player player, Game context) {
        player.getTribe().addGatherer(this);
        player.addToGatheringDiscount(this.discount);
        // updates the GatheringDiscount of tha player
    }

}
