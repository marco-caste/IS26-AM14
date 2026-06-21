package it.polimi.ingsw.am14.model.cards.buildings.effects;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am14.model.gameLogic.*;

/**
 * Represents a functional interface utilizing the Strategy pattern to define various
 * effects that can be applied by building cards.
 * <p>
 * This interface uses Jackson annotations to support polymorphic serialization and
 * deserialization, allowing the correct implementing class to be instantiated based
 * on the type provided in the JSON property.
 * </p>
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DoubleBuilderPrestige.class, name = "DoubleBuilderPrestige"),
        @JsonSubTypes.Type(value = DoubleShamanicReward.class, name = "DoubleShamanicReward"),
        @JsonSubTypes.Type(value = EndGameSetPrestige.class, name = "EndGameSetPrestige"),
        @JsonSubTypes.Type(value = ExtraEndTurnFood.class, name = "ExtraEndTurnFood"),
        @JsonSubTypes.Type(value = ExtraShamanicStars.class, name = "ExtraShamanicStars"),
        @JsonSubTypes.Type(value = Flat25Prestige.class, name = "Flat25Prestige"),
        @JsonSubTypes.Type(value = HuntExtraFoodAndPrestige.class, name = "HuntExtraFoodAndPrestige"),
        @JsonSubTypes.Type(value = InventorPairFood.class, name = "InventorPairFood"),
        @JsonSubTypes.Type(value = PaintExtraFood.class, name = "PaintExtraFood"),
        @JsonSubTypes.Type(value = PlayerExtraDraft.class, name = "PlayerExtraDraft"),
        @JsonSubTypes.Type(value = ScoringArtists.class, name = "ScoringArtists"),
        @JsonSubTypes.Type(value = ScoringBuilders.class, name = "ScoringBuilders"),
        @JsonSubTypes.Type(value = ScoringGatherers.class, name = "ScoringGatherers"),
        @JsonSubTypes.Type(value = ScoringHunters.class, name = "ScoringHunters"),
        @JsonSubTypes.Type(value = ScoringInventors.class, name = "ScoringInventors"),
        @JsonSubTypes.Type(value = ScoringShamans.class, name = "ScoringShamans"),
        @JsonSubTypes.Type(value = SetCompletionFood.class, name = "SetCompletionFood"),
        @JsonSubTypes.Type(value = ShamanicPenaltyShield.class, name = "ShamanicPenaltyShield"),
        @JsonSubTypes.Type(value = SustenanceDiscountArtists.class, name = "SustenanceDiscountArtists"),
        @JsonSubTypes.Type(value = SustenanceDiscountGatherers.class, name = "SustenanceDiscountGatherers"),
        @JsonSubTypes.Type(value = SustenanceDiscountInventors.class, name = "SustenanceDiscountInventors")
})

@FunctionalInterface //In order to have only 1 method
public interface EffectStrategy {
    /**
     * Applies the specific building effect to the given player based on the current game state.
     *
     * @param player the player who is receiving the benefits or penalties of the effect
     * @param game   the current game instance to retrieve any necessary global state variables
     */
    void applyEffect(Player player, Game game);
}



