package it.polimi.ingsw.am14.model.cards.buildings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.model.cards.buildings.effects.EffectStrategy;
import it.polimi.ingsw.am14.model.cards.buildings.types.*;
import it.polimi.ingsw.am14.model.cards.buildings.visitors.*;
import it.polimi.ingsw.am14.model.gameLogic.*;
/**
 * Represents an abstract Building card within the game.
 * <p>
 * This class extends {@link Card} and defines the core attributes and behaviors of a building,
 * such as its cost, prestige points, and the strategy used to apply its specific effect.
 * It also supports the Visitor Pattern through the {@link #accept(BuildingVisitor)} method
 * to handle specialized building types dynamically.
 * </p>
 * <p>
 * Serialization and deserialization are handled polimorphically via Jackson annotations,
 * mapping subclasses based on the "type" property.
 * </p>
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EndGameBuilding.class, name = "EndGameBuilding"),
        @JsonSubTypes.Type(value = HuntEventBuilding.class, name = "HuntEventBuilding"),
        @JsonSubTypes.Type(value = ImmediateBuilding.class, name = "ImmediateBuilding"),
        @JsonSubTypes.Type(value = InventorsBuilding.class, name = "InventorsBuilding"),
        @JsonSubTypes.Type(value = PaintEventBuilding.class, name = "PaintEventBuilding"),
        @JsonSubTypes.Type(value = PassiveBuilding.class, name = "PassiveBuilding"),
        @JsonSubTypes.Type(value = SetBuilding.class, name = "SetBuilding"),
        @JsonSubTypes.Type(value = ShamanBonusEventBuilding.class, name = "ShamanBonusEventBuilding"),
        @JsonSubTypes.Type(value = ShamanMalusEventBuilding.class, name = "ShamanMalusEventBuilding"),
        @JsonSubTypes.Type(value = SustenanceEventBuilding.class, name = "SustenanceEventBuilding")
})

public abstract class Building extends Card {
    private final int cost;
    private final int prestigePoints;
    private final EffectStrategy effectStrategy;
    /**
     * Constructs a new {@code Building} card with the specified attributes.
     * This constructor is annotated for Jackson JSON deserialization.
     *
     * @param cost           the food cost required to purchase the building
     * @param prestigePoints the prestige points awarded by this building
     * @param age            the historical age/era the card belongs to (passed to super)
     * @param id             the unique identifier of the card (passed to super)
     * @param effectStrategy the strategy defining the effect triggered by this building
     */
    public Building(
            @JsonProperty("cost") int cost,
            @JsonProperty("prestigePoints") int prestigePoints,
            @JsonProperty("age") int age,
            @JsonProperty("id") int id,
            @JsonProperty("effectStrategy") EffectStrategy effectStrategy) {
        super(age, id);
        this.cost = cost;
        this.prestigePoints = prestigePoints;
        this.effectStrategy = effectStrategy;
    }

    public int getCost() {
        return cost;
    }
    public int getPrestigePoints() {
        return prestigePoints;
    }
    /**
     * Applies the building's specific effect to the player using the assigned strategy.
     *
     * @param player the {@link Player} receiving the effect
     * @param game   the current {@link Game} context
     */
    @Override
    public void applyEffect(Player player, Game game) {
        this.effectStrategy.applyEffect(player, game);
    }
    /**
     * Determines whether the specified player has enough resources to pick/buy this building,
     * taking into account the player's available food and active building discounts.
     *
     * @param player the {@link Player} checking the requirements
     * @return {@code true} if the building can be picked, {@code false} otherwise
     */
    @Override
    public boolean isPickable(Player player) {
        return player.getNumFood() + player.getTotalBuildingDiscount() >= cost;
    }
    /**
     * Executes the purchase action of the building for a player.
     * <p>
     * Deducts the appropriate amount of food from the player (clamping the maximum
     * discount so the player doesn't gain food from buying) and adds this building
     * to the player's tribe.
     * </p>
     *
     * @param player the {@link Player} purchasing the building
     */
    public void buy(Player player) {
        int payment = -cost + player.getTotalBuildingDiscount();
        if (payment > 0)
            payment = 0;
        player.addToNumFood(payment);
        player.getTribe().addBuilding(this);
    }
    /**
     * Accepts a visitor to perform specialized processing based on the specific
     * concrete type of the building. Part of the Visitor Pattern.
     *
     * @param visitor the {@link BuildingVisitor} targeting this building
     */
    public abstract void accept(BuildingVisitor visitor);
    /**
     * Returns a string representation of the building, detailing its properties
     * and specific subclass type name.
     *
     * @return a string summarizing the building's state
     */
    @Override
    public String toString() {
        return "Building " +
                "cost=" + cost +
                ", prestigePoints=" + prestigePoints +
                ", effectStrategy=" + effectStrategy +
                ", type = " + this.getClass().getSimpleName();
    }
}
