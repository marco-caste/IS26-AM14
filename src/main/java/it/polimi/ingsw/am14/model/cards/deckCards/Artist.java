package it.polimi.ingsw.am14.model.cards.deckCards;

import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;

public class Artist extends CharacterCard {

    public Artist(int age, int id) {
        super(age, id);
    }

    @Override
    public void applyEffect(Player player, Game context) {
        player.getTribe().addArtist(this);
    }
}
