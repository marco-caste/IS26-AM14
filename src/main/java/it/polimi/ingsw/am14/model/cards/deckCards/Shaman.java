package it.polimi.ingsw.am14.model.cards.deckCards;

import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;

public class Shaman extends CharacterCard {

    private final int numStars;

    public Shaman(int age, int id, int numStars) {
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



