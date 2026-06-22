package it.polimi.ingsw.am14.clientModelController;

import it.polimi.ingsw.am14.network.EntryPointFactory;
import it.polimi.ingsw.am14.network.VirtualServer;
import it.polimi.ingsw.am14.network.messages.events.Event;
import it.polimi.ingsw.am14.view.ViewType;
import it.polimi.ingsw.am14.view.viewEvents.ExceptionViewEvent;
import it.polimi.ingsw.am14.view.viewEvents.NetworkErrorViewEvent;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.am14.utils.Constants.PING_TIMEOUT;
import static it.polimi.ingsw.am14.utils.Constants.TotemColors;
/**
 * The primary controller on the client side, orchestrating network requests and local model sync.
 * <p>
 * This class acts as the mediator between the user view (UI/CLI), the local {@link ClientModel},
 * and the remote infrastructure represented by the {@link VirtualServer} proxy. It enforces defensive,
 * "fail-fast" business rules (checking turn validity, available card indexes, constraints) before
 * uploading actions to the network. Additionally, it implements low-level background heartbeat routines
 * (Ping/Pong tracking) to handle connection losses or timeouts cleanly.
 * </p>
 */
public class ClientController {
    private ClientModel model;
    private VirtualServer server;
    private long lastPingReceived;
    List<Thread> threads = new ArrayList<>();
    private final Object dataLock = new Object();
    /**
     * Attempts to pick a card from the board grid layout on behalf of the home player.
     * <p>
     * Before sending the action across the network proxy, the routine enforces a sequence of local defensive guards:
     * </p>
     * <ul>
     *   <li>Ensures an active server session link exists.</li>
     *   <li>Checks whether the game is currently inside a valid picking phase.</li>
     *   <li>Validates that the selected unique ID lies within legal card indices ({@code cardId <= 116}).</li>
     *   <li>Enforces specific game rules (e.g., preventing explicit collection of structural Event cards between indices 84 and 95).</li>
     *   <li>Verifies if it is currently this player's turn to act.</li>
     *   <li>Calculates row addresses to confirm structural eligibility and available resources.</li>
     * </ul>
     * Any failed rule alerts the view subsystem via {@link ExceptionViewEvent} without hitting the network thread.
     *
     * @param cardId the unique identification index of the card being drafted
     */
    public void pickCard(int cardId) {
       //Model check
        if (server == null) {
            handleException("You are not connected to a server.");
            return;
        }


        if(!model.checkPickState()) {
            handleException("You can't pick a card right now.");
            return;
        }

        //cardId check
        if (cardId > 116) {
            handleException("Invalid card Id.");
            return;
        }

        //Not event check
        if (cardId >= 84 && cardId <= 95) {
            handleException("You can't pick an event card.");
            return;
        }

        //Turn check
        if (!model.checkPickTurn()) {
            handleException("It's not your turn to pick a card.");
            return;
        }

        //Card check in specific rows
        int row = model.findRowFromCardId(cardId);
        if (!model.checkRow(row)) {
            handleException("You can't pick this card.");
            return;
        }

        try {
            server.pickCard(model.getHomePlayerNickname(), cardId, row);
        } catch (Exception e) {
            handleNetworkError("A network error occurred while picking the card.");
        }
    }
    /**
     * Submits a request to position the player's physical scoring totem marker on the shared board pathway.
     * <p>
     * Performs prerequisite model checks including current active state validation, player
     * sequencing validation, and alphanumeric slot alignment verification before dispatching.
     * </p>
     *
     * @param position the unique character key address identifying the destination track coordinate slot
     */
    public void placeTotem(char position) {
        //Model check
        if (server == null) {
            handleException("You are not connected to a server.");
            return;
        }

        //State check
        if (!model.checkPlaceState()) {
            handleException("You can't place a totem right now.");
            return;
        }

        //Turn check
        if (!model.checkPlaceTurn()) {
            handleException("It's not your turn to place a totem.");
            return;
        }

        //Valid position check
        if (!model.checkTotemPosition(position)) {
            handleException("Invalid position.");
            return;
        }

        try {
            server.placeTotem(model.getHomePlayerNickname(), position);
        } catch (Exception e) {
            handleNetworkError("A network error occurred while placing the totem.");
        }
    }
    /**
     * Initializes the client profile workspace, generates an isolated model layer, binds the chosen
     * user UI skin, and establishes connection pipelines toward remote game configurations.
     *
     * @param connectionMode the technology framework targeted for interaction (e.g., "RMI" or "SOCKET")
     * @param serverIP       the remote hostname or network address housing the target application deployment
     * @param serverPort     the active endpoint gateway number open on the target environment
     * @param playerNickname the personal text nickname identifier selected by the user
     * @param view           the direct display observer interface skin receiving visual state outputs
     */
    public void connect(String connectionMode, String serverIP, int serverPort, String playerNickname, ViewType view) {

        model = new ClientModel();
        model.registerObserver(view);

        // Validate server port
        if (serverPort <= 0 || serverPort > 65535) {
            handleException("Invalid port number. Port must be between 1 and 65535.");
            return;
        }

        try {
            server = EntryPointFactory.connect(
                    connectionMode,
                    serverIP,
                    serverPort,
                    playerNickname.toLowerCase(),
                    this
            );
        } catch (Exception e) {
            handleNetworkError("Failed to connect to the server.");
        }
    }
    /**
     * Drops the active network connection proxy safely, sets internal registers to null,
     * and broadcasts warning alerts to prompt view handlers to shift contexts towards home screen templates.
     */
    public void disconnect() {
        if (this.server == null) {
            handleException("You are not connected to a server.");
            return;
        }

        handleNetworkError("Server is not responding. Try to reconnect later. Shutting down connection...");
        this.server = null;
        this.model = null;
    }
    /**
     * Contacts the server entry registries to request the generation of an isolated game session match lobby.
     *
     * @param numPlayers the global limit threshold of human players permitted to participate (restricted to 2-5)
     * @param totemColor the unique cosmetic identifier assigned to track player rankings on tracks
     */
    public void createGame(int numPlayers, String totemColor) {
        //Model check
        if (server == null) {
            handleException("You are not connected to a server.");
            return;
        }

        //Check if the user is already connected to a game
        if (model.getGameId() >= 0) {
            handleException("You are already connected to a game.");
            return;
        }

        //Check numPlayers
        if (numPlayers < 2 || numPlayers > 5) {
            handleException("Invalid number of players.");
            return;
        }

        //Check TotemColor
        if (!List.of(TotemColors).contains(totemColor.toLowerCase())) {
            handleException("Invalid totem color.");
            return;
        }

        try {
            server.createGame(model.getHomePlayerNickname(), numPlayers, totemColor);
        } catch (Exception e) {
            handleNetworkError("A network error occurred while creating the game.");
        }
    }
    /**
     * Attaches the client profile to an pre-existing matches lobby hosted on the remote framework.
     *
     * @param gameId     the unique registration room index key matching the remote session lobby target
     * @param totemColor the custom aesthetic configuration picked to reflect user scoring steps
     */
    public void joinGame(int gameId, String totemColor) {
        //Model check
        if (server == null) {
            handleException("You are not connected to a server.");
            return;
        }

        //Check if the user is already connected to a game
        if (model.getGameId() >= 0) {
            handleException("You are already connected to a game.");
            return;
        }

        //Check TotemColor
        if (!List.of(TotemColors).contains(totemColor.toLowerCase())) {
            handleException("Invalid totem color.");
            return;
        }

        //Check gameId
        if (gameId < 0) {
            handleException("Invalid game ID.");
            return;
        }

        try {
            server.joinGame(model.getHomePlayerNickname(), gameId, totemColor);
        } catch (Exception e) {
            handleNetworkError("A network error occurred while joining the game.");
        }
    }
    /**
     * Extends connection lifespans locally by updating the tracking clock timestamp to match the exact
     * moment an active ping payload safely arrived from the server.
     */
    public void updateLastPing() {
        synchronized (dataLock) {
            lastPingReceived = System.currentTimeMillis();
        }
    }
    /**
     * Launches a dedicated background watchdog thread that loops at regular intervals to monitor
     * connection liveness.
     * <p>
     * If the delta duration between the current clock value and the {@link #lastPingReceived} registry
     * exceeds the configured timeout parameter, it triggers an automated crash disconnect cycle, interrupts
     * downstream sub-threads, and gracefully terminates itself to protect application stability.
     * </p>
     */
    public void checkPing(){
        Thread thread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(PING_TIMEOUT);
                    long now = System.currentTimeMillis();
                    synchronized (dataLock) {
                        if (now - lastPingReceived > PING_TIMEOUT) {
                            disconnect();
                            threads.forEach(Thread::interrupt);
                            threads = new ArrayList<>();
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Client-PingReceiver");
        thread.start();
    }
    /**
     * Registers a background worker thread into the internal tracker pool for unified lifecycle management.
     *
     * @param thread the operational worker {@link Thread} reference being registered
     */
    public void addThread(Thread thread) {
        threads.add(thread);
    }
    /**
     * Sets up the client keep-alive infrastructure by triggering an outbound ping thread towards the server,
     * refreshing timestamps, and launching the watchdog worker routine.
     *
     * @param playerNickname the unique user nickname token transmitting the payload
     */
    public void initializePing(String playerNickname) {
        try {
            addThread(server.pingServer(playerNickname));
            updateLastPing();
            checkPing();
        } catch (Exception e) {
            handleNetworkError("A network error occurred while initializing ping.");
        }
    }
    /**
     * Verifies if a functional network proxy connection channel remains open towards the remote architecture.
     *
     * @return {@code true} if the server interface pointer is safely configured; {@code false} otherwise
     */
    public boolean isConnected() {
        return server != null;
    }
    /**
     * Receives an inbound asynchronous update package coming down from the networking pipelines.
     * <p>
     * Utilizes a deterministic synchronization lock to guarantee thread safety, avoiding race conditions
     * when applying mutations to the local client model and notifying relevant listeners.
     * </p>
     *
     * @param event the inbound functional {@link Event} capsule enclosing structural status updates
     */
    public void update(Event event) {
        synchronized (dataLock) {
            event.effect(model);
            event.onEventProcessed(this);
        }
    }
    /**
     * Encapsulates semantic error data and pushes specific alert tokens down into view observers.
     *
     * @param description text description explaining the exact logical failure encountered
     */
    private void handleException(String description) {
        model.notifyObservers(new ExceptionViewEvent(description));
    }

    private void handleNetworkError(String errorMessage) {
        model.notifyObservers(new NetworkErrorViewEvent(errorMessage));
    }
}
