package it.polimi.ingsw.am14.exceptions;

/**
 * Exception thrown when the setup of the game is illegal.
 */
public class IllegalSetupException extends ModelException {
    public IllegalSetupException(String message) {
        super(message);
    }
}
