package it.polimi.ingsw.am14.model.cards.buildings.types;

import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.buildings.effects.EffectStrategy;
import it.polimi.ingsw.am14.model.cards.buildings.visitors.BuildingVisitor;

public class EndGameBuilding extends Building {
    public EndGameBuilding(int cost, int prestigePoints, int age, int id, EffectStrategy effectStrategy) {
        super(cost, prestigePoints, age, id, effectStrategy);
    }

    @Override
    public void accept(BuildingVisitor visitor) {
        visitor.visit(this);
    }
}
