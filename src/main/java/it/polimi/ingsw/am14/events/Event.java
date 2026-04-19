package it.polimi.ingsw.am14.events;

import it.polimi.ingsw.am14.view.ClientModel;

public abstract class Event {
    // a brief description of the event, for logging purposes
    protected String description;

    public abstract void effect(ClientModel model);
}
