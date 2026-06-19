package it.polimi.ingsw.am14.exceptions;

/**
 * Exception thrown when a player is forced to pick a specific card or resource in the game.
 */
public class ForcedPickException extends ModelException {
    public ForcedPickException(String message) {
        super(message);
    }
}
