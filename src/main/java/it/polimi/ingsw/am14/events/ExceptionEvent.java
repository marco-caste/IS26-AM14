package it.polimi.ingsw.am14.events;

import it.polimi.ingsw.am14.view.ClientModel;

public class ExceptionEvent extends Event {

    public ExceptionEvent(String error) {
        this.description = error;
    }

    public void effect(ClientModel model) {
        System.out.println("Error: " + this.description);
    }
}
