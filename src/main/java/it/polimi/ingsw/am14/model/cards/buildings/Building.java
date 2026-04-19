package it.polimi.ingsw.am14.model.cards.buildings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am14.model.cards.Card;
import it.polimi.ingsw.am14.model.cards.buildings.effects.EffectStrategy;
import it.polimi.ingsw.am14.model.cards.buildings.types.*;
import it.polimi.ingsw.am14.model.cards.buildings.visitors.*;
import it.polimi.ingsw.am14.model.gameLogic.*;

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
    private EffectStrategy effectStrategy;

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

    @Override
    public String toString() {
        return "Building " +
                "cost=" + cost +
                ", prestigePoints=" + prestigePoints +
                ", effectStrategy=" + effectStrategy +
                ", type = " + this.getClass().getSimpleName();
    }
}
