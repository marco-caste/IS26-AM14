package it.polimi.ingsw.am14.exceptions;

/**
 * Exception thrown when a card pick is illegal.
 */
public class IllegalPickException extends ModelException {
    public IllegalPickException(String message) {
        super(message);
    }
}
