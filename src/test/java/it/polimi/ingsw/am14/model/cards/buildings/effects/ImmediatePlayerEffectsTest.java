package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImmediatePlayerEffectsTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("P1", TotemColor.WHITE);
    }

    @Test
    void extraEndTurnFoodSetsFlagToOne() {
        ExtraEndTurnFood effect = new ExtraEndTurnFood();

        effect.applyEffect(player, null);

        assertEquals(1, player.getAddFoodStartOfRound());
    }

    @Test
    void extraShamanicStarsAddsThreeStars() {
        ExtraShamanicStars effect = new ExtraShamanicStars();

        effect.applyEffect(player, null);

        assertEquals(3, player.getNumStars());
    }

    @Test
    void inventorPairFoodAddsThreeFood() {
        InventorPairFood effect = new InventorPairFood();

        effect.applyEffect(player, null);

        assertEquals(3, player.getNumFood());
    }

    @Test
    void playerExtraDraftSetsAddTopPickToOne() {
        PlayerExtraDraft effect = new PlayerExtraDraft();

        effect.applyEffect(player, null);

        assertEquals(1, player.getAddTopPick());
    }

    @Test
    void setCompletionFoodAddsFiveFood() {
        SetCompletionFood effect = new SetCompletionFood();

        effect.applyEffect(player, null);

        assertEquals(5, player.getNumFood());
    }

    @Test
    void endGameSetPrestigeAddsSixPerCompletedSet() {
        EndGameSetPrestige effect = new EndGameSetPrestige();
        player.addToSetCompleted(3);

        effect.applyEffect(player, null);

        assertEquals(18, player.getNumPrestigePoints());
    }

    @Test
    void flat25PrestigeCurrentBehaviorDoesNothing() {
        Flat25Prestige effect = new Flat25Prestige();
        player.addToPrestigePoints(9);

        effect.applyEffect(player, null);

        assertEquals(9, player.getNumPrestigePoints());
    }
}

