package it.polimi.ingsw.am14.model.cards.buildings.effects;

import it.polimi.ingsw.am14.model.cards.deckCards.Artist;
import it.polimi.ingsw.am14.model.cards.deckCards.Builder;
import it.polimi.ingsw.am14.model.cards.deckCards.Gatherer;
import it.polimi.ingsw.am14.model.cards.deckCards.Hunter;
import it.polimi.ingsw.am14.model.cards.deckCards.InventionType;
import it.polimi.ingsw.am14.model.cards.deckCards.Inventor;
import it.polimi.ingsw.am14.model.cards.deckCards.Shaman;
import it.polimi.ingsw.am14.model.gameLogic.Player;
import it.polimi.ingsw.am14.model.gameLogic.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TribeCountEffectsTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("P1", TotemColor.WHITE);

        player.getTribe().addHunter(new Hunter(1, 1, false));
        player.getTribe().addHunter(new Hunter(1, 2, false));

        player.getTribe().addGatherer(new Gatherer(1, 3, 1));
        player.getTribe().addGatherer(new Gatherer(1, 4, 1));
        player.getTribe().addGatherer(new Gatherer(1, 5, 1));

        player.getTribe().addArtist(new Artist(1, 6));
        player.getTribe().addArtist(new Artist(1, 7));

        player.getTribe().addBuilder(new Builder(1, 8, 1, 2));

        player.getTribe().addInventor(new Inventor(1, 9, InventionType.ARROW));
        player.getTribe().addInventor(new Inventor(1, 10, InventionType.BOAT));

        player.getTribe().addShaman(new Shaman(1, 11, 1));
        player.getTribe().addShaman(new Shaman(1, 12, 1));
    }

    @Test
    void huntExtraFoodAndPrestigeUsesHuntersSize() {
        HuntExtraFoodAndPrestige effect = new HuntExtraFoodAndPrestige();

        effect.applyEffect(player, null);

        assertEquals(2, player.getNumFood());
        assertEquals(2, player.getNumPrestigePoints());
    }

    @Test
    void paintExtraFoodUsesArtistsSize() {
        PaintExtraFood effect = new PaintExtraFood();

        effect.applyEffect(player, null);

        assertEquals(2, player.getNumFood());
    }

    @Test
    void scoringArtistsAddsFourPerArtist() {
        ScoringArtists effect = new ScoringArtists();

        effect.applyEffect(player, null);

        assertEquals(8, player.getNumPrestigePoints());
    }

    @Test
    void scoringBuildersAddsFourPerBuilder() {
        ScoringBuilders effect = new ScoringBuilders();

        effect.applyEffect(player, null);

        assertEquals(4, player.getNumPrestigePoints());
    }

    @Test
    void scoringGatherersAddsFourPerGatherer() {
        ScoringGatherers effect = new ScoringGatherers();

        effect.applyEffect(player, null);

        assertEquals(12, player.getNumPrestigePoints());
    }

    @Test
    void scoringHuntersAddsThreePerHunter() {
        ScoringHunters effect = new ScoringHunters();

        effect.applyEffect(player, null);

        assertEquals(6, player.getNumPrestigePoints());
    }

    @Test
    void scoringInventorsAddsTwoPerInventor() {
        ScoringInventors effect = new ScoringInventors();

        effect.applyEffect(player, null);

        assertEquals(4, player.getNumPrestigePoints());
    }

    @Test
    void scoringShamansAddsFourPerShaman() {
        ScoringShamans effect = new ScoringShamans();

        effect.applyEffect(player, null);

        assertEquals(8, player.getNumPrestigePoints());
    }

    @Test
    void sustenanceDiscountArtistsCurrentBehaviorAddsFoodEqualToArtists() {
        SustenanceDiscountArtists effect = new SustenanceDiscountArtists();

        effect.applyEffect(player, null);

        assertEquals(2, player.getNumFood());
    }

    @Test
    void sustenanceDiscountGatherersCurrentBehaviorAddsFoodEqualToGatherers() {
        SustenanceDiscountGatherers effect = new SustenanceDiscountGatherers();

        effect.applyEffect(player, null);

        assertEquals(3, player.getNumFood());
    }

    @Test
    void sustenanceDiscountInventorsCurrentBehaviorAddsFoodEqualToInventors() {
        SustenanceDiscountInventors effect = new SustenanceDiscountInventors();

        effect.applyEffect(player, null);

        assertEquals(2, player.getNumFood());
    }
}

