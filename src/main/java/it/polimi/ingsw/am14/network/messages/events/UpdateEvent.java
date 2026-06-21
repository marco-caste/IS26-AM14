package it.polimi.ingsw.am14.network.messages.events;

/**
 * Abstract superclass representing an update event in the game.
 * Inherits from {@link Event}.
 * Subclasses of this class are used to broadcast specific state changes to the clients.
 */

public abstract class UpdateEvent extends Event{

    /**
     * Constructs an {@code UpdateEvent} with the specified description.
     *
     * @param description a brief explanation or message detailing the update.
     */

    public UpdateEvent(String description) {
        super(description);
    }
}
