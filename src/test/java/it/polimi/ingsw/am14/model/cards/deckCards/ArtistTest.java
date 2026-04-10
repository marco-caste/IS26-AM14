package it.polimi.ingsw.am14.model.cards.deckCards;

import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArtistTest {
    private Player player;
    private Artist artist;

    @BeforeEach
    void setUp() {
        player = new Player("player", TotemColor.WHITE);
        artist = new Artist(3, 27);
    }

    @Test
    void isPickable() {
        assertTrue(artist.isPickable(player));
    }

    @Test
    void applyEffect() {
        artist.applyEffect(player, null);

        assertEquals(1, player.getTribe().getArtistsSize());
    }
}