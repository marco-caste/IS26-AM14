package it.polimi.ingsw.am14.model.cards.buildings;

import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.model.cards.buildings.effects.EffectStrategy;
import it.polimi.ingsw.am14.model.cards.buildings.visitors.*;
import it.polimi.ingsw.am14.model.gameLogic.*;

public abstract class Building extends Card {
    private final int cost;
    private final int prestigePoints;
    private EffectStrategy effectStrategy;

    public Building(int cost, int prestigePoints, int age, int id, EffectStrategy effectStrategy) {
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

    @Override
    public void applyEffect(Player player, Game game) {
        this.effectStrategy.applyEffect(player, game);
    }

    @Override
    public boolean isPickable(Player player) {
        return player.getNumFood() + player.getTotalBuildingDiscount() >= cost;
    }

    public void buy(Player player) {
        int payment = -cost + player.getTotalBuildingDiscount();
        if (payment > 0)
            payment = 0;
        player.addToNumFood(payment);
        player.getTribe().addBuilding(this);
    }

    public abstract void accept(BuildingVisitor visitor);
}
