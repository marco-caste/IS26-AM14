package it.polimi.ingsw.am14.model.cards.deckCards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.buildings.visitors.*;
import it.polimi.ingsw.am14.model.gameLogic.*;

import java.util.ArrayList;
/**
 * Represents a Paint Event card within the game.
 * <p>
 * This class extends {@link EventCard}. When activated, it evaluates the number of Artists
 * in each player's tribe against a threshold determined by the card's age.
 * Players with too few artists suffer a prestige penalty, while players exceeding the threshold
 * receive a prestige reward. It also checks for building interactions using the Visitor Pattern.
 * </p>
 */
public class PaintEvent extends EventCard {
    /**
     * Constructs a new {@code PaintEvent} card with the specified age and unique identifier.
     * This constructor is annotated with {@link JsonCreator} for Jackson JSON deserialization.
     *
     * @param age the historical age/era this card belongs to (passed to super)
     * @param id  the unique identifier of the card (passed to super)
     */
    @JsonCreator
    public PaintEvent(
            @JsonProperty("age") int age,
            @JsonProperty("id") int id
    ) {
        super(age, id);
    }

    private void checkBuilding(Player player, Game context) {
        PaintEventVisitor visitor = new PaintEventVisitor(context, player);
        ArrayList<Building> buildings = player.getTribe().getBuildings();
        for (Building building : buildings) {
            building.accept(visitor);
        }
    }
    /**
     * Applies the global paint event effect to all players in the game.
     * <p>
     * For every player, this method computes a threshold value: {@code lossRange = age - 1}.
     * </p>
     * <ul>
     *   <li>If the player's artist count is <b>less than or equal to</b> the {@code lossRange},
     *       they lose prestige points equal to twice their artist count.</li>
     *   <li>If the player's artist count is <b>greater than</b> the {@code lossRange},
     *       they gain prestige points equal to their artist count multiplied by the card's age.</li>
     * </ul>
     * After updating the prestige points, it evaluates the player's buildings via {@link #checkBuilding(Player, Game)}
     * and finally cleans up the board state by invoking {@link #removeEvent(Game)}.
     *
     * @param player  the current active {@link Player} triggering the step (the effect applies to all players)
     * @param context the current {@link Game} context used to access the player list and the board
     */
    @Override
    public void applyEffect(Player player, Game context) {

        int lossRange = this.getAge() - 1 ; // it's a formula which gives us the range of artists that cause a loss of prestige points
        int numArtists;
        int prestigeLoss;  // gives the number of prestige loss if the player has a number of artists <= lossRange
        int prestigeWin;    // gives the number of prestige win if the player has a number of artists > lossRange

        ArrayList<Player> players = new ArrayList<>(context.getPlayers());

        for (Player p : players) {

            numArtists = p.getTribe().getArtistsSize();
            prestigeWin = numArtists * this.getAge() ;
            prestigeLoss = numArtists * 2;

            if (numArtists <= lossRange)
                p.addToPrestigePoints(-prestigeLoss);
            else
                p.addToPrestigePoints(prestigeWin);

            checkBuilding(p, context);

        }
        removeEvent(context);
    }


}
