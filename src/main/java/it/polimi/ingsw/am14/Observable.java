package it.polimi.ingsw.am14;

import it.polimi.ingsw.am14.events.Event;

public interface Observable {

    void addObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers(Event event);
}
