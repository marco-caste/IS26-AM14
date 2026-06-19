package it.polimi.ingsw.am14.exceptions;

/**
 * Exception thrown when an illegal game ID is encountered.
 */
public class IllegalGameIdException extends ModelException {
    public IllegalGameIdException(String message) {
        super(message);
    }
}
