package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.deckCards.HuntEvent;
import it.polimi.ingsw.am14.model.cards.deckCards.ShamanEvent;
import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShamanicPenaltyShieldTest {

    private Player player;
    private Game game;
    private ShamanicPenaltyShield effect;

    @BeforeEach
    void setUp() {
        player = new Player("P1", TotemColor.WHITE);
        game = new Game(null, new ArrayList<>(), new Board(2), 0, 2, 1, 1,1);
        effect = new ShamanicPenaltyShield();
    }

    @Test
    void applyEffectAddsOnePlusTwiceAgeOfLastEvent() {
        game.getBoard().addToResolvingOtherEventsRow(new ShamanEvent(3, 30));

        effect.applyEffect(player, game);

        assertEquals(7, player.getNumPrestigePoints());
    }

    @Test
    void applyEffectUsesLastEventWhenMultipleArePresent() {
        game.getBoard().addToResolvingOtherEventsRow(new HuntEvent(1, 20));
        game.getBoard().addToResolvingOtherEventsRow(new ShamanEvent(2, 21));

        effect.applyEffect(player, game);

        assertEquals(5, player.getNumPrestigePoints());
    }

    @Test
    void applyEffectWithNoOtherEventThrowsNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> effect.applyEffect(player, game));
    }
}

