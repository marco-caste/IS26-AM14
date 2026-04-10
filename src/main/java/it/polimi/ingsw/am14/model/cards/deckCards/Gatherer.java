package it.polimi.ingsw.am14.model.cards.deckCards;

import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;

public class Gatherer extends CharacterCard{

    private final int  discount;
    // it's a positive number that represents
    // the discount on the gathering event

    public Gatherer ( int age, int id, int discount) {
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
