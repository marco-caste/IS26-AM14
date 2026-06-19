package it.polimi.ingsw.am14.exceptions;

/**
 * Exception thrown when a totem placement is illegal.
 */
public class IllegalPlacementException extends ModelException {
    public IllegalPlacementException(String message) {
        super(message);
    }
}
