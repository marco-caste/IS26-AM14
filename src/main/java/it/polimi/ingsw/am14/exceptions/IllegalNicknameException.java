package it.polimi.ingsw.am14.exceptions;

/**
 * Exception thrown when a nickname is illegal.
 */
public class IllegalNicknameException extends ModelException {
    public IllegalNicknameException(String message) {
        super(message);
    }
}
