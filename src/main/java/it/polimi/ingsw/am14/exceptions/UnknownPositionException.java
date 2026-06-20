package it.polimi.ingsw.am14.exceptions;

/**
 * Exception thrown when a position is not recognized when placing players' totems.
 */
public class UnknownPositionException extends ModelException {
    public UnknownPositionException(String message) {
        super(message);
    }
}
