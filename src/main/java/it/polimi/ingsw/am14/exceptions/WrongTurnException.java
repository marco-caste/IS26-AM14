package it.polimi.ingsw.am14.exceptions;

/**
 * Exception thrown when a player tries to make a move out of turn.
 */
public class WrongTurnException extends ModelException {
    public WrongTurnException(String message) {
        super(message);
    }
}
