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

class DoubleShamanicRewardTest {

    private Player player;
    private Game game;
    private DoubleShamanicReward effect;

    @BeforeEach
    void setUp() {
        player = new Player("P1", TotemColor.WHITE);
        // Minimal game snapshot: for this effect we only need game.getBoard().getLastOtherEvent().
        game = new Game(null, new ArrayList<>(), new Board(2), 0, 2, 1, 1,1);
        effect = new DoubleShamanicReward();
    }

    @Test
    void applyEffectAddsFiveTimesAgeOfLastOtherEvent() {
        game.getBoard().addToResolvingOtherEventsRow(new ShamanEvent(3, 10));

        effect.applyEffect(player, game);

        assertEquals(15, player.getNumPrestigePoints());
    }

    @Test
    void applyEffectUsesLastInsertedOtherEventWhenMultipleEventsExist() {
        game.getBoard().addToResolvingOtherEventsRow(new HuntEvent(1, 11));
        game.getBoard().addToResolvingOtherEventsRow(new ShamanEvent(2, 12));

        effect.applyEffect(player, game);

        assertEquals(10, player.getNumPrestigePoints());
    }

    @Test
    void applyEffectAccumulatesOnExistingPrestigePoints() {
        player.addToPrestigePoints(7);

        game.getBoard().addToResolvingOtherEventsRow(new ShamanEvent(2, 13));

        effect.applyEffect(player, game);

        assertEquals(17, player.getNumPrestigePoints());
    }

    @Test
    void applyEffectWithNoOtherEventThrowsNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> effect.applyEffect(player, game));
    }

    @Test
    void applyEffectWithNullPlayerThrowsNullPointerException() {
        game.getBoard().addToResolvingOtherEventsRow(new ShamanEvent(1, 14));

        assertThrows(NullPointerException.class, () -> effect.applyEffect(null, game));
    }

    @Test
    void applyEffectWithNullGameThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> effect.applyEffect(player, null));
    }
}
