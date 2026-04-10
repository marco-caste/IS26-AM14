package it.polimi.ingsw.am14.model.utils;

import it.polimi.ingsw.am14.model.cards.buildings.effects.EffectStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EffectConstructorTest {

    @Test
    void create_validStrategy_returnsCorrectEffectInstance() {
        EffectConstructor constructor = new EffectConstructor();

        EffectStrategy effect = constructor.create("Flat25Prestige");

        assertNotNull(effect);
        assertEquals("Flat25Prestige", effect.getClass().getSimpleName());
    }

    @Test
    void create_invalidStrategy_throwsIllegalArgumentException() {
        EffectConstructor constructor = new EffectConstructor();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> constructor.create("NotExistingStrategy")
        );

        assertTrue(ex.getMessage().contains("Strategy non supportata"));
    }

    @Test
    void create_allRegisteredStrategies_returnNonNullInstances() {
        EffectConstructor constructor = new EffectConstructor();

        String[] keys = {
                "DoubleBuilderPrestige",
                "DoubleShamanicReward",
                "EndGameSetPrestige",
                "ExtraEndTurnFood",
                "ExtraShamanicStars",
                "Flat25Prestige",
                "HuntExtraFoodAndPrestige",
                "InventorPairFood",
                "PaintExtraFood",
                "PlayerExtraDraft",
                "ScoringArtists",
                "ScoringBuilders",
                "ScoringGatherers",
                "ScoringHunters",
                "ScoringInventors",
                "ScoringShamans",
                "SetCompletionFood",
                "ShamanicPenaltyShield",
                "SustenanceDiscountArtists",
                "SustenanceDiscountGatherers",
                "SustenanceDiscountInventors"
        };

        for (String key : keys) {
            EffectStrategy effect = constructor.create(key);
            assertNotNull(effect, "Effect should not be null for key: " + key);
        }
    }
}