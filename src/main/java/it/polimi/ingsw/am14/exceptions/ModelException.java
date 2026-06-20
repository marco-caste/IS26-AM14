package it.polimi.ingsw.am14.exceptions;

/**
 * General type exception, thrown when an error occurs in the model layer of the application.
 */
public class ModelException extends Exception {
    public ModelException(String message) {
        super(message);
    }
}
