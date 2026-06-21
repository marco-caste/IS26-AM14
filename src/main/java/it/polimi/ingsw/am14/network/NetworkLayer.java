package it.polimi.ingsw.am14.network;

import it.polimi.ingsw.am14.network.messages.events.Event;
import it.polimi.ingsw.am14.exceptions.IllegalNicknameException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/**
 * The central network management layer responsible for mapping and coordinating connected clients.
 * It provides thread-safe access to associations between users (nicknames), active games (game IDs),
 * their active connections ({@link VirtualClient}), dedicated server threads, and message queues ({@link EventBuffer}).
 */
public class NetworkLayer implements Observer {

    private final Map<Integer, Set<VirtualClient>> gameIdToClients;
    private final Map<String, VirtualClient> nicknameToClient;
    private final Map<VirtualClient, EventBuffer> clientToBuffer;
    private final Map<VirtualClient, ArrayList<Thread>> clientToThreads;
    /**
     * Constructs a new {@code NetworkLayer}, initializing all the underlying management maps.
     */
    public NetworkLayer() {
        this.gameIdToClients = new HashMap<>();
        this.nicknameToClient = new HashMap<>();
        this.clientToBuffer = new HashMap<>();
        this.clientToThreads = new HashMap<>();
    }
    /**
     * Broadcasts an event to all clients currently taking part in the specified game.
     *
     * @param update the {@link Event} to broadcast.
     * @param gameId the identifier of the game whose clients should receive the event.
     */
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
    /**
     * Associates a client with a specific game ID in the network layer.
     *
     * @param gameId the identifier of the game.
     * @param client the {@link VirtualClient} joining the game.
     */
    public void updateClients(int gameId, VirtualClient client) {
        synchronized (this.gameIdToClients) {
            if (!this.gameIdToClients.containsKey(gameId)) {
                this.gameIdToClients.put(gameId, new java.util.HashSet<>());
            }
            this.gameIdToClients.get(gameId).add(client);
        }
    }
    /**
     * Binds a unique nickname to a newly connected client.
     *
     * @param nickname the requested username.
     * @param client   the {@link VirtualClient} requesting the nickname.
     * @throws IllegalNicknameException if the nickname is already registered by another active client.
     */
    public void updateNicknameMap(String nickname, VirtualClient client) throws IllegalNicknameException {
        synchronized (this.nicknameToClient) {
            if (this.nicknameToClient.containsKey(nickname)) {
                throw new IllegalNicknameException("Nickname already in use");
            }
            this.nicknameToClient.put(nickname, client);
        }
    }
    /**
     * Registers a background thread associated with a specific client.
     *
     * @param client the {@link VirtualClient} being managed.
     * @param thread the {@link Thread} handling the client's operations.
     */
    public void updateClientThreads(VirtualClient client, Thread thread) {
        synchronized (this.clientToThreads) {
            if (!this.clientToThreads.containsKey(client)) {
                this.clientToThreads.put(client, new ArrayList<>());
            }
            this.clientToThreads.get(client).add(thread);
        }
    }
    /**
     * Creates and registers a new, empty {@link EventBuffer} for the specified client.
     *
     * @param client the {@link VirtualClient} to set up the buffer for.
     */
    public void updateBufferMap (VirtualClient client) {
        synchronized (this.clientToBuffer) {
            this.clientToBuffer.put(client, new EventBuffer());
        }
    }
    /**
     * Adds an event to the specific buffer of a designated client.
     *
     * @param client the target {@link VirtualClient}.
     * @param event  the {@link Event} to enqueue.
     */
    public void addToEventBuffer(VirtualClient client, Event event) {
        synchronized (this.clientToBuffer) {
            EventBuffer buffer = this.clientToBuffer.get(client);
            if (buffer != null) {
                buffer.addEvent(event);
            }
        }
    }
    /**
     * Retrieves the event buffer assigned to an active client.
     *
     * @param client the {@link VirtualClient} whose buffer is being queried.
     * @return the client's {@link EventBuffer}, or {@code null} if one does not exist.
     */
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
    /**
     * Retrieves the client interface bound to the given nickname.
     *
     * @param nickname the username to look up.
     * @return the associated {@link VirtualClient}, or {@code null} if not found.
     */
    public VirtualClient getClientBy(String nickname) {
        synchronized (this.nicknameToClient) {
            return this.nicknameToClient.get(nickname);
        }
    }
    /**
     * Removes the mapping of a nickname from the network registry, usually upon disconnection.
     *
     * @param nickname the username to unbind.
     */
    public void removeClientBy(String nickname) {
        synchronized (this.nicknameToClient) {
            this.nicknameToClient.remove(nickname);
        }
    }
    /**
     * Completely removes a game and its client affiliations from the registry.
     *
     * @param gameId the identifier of the game to dispel.
     */
    public void removeGame(int gameId) {
        synchronized (this.gameIdToClients) {
            this.gameIdToClients.remove(gameId);
        }
    }
    /**
     * Deletes the designated event buffer assigned to a specific client.
     *
     * @param client the {@link VirtualClient} whose buffer is to be destroyed.
     */
    public void removeBuffer(VirtualClient client) {
        synchronized (this.clientToBuffer) {
            this.clientToBuffer.remove(client);
        }
    }
    /**
     * Unregisters a single client from a specific game's player registry.
     *
     * @param client the {@link VirtualClient} to remove.
     * @param gameId the target game ID.
     */
    public void removeClientFromGameId(VirtualClient client, int gameId) {
        synchronized (this.gameIdToClients) {
            if (this.gameIdToClients.containsKey(gameId)) {
                this.gameIdToClients.get(gameId).remove(client);
            }
        }
    }
    /**
     * Notifies and wakes up any threads currently blocking and waiting on the given client's event buffer.
     * Used primarily during teardown procedures.
     *
     * @param client the target {@link VirtualClient}.
     */
    public void notifyBufferForClient(VirtualClient client) {
        EventBuffer buffer = getBuffer(client);
        if (buffer != null) {
            synchronized (buffer) {
                buffer.notifyAll();
            }
        }
    }
    /**
     * Interrupts and evicts all server threads tracked and tied to the specified client.
     *
     * @param client the {@link VirtualClient} being terminated.
     */
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
    /**
     * Broadcasts a network-level keep-alive ping to every single client connected to the server.
     */
    public void pingAllClients() {
        synchronized (this.nicknameToClient) {
            for (VirtualClient client : this.nicknameToClient.values()) {
                try {
                    client.pingClient();
                } catch (Exception _) {
                }
            }
        }
    }
}
