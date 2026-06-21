package it.polimi.ingsw.am14.model.cards.deckCards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.buildings.visitors.SustenanceEventVisitor;
import it.polimi.ingsw.am14.model.gameLogic.*;
import it.polimi.ingsw.am14.model.boardComponents.Board;

import java.util.ArrayList;
/**
 * Represents a Sustenance Event card within the game deck.
 * <p>
 * This class extends {@link EventCard} and implements the global tribe feeding mechanic.
 * When triggered, every player must feed each member of their tribe using their available food reserves.
 * Failing to fully sustain the tribe results in a prestige point penalty proportional to the missing
 * food and the card's age. Conversely, protective building effects are evaluated beforehand using
 * the Visitor Pattern to mitigate potential food shortages.
 * </p>
 */
public class SustenanceEvent extends EventCard {
    /**
     * Constructs a new {@code SustenanceEvent} card with the specified age and unique identifier.
     * This constructor is annotated with {@link JsonCreator} for Jackson JSON deserialization.
     *
     * @param age the historical age/era this card belongs to (passed to super)
     * @param id  the unique identifier of the card (passed to super)
     */
    @JsonCreator
    public SustenanceEvent(
            @JsonProperty("age") int age,
            @JsonProperty("id") int id
    ) {
        super(age, id);
    }
    /**
     * Applies the global sustenance event effect to all active players in the game.
     * <p>
     * For each player, the feeding sequence executes as follows:
     * <ol>
     * <li>The initial food stock is recorded to prevent passive gains from defensive buildings.</li>
     * <li>Buildings are checked via {@link #checkBuilding(Player, Game)} to apply event-specific production or discounts.</li>
     * <li>The total food demand is calculated by summing all tribe members (Hunters, Gatherers, Shamans, Builders, Inventors, Artists)
     *     and subtracting the player's cumulative gathering discount.</li>
     * <li>If a food deficit occurs ({@code missingFood > 0}), the player's food resource is depleted into the negative
     *     (or cleared, depending on model constraints) and they suffer a prestige penalty equal to {@code missingFood * cardAge}.</li>
     * <li>If the player successfully feeds their tribe, a validation check ensures their final food count does not exceed
     *     their starting stock, clamping it back to {@code oldFood} if necessary.</li>
     * </ol>
     * </p>
     *
     * @param playerNotUsed the current active player token (unused as the loop evaluates all players globally)
     * @param context       the current {@link Game} context containing the player roster
     */
    @Override
    public void applyEffect(Player playerNotUsed, Game context) {
        int totalFoodNeeded;
        int missingFood;
        int prestigeLoss;

        ArrayList<Player> players = new ArrayList<>(context.getPlayers());

        for (Player player : players) {
            //Saving the old amount in order to apply a discount and not give food
            int oldFood = player.getNumFood();
            //Adding food based on building
            checkBuilding(player, context);

            //calculating the total food needed for the player to feed his tribe
            totalFoodNeeded =
                    player.getTribe().getHuntersSize() +
                            player.getTribe().getGatherersSize() +
                            player.getTribe().getShamansSize() +
                            player.getTribe().getBuildersSize() +
                            player.getTribe().getInventorsSize() +
                            player.getTribe().getArtistsSize() -
                            player.getTotalGatheringDiscount();

            missingFood = totalFoodNeeded - player.getNumFood(); // calculating the missing food for the player
            prestigeLoss = missingFood * this.getAge(); // calculating the prestige loss for the player

            player.addToNumFood(-totalFoodNeeded);
            if (missingFood > 0) { // if the player doesn't have enough food, he will lose all his food and some prestige points
                player.addToPrestigePoints(-prestigeLoss);
            }
            else {
                //check: if you have more food then before you come back to the old amount
                if (player.getNumFood() > oldFood)
                    player.setNumFood(oldFood);
            }
        }
    }

    private void checkBuilding(Player player, Game context) {
        SustenanceEventVisitor visitor = new SustenanceEventVisitor(context, player);
        ArrayList<Building> buildings = player.getTribe().getBuildings();
        for (Building building : buildings) {
            building.accept(visitor);
        }
    }
    /**
     * Handles the specific discarding routine for sustenance events.
     * <p>
     * Unlike generic events, this overrides the default behavior to route this card directly
     * into the board's dedicated row for pending sustenance events ({@code resolvingSustenanceEventsRow})
     * before clearing its index slot from the deckable row.
     * </p>
     *
     * @param board     the {@link Board} instance where the card layout is tracked
     * @param cardIndex the index position of this card within its layout row
     */
    @Override
    public void discard(Board board, int cardIndex) {
        board.addToResolvingSustenanceEventsRow(this);
        board.removeFromBottomDeckableRow(cardIndex);
    }


}
