package it.polimi.ingsw.am14.exceptions;

/**
 * Exception thrown when a nickname is not recognized in the system.
 */
public class UnknownNicknameException extends ModelException {
    public UnknownNicknameException(String message) {
        super(message);
    }
}
