package it.polimi.ingsw.am14.network;

import it.polimi.ingsw.am14.network.messages.events.Event;
import it.polimi.ingsw.am14.exceptions.IllegalNicknameException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NetworkLayer implements Observer {

    // TODO: FINE SINCRONIZZAZIONE
    private final Map<Integer, Set<VirtualClient>> gameIdToClients;
    private final Map<String, VirtualClient> nicknameToClient;
    private final Map<VirtualClient, EventBuffer> clientToBuffer;
    private final Map<VirtualClient, ArrayList<Thread>> clientToThreads;

    public NetworkLayer() {
        this.gameIdToClients = new HashMap<>();
        this.nicknameToClient = new HashMap<>();
        this.clientToBuffer = new HashMap<>();
        this.clientToThreads = new HashMap<>();
    }

    public void broadcastUpdate(Event update, int gameId) {
        synchronized (this.gameIdToClients) {
            Set<VirtualClient> clients = this.gameIdToClients.get(gameId);
            if (clients != null) {
                for (VirtualClient client : clients) {
                    addToEventBuffer(client, update);
                }
            }
        }
    }

    public void updateClients(int gameId, VirtualClient client) {
        synchronized (this.gameIdToClients) {
            if (!this.gameIdToClients.containsKey(gameId)) {
                this.gameIdToClients.put(gameId, new java.util.HashSet<>());
            }
            this.gameIdToClients.get(gameId).add(client);
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

    public void updateClientThreads(VirtualClient client, Thread thread) {
        synchronized (this.clientToThreads) {
            if (!this.clientToThreads.containsKey(client)) {
                this.clientToThreads.put(client, new ArrayList<>());
            }
            this.clientToThreads.get(client).add(thread);
        }
    }

    public void updateBufferMap (VirtualClient client) {
        synchronized (this.clientToBuffer) {
            this.clientToBuffer.put(client, new EventBuffer());
        }
    }

    public void addToEventBuffer(VirtualClient client, Event event) {
        synchronized (this.clientToBuffer) {
            EventBuffer buffer = this.clientToBuffer.get(client);
            if (buffer != null) {
                buffer.addEvent(event);
            }
        }
    }

    public EventBuffer getBuffer(VirtualClient client) {
        synchronized (this.clientToBuffer) {
            return this.clientToBuffer.get(client);
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

    public VirtualClient getClientBy(String nickname) {
        synchronized (this.nicknameToClient) {
            return this.nicknameToClient.get(nickname);
        }
    }

    public void removeClientBy(String nickname) {
        synchronized (this.nicknameToClient) {
            this.nicknameToClient.remove(nickname);
        }
    }

    public void removeGame(int gameId) {
        synchronized (this.gameIdToClients) {
            this.gameIdToClients.remove(gameId);
        }
    }

    public void removeBuffer(VirtualClient client) {
        synchronized (this.clientToBuffer) {
            this.clientToBuffer.remove(client);
        }
    }

    public void removeClientFromGameId(VirtualClient client, int gameId) {
        synchronized (this.gameIdToClients) {
            if (this.gameIdToClients.containsKey(gameId)) {
                this.gameIdToClients.get(gameId).remove(client);
            }
        }
    }

    public void notifyBufferForClient(VirtualClient client) {
        EventBuffer buffer = getBuffer(client);
        if (buffer != null) {
            synchronized (buffer) {
                buffer.notifyAll();
            }
        }
    }

    public void removeThreadsByClient(VirtualClient client) {
        synchronized (this.clientToThreads) {
            if (this.clientToThreads.containsKey(client)) {
                for (Thread thread : this.clientToThreads.get(client)) {
                    thread.interrupt();
                }
                this.clientToThreads.remove(client);
            }
        }
    }

    public void pingAllClients() {
        synchronized (this.nicknameToClient) {
            for (VirtualClient client : this.nicknameToClient.values()) {
                try {
                    client.pingClient();
                } catch (Exception e) {
                    // TODO: è normale che il client venga pingato 4 volte dopo la disconnessione, deve scadere il timeout.
                    // potrebbe essere non necessario stampare questo errore.
                    System.err.println("[NetworkLayer] Failed to ping client: " + e.getMessage());
                }
            }
        }
    }
}
