package it.polimi.ingsw.am14.model.cards.deckCards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;

public class Shaman extends CharacterCard {

    private final int numStars;

    @JsonCreator
    public Shaman(
            @JsonProperty("age") int age,
            @JsonProperty("id") int id,
            @JsonProperty("numStars") int numStars
    ) {
        super(age, id);
        this.numStars = numStars;
    }

    @Override
    public void applyEffect(Player player, Game context) { //
        player.getTribe().addShaman(this);
        player.addToStars(this.numStars);
        //it gives the player a number of stars that will be useful for the shaman events.
    }


}



