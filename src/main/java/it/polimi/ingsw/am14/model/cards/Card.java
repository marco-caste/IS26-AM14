package it.polimi.ingsw.am14.model.cards;

import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.Player;
/**
 * The abstract base class representing a generic card within the game.
 * <p>
 * This class defines the core attributes shared by all cards, such as their historical
 * era/age and a unique identifier. It enforces the contract for applying card effects
 * and validating whether a card can be picked or purchased by a specific player.
 * </p>
 */
public abstract class Card {
    private final int age;
    private final int id;
    /**
     * Constructs a core {@code Card} instance with its defining age and identity.
     *
     * @param age the historical age or era this card belongs to
     * @param id  the unique identifier assigned to this specific card
     */
    public Card(int age, int id) {
        this.age = age;
        this.id = id;
    }

    public int getAge(){
        return age;
    }

    public int getId(){
        return id;
    }
    /**
     * Applies the card's unique game effect to the player or the broader game context.
     * <p>
     * This method must be implemented by all concrete subclasses extending {@code Card}.
     * It is designed to be triggered immediately when a player successfully drafts,
     * triggers, or purchases the card during their turn.
     * </p>
     *
     * @param player  the {@link Player} currently interacting with or claiming the card
     * @param context the current {@link Game} state and orchestration context
     */
    public abstract void applyEffect(Player player, Game context) ;
    // this method will be implemented in the classes that extend Card,
    // and will apply the effect of the card to the player passed as a parameter
    // we will call this method when the player picks the card
    /**
     * Evaluates whether the card is currently eligible to be picked or purchased by the given player.
     * <p>
     * The business logic varies significantly depending on the concrete type of the card:
     * <ul>
     *   <li><b>Character Cards:</b> Typically return {@code true} as they are freely draftable.</li>
     *   <li><b>Event Cards:</b> Typically return {@code false} as they are resolved globally rather than kept.</li>
     *   <li><b>Building Cards:</b> Return {@code true} only if the player currently meets the specific resource or food requirements to afford them; {@code false} otherwise.</li>
     * </ul>
     * </p>
     *
     * @param player the {@link Player} trying to pick or purchase this card
     * @return {@code true} if the card satisfies the picking conditions for the player; {@code false} otherwise
     */
    public abstract boolean isPickable(Player player);
    // if the card is:
    // Character : returns true
    // Event : returns false
    // Building : returns true if the player has enough food to buy it, false otherwise
}
