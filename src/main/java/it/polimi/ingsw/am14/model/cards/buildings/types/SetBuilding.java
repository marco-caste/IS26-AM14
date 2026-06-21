package it.polimi.ingsw.am14.model.cards.buildings.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.buildings.effects.EffectStrategy;
import it.polimi.ingsw.am14.model.cards.buildings.visitors.BuildingVisitor;
/**
 * Represents a set building card in the game.
 * Extends the {@link Building} class and applies its specific effect
 * related to sets of cards or items. Allows operations through the visitor pattern.
 */
public class SetBuilding extends Building {
    /**
     * Constructs a new {@code SetBuilding} with the specified attributes.
     *
     * @param cost           the cost of the building
     * @param prestigePoints the base prestige points granted by the building
     * @param age            the age or era the building belongs to
     * @param id             the unique identifier of the building
     * @param effectStrategy the strategy defining the specific effect of this building
     */
    public SetBuilding(@JsonProperty("cost") int cost,
                       @JsonProperty("prestigePoints") int prestigePoints,
                       @JsonProperty("age") int age,
                       @JsonProperty("id") int id,
                       @JsonProperty("effectStrategy") EffectStrategy effectStrategy) {
        super(cost, prestigePoints, age, id, effectStrategy);
    }
    /**
     * Accepts a {@link BuildingVisitor} to perform operations specific to this building type.
     *
     * @param visitor the visitor operating on this building
     */
    @Override
    public void accept(BuildingVisitor visitor) {
        visitor.visit(this);
    }
}
