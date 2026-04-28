package it.polimi.ingsw.am14;

import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.exceptions.IllegalNicknameException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NetworkLayer implements Observer{

    private final Map<Integer, Set<VirtualClient>> clients;
    private final Map<String, VirtualClient> nicknameToClient;
    private final Map<VirtualClient, EventBuffer> clientToBuffer;

    public NetworkLayer() {
        this.clients = new HashMap<>();
        this.nicknameToClient = new HashMap<>();
        this.clientToBuffer = new HashMap<>();
    }

    public void broadcastUpdate(Event update, int gameId) {
        synchronized (this.clients) {
            for (VirtualClient client : this.clients.get(gameId)) {
                addToEventBuffer(client, update);
            }
        }
    }

    public void updateClients(int gameId, VirtualClient client) {
        synchronized (this.clients) {
            if (!this.clients.containsKey(gameId)) {
                this.clients.put(gameId, new java.util.HashSet<>());
            }
            this.clients.get(gameId).add(client);
        }
    }

    public void updateNicknameMap(String nickname, VirtualClient client) throws IllegalNicknameException {
        synchronized (this.nicknameToClient) {
            if (this.nicknameToClient.containsKey(nickname)) {
                throw new IllegalNicknameException("Nickname already in use");
            }
            this.nicknameToClient.put(nickname, client);
        }
    }

    public void updateBufferMap (VirtualClient client) {
        this.clientToBuffer.put(client, new EventBuffer());
    }

    public void addToEventBuffer(VirtualClient client, Event event) {
        this.clientToBuffer.get(client).addEvent(event);
    }

    public EventBuffer getBuffer(VirtualClient client) {
        return this.clientToBuffer.get(client);
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

    public VirtualClient getClientBy(String nickname) {
        synchronized (this.nicknameToClient) {
            return this.nicknameToClient.get(nickname);
        }
    }
}
