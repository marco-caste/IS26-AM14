package it.polimi.ingsw.am14.model.utils;

import it.polimi.ingsw.am14.model.cards.buildings.effects.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class EffectConstructor {
    private final Map<String, Supplier<EffectStrategy>> registry = new HashMap<>();

    public EffectConstructor() {
        registry.put("DoubleBuilderPrestige" , DoubleBuilderPrestige::new);
        registry.put("DoubleShamanicReward" , DoubleShamanicReward::new);
        registry.put("EndGameSetPrestige" , EndGameSetPrestige::new);
        registry.put("ExtraEndTurnFood" , ExtraEndTurnFood::new);
        registry.put("ExtraShamanicStars" , ExtraShamanicStars::new);
        registry.put("Flat25Prestige" , Flat25Prestige::new);
        registry.put("HuntExtraFoodAndPrestige" , HuntExtraFoodAndPrestige::new);
        registry.put("InventorPairFood" , InventorPairFood::new);
        registry.put("PaintExtraFood" , PaintExtraFood::new);
        registry.put("PlayerExtraDraft" , PlayerExtraDraft::new);
        registry.put("ScoringArtists" , ScoringArtists::new);
        registry.put("ScoringBuilders" , ScoringBuilders::new);
        registry.put("ScoringGatherers" , ScoringGatherers::new);
        registry.put("ScoringHunters" , ScoringHunters::new);
        registry.put("ScoringInventors" , ScoringInventors::new);
        registry.put("ScoringShamans" , ScoringShamans::new);
        registry.put("SetCompletionFood" , SetCompletionFood::new);
        registry.put("ShamanicPenaltyShield" , ShamanicPenaltyShield::new);
        registry.put("SustenanceDiscountArtists" , SustenanceDiscountArtists::new);
        registry.put("SustenanceDiscountGatherers" , SustenanceDiscountGatherers::new);
        registry.put("SustenanceDiscountInventors" , SustenanceDiscountInventors::new);
    }

    public EffectStrategy create(String strategyKey) {
        Supplier<EffectStrategy> supplier = registry.get(strategyKey);
        if (supplier == null) {
            throw new IllegalArgumentException("Strategy non supportata: " + strategyKey);
        }
        return supplier.get();
    }
}
