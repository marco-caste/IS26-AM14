package it.polimi.ingsw.am14.model.cards.deckCards;

import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;

public class Builder extends CharacterCard{

    private final int buildDiscount ; // positive number
    private final int prestigePoints; // prestige that the player will get at the end of the game from this builder

    public Builder(int age, int id, int buildDiscount, int prestigePoints) {
        super(age, id);
        this.buildDiscount = buildDiscount;
        this.prestigePoints = prestigePoints;
    }

    public int getPrestigePoints(){
        return this.prestigePoints;
    }

    @Override
    public void applyEffect(Player player, Game context) {
        player.getTribe().addBuilder(this);
        player.addToBuildingDiscount(this.buildDiscount);
    }




}