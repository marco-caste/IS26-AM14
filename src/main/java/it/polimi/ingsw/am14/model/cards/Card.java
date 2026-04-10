package it.polimi.ingsw.am14.model.cards;

import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;

public abstract class Card {
    private final int age;
    private final int id;

    public Card(int age, int id) {
        this.age = age;
        this.id = id;
    }

    public int getAge(){
        return age;
    }

    public int getId(){
        return id;
    }

    public abstract void applyEffect(Player player, Game context) ;
    // this method will be implemented in the classes that extend Card,
    // and will apply the effect of the card to the player passed as a parameter
    // we will call this method when the player picks the card

    public abstract boolean isPickable(Player player);
    // if the card is:
    // Character : returns true
    // Event : returns false
    // Building : returns true if the player has enough food to buy it, false otherwise
}
