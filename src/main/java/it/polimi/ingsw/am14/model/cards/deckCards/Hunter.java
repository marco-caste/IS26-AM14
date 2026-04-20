package it.polimi.ingsw.am14.model.cards.deckCards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;

public class Hunter extends CharacterCard{

    private final boolean hasHam;

    @JsonCreator
    public Hunter(
            @JsonProperty("age") int age,
            @JsonProperty("id") int id,
            @JsonProperty("hasHam") boolean food
    ) {
        super(age, id);
        this.hasHam = food;
    }

    private void gainFood(Player player){
        if(hasHam){
            int numHunters = player.getTribe().getHuntersSize();
            player.addToNumFood(numHunters);
        }
    }// if the player has ham, he will gain 1 food for each hunter in his tribe (including this one)

    @Override
    public void applyEffect(Player player, Game context) {
        player.getTribe().addHunter(this);
        gainFood(player);
    }  // we add the player to the tribe and then we call the gainFood method to give him food if the hunter picked
}
