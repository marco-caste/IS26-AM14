package it.polimi.ingsw.am14;

import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.socket.server.SocketClientHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NetworkLayer implements Observer{

    private final Map<Integer, Set<VirtualView>> clients;
    private final Map<String, VirtualView> nicknameToClient;
    private final Set<VirtualView> waitingClients;


    public NetworkLayer() {
        this.clients = new HashMap<>();
        this.nicknameToClient = new HashMap<>();
        this.waitingClients = new HashSet<>();
    }

    public void broadcastUpdate(Event update, int gameId) {
        synchronized (this.clients) {
            for (VirtualView client : this.clients.get(gameId)) {
                client.addToEventBuffer(update);
            }
        }
    }

    public void updateClients(int gameId, VirtualView client) {
        synchronized (this.clients) {
            if (!this.clients.containsKey(gameId)) {
                this.clients.put(gameId, new java.util.HashSet<>());
            }
            this.clients.get(gameId).add(client);
        }
    }

    public void updateMap(String nickname, VirtualView client) {
        synchronized (this.nicknameToClient) {
            this.nicknameToClient.put(nickname, client);
        }
    }

    /**
     * <p>
     *     receives an event directly from the model and manages the actual update of the clients
     *     distinguishing between a direct update and a broadcast update, according to the type of event received
     * </p>
     * @param event not null - the event to update the client with
     */
    @Override
    public void update(Event event) {
        // will add the event to the buffers of the clients and then return.
        event.bufferize(this);
    }

    public VirtualView getClientBy(String nickname) {
        synchronized (this.nicknameToClient) {
            return this.nicknameToClient.get(nickname);
        }
    }

    public void addWaitingClient(VirtualView handler) {
        synchronized (this.waitingClients) {
            this.waitingClients.add(handler);
        }
    }

    public boolean containsWaiting(SocketClientHandler sender) {
        synchronized (this.waitingClients) {
            return this.waitingClients.contains(sender);
        }
    }

    public void removeFromWaiting(SocketClientHandler sender) {
        synchronized (this.waitingClients) {
            this.waitingClients.remove(sender);
        }
    }
}
