package it.polimi.ingsw.am14.model.cards.buildings.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.buildings.effects.EffectStrategy;
import it.polimi.ingsw.am14.model.cards.buildings.visitors.BuildingVisitor;

public class PaintEventBuilding extends Building {
    public PaintEventBuilding(@JsonProperty("cost") int cost,
                              @JsonProperty("prestigePoints") int prestigePoints,
                              @JsonProperty("age") int age,
                              @JsonProperty("id") int id,
                              @JsonProperty("effectStrategy") EffectStrategy effectStrategy) {
        super(cost, prestigePoints, age, id, effectStrategy);
    }

    @Override
    public void accept(BuildingVisitor visitor) {
        visitor.visit(this);
    }
}
