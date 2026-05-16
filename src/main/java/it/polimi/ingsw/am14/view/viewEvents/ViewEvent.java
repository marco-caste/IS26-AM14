package it.polimi.ingsw.am14.view.viewEvents;

import it.polimi.ingsw.am14.view.ViewType;

public interface ViewEvent {
    /**
     * <p>
     *     This method is used to render the event on the view. It will be called in the network layer,
     *     once a network event has been received on the server.
     *     It will need to cast the view to the correct type in order to call the correct methods.
     *     Each different ViewEvent will then call a series of methods exposed by the view, in order to
     *     update the view with the new information.
     * </p>
     * @param view not null, the view that will handle this event
     */
    void render(ViewType view);
}
