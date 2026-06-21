package it.polimi.ingsw.am14.exceptions;

/**
 * Exception thrown when an action is attempted in an invalid state.
 */
public class WrongStateException extends ModelException {
    public WrongStateException(String message) {
        super(message);
    }
}
