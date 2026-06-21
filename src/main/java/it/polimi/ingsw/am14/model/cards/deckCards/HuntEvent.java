package it.polimi.ingsw.am14.model.cards.deckCards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.buildings.visitors.HuntEventVisitor;
import it.polimi.ingsw.am14.model.gameLogic.*;

import java.util.ArrayList;
/**
 * Represents a Hunt Event card within the game.
 * <p>
 * This class extends {@link EventCard}. When triggered, the hunt event globally affects
 * all players in the game, rewarding them with food and prestige points proportional
 * to their active hunting party (Hunters) and the current historical age.
 * It also triggers potential sub-effects on players' buildings via the Visitor Pattern.
 * </p>
 */
public class HuntEvent extends EventCard {
    /**
     * Constructs a new {@code HuntEvent} card with the specified age and unique identifier.
     * This constructor is annotated with {@link JsonCreator} for Jackson JSON deserialization.
     *
     * @param age the historical age/era this card belongs to (passed to super)
     * @param id  the unique identifier of the card (passed to super)
     */
    @JsonCreator
    public HuntEvent(
            @JsonProperty("age") int age,
            @JsonProperty("id") int id
    ) {
        super(age, id);
    }
    /**
     * Internal helper method that iterates through a player's constructed buildings
     * and evaluates them using a {@link HuntEventVisitor}.
     * <p>
     * This allows specific buildings reacting to hunt events to trigger additional rewards
     * or custom effects for that player.
     * </p>
     *
     * @param player  the {@link Player} whose buildings are being checked
     * @param context the current {@link Game} context
     */
    private void checkBuilding(Player player, Game context) {
        HuntEventVisitor visitor = new HuntEventVisitor(context, player);
        ArrayList<Building> buildings = player.getTribe().getBuildings();
        for (Building building : buildings) {
            building.accept(visitor);
        }
    }
    /**
     * Applies the global hunt event effect to all active players.
     * <p>
     * For every player in the game, this method:
     * <ol>
     * <li>Calculates immediate food and prestige rewards based on their hunter count.</li>
     * <li>Applies the rewards directly to the player's status.</li>
     * <li>Triggers building checks via {@link #checkBuilding(Player, Game)}.</li>
     * </ol>
     * Finally, it completely removes the event from the active board layout using {@link #removeEvent(Game)}.
     * </p>
     *
     * @param player  the current active {@link Player} triggering the step (though the effect loops through all players)
     * @param context the current {@link Game} context used to retrieve the player list and board
     */
    @Override
    public void applyEffect(Player player, Game context) {
        int numHunters;
        int prestigeWin;

        ArrayList<Player> players = new ArrayList<>(context.getPlayers());

        for(Player p : players){
            numHunters = p.getTribe().getHuntersSize();
            prestigeWin = numHunters * this.getAge();
            // the prestigeWin depends on the number of hunters and the age of the card

            p.addToNumFood(numHunters);
            p.addToPrestigePoints(prestigeWin);
            checkBuilding(p, context);
        }
        // we add one food and prestige Points for each hunter the player has in his tribe

        removeEvent(context);
    }
}
