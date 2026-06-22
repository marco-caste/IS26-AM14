package it.polimi.ingsw.am14.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.network.messages.events.ExceptionEvent;
import it.polimi.ingsw.am14.network.messages.events.LoginSuccesfulEvent;
import it.polimi.ingsw.am14.network.messages.events.PlayerDisconnectedEvent;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.exceptions.*;
import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.GameFactory;
import it.polimi.ingsw.am14.utils.Codec;
import it.polimi.ingsw.am14.utils.JsonCodec;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static it.polimi.ingsw.am14.utils.Constants.PING_TIMEOUT;
/**
 * The root orchestrator and session gateway on the server side.
 * <p>
 * This class serves as the entry point for global cluster management, directing operations
 * before players enter a distinct game loop context. It maintains multi-tenant indexing dictionaries
 * linking player nicknames to active match rooms and mapping unique match IDs to specific {@link GameController}
 * sub-units.
 * </p>
 * <p>
 * Key infrastructure duties include:
 * </p>
 * <ul>
 * <li><b>User Onboarding:</b> Handling entry authentication registration and handling active session reconnection loops.</li>
 * <li><b>Liveness Watchdog:</b> Monitoring background heartbeats to safely disconnect unresponsive socket/RMI clients.</li>
 * <li><b>Asynchronous Persistence:</b> Spawning detached non-blocking background threads to serialize
 * the complete system state into cold storage after critical actions (picks, placements).</li>
 * </ul>
 */
public class LobbyController {
    private final Map<Integer, GameController> gameIdToGameController;
    private final Map<String, Integer> nicknameToGameId;
    private int gameIdCounter;
    @JsonIgnore
    private NetworkLayer networkLayer;        // TODO: CHECK SINCRONIZZAZIONE
    @JsonIgnore
    private final Map<String, Long> lastPingReceivedTime;
    @JsonIgnore
    private final Object dataLock = new Object();
    private String saveFilePath;
    @JsonIgnore
    private final Object saveFileLock = new Object();
    /**
     * Standard constructor initializing an empty standalone server lobby context.
     *
     * @param networkLayer the outbound packet transmission bridge pipeline
     * @param saveFilePath target file system directory address for writing state recovery data
     */
    public LobbyController(NetworkLayer networkLayer, String saveFilePath) {
        this.gameIdToGameController = new HashMap<>();
        this.nicknameToGameId = new HashMap<>();
        this.networkLayer = networkLayer;
        this.lastPingReceivedTime = new HashMap<>();
        this.gameIdCounter = 0;
        this.saveFilePath = saveFilePath;
    }
    /**
     * Reconstructive constructor utilizing Jackson mapping components to inflate an entire historical
     * deployment node back into volatile memory after a server crash shutdown.
     *
     * @param nicknameToGameId       historical user session assignment registry map
     * @param gameIdToGameController historical sub-controller map populated with deserialized match data
     * @param gameIdCounter          the last verified state configuration counter tracking active indexes
     * @param saveFilePath           target backup storage path matching physical deployment setups
     */
    @JsonCreator
    public LobbyController(
            @JsonProperty("nicknameToGameId") Map<String, Integer> nicknameToGameId,
            @JsonProperty("gameIdToGameController") Map<Integer, GameController> gameIdToGameController,
            @JsonProperty("gameIdCounter") int gameIdCounter,
            @JsonProperty("saveFilePath") String saveFilePath) {
        this.saveFilePath = saveFilePath;
        this.gameIdToGameController = gameIdToGameController;
        this.nicknameToGameId = nicknameToGameId;
        this.networkLayer = new NetworkLayer();
        this.lastPingReceivedTime = new HashMap<>();
        this.gameIdCounter = gameIdCounter;
    }
    /**
     * Appends a newly compiled game sub-controller entity into the centralized dictionary cache.
     */
    private void addGameController(int gameId, GameController gameController) {
        gameIdToGameController.put(gameId, gameController);
    }
    /**
     * Discards a sub-controller mapping from internal registers, typically triggered upon match conclusion.
     */
    private void removeGameController(int gameId) {
        gameIdToGameController.remove(gameId);
    }
    /**
     * Attempts safe lookup retrieval of a sub-controller entity using an optional container wrapper.
     */
    private Optional<GameController> getGameController(int gameId) {
        return Optional.ofNullable(gameIdToGameController.get(gameId));
    }
    /**
     * Assigns a structural binding pairing an authenticated user nickname token to a room index tracker.
     */
    private void assignPlayerToGame(String nickname, int gameId) {
        nicknameToGameId.put(nickname, gameId);
    }
    /**
     * Purges a player's credentials out of multi-tenant assignment dictionaries completely.
     */
    private void removePlayerFromLobby(String nickname) {
        nicknameToGameId.remove(nickname);
    }
    /**
     * Thread-safe query utility retrieving the current match identification index assigned to a user nickname.
     *
     * @param nickname targeted player signature text string under evaluation
     * @return an {@link Optional} containing the matched room index code integer, or empty if unmapped
     */
    public Optional<Integer> getGameIdByNickname(String nickname) {
        synchronized (dataLock) {
            return Optional.ofNullable(nicknameToGameId.get(nickname));
        }
    }
    /**
     * Stages a newborn user signature inside the baseline lobby environment, initializing tracking pointers to null.
     */
    private void addPlayerToLobby(String nickname) {
        nicknameToGameId.put(nickname, null);
    }
    /**
     * Evaluates if an active user profile signature currently holds a non-null match room assignment index.
     */
    private boolean hasPlayerAGameAssigned(String nickname) {
        return nicknameToGameId.containsKey(nickname) && nicknameToGameId.get(nickname) != null;
    }
    /**
     * Performs a reverse dictionary lookup traversal to isolate all user nickname keys tied to a room index.
     */
    private Set<String> getPlayersByGameId(int gameId) {
        Set<String> players = new HashSet<>();
        Set<Map.Entry<String, Integer>> entries = nicknameToGameId.entrySet();
        for (Map.Entry<String, Integer> entry : entries) {
            if (entry.getValue() != null && entry.getValue() == gameId) {
                players.add(entry.getKey());
            }
        }
        return players;
    }
    /**
     * Captures the system's exact current calendar timestamp and updates a player's liveness tracker.
     */
    private void setLastPingReceivedTime(String nickname) {
        synchronized (dataLock) {
            lastPingReceivedTime.put(nickname, System.currentTimeMillis());
        }
    }
    /**
     * Evaluates if a player profile has an active heartbeat registration tracking vector inside memory.
     */
    private boolean isPlayerConnected(String nickname) {
        synchronized (dataLock) {
            return lastPingReceivedTime.containsKey(nickname);
        }
    }
    /**
     * Binds a fresh outbound communication engine wrapper, supporting system hot-swaps after network resets.
     *
     * @param networkLayer active low-level messaging transport subsystem instance
     */
    public void setNetworkLayer(NetworkLayer networkLayer) {
        this.networkLayer = networkLayer;
    }
    /**
     * Clears internal connection heartbeat indexes mapped to a user signature.
     */
    private void removeLastPingReceivedTime(String nickname) {
        lastPingReceivedTime.remove(nickname);
    }
    /**
     * Processes inbound user entries requesting credential authorization or recovery mapping connections.
     * <p>
     * Logic constraints:
     * </p>
     * <ul>
     * <li>If the nickname matches a historical active match placement, it initiates structural reconnection hooks.</li>
     * <li>If the nickname represents a first-time login, it registers the client into the global staging pool.</li>
     * </ul>
     * Dispatches a {@link LoginSuccesfulEvent} forward down the wire upon completion to update client states.
     *
     * @param nickname text signature identifier selected by the client framework node
     * @throws IllegalGameIdException if a historical match indexing record points to an absent engine instance
     * @throws WrongStateException    if structural model locks reject entry parameters down the pipeline
     */
    public void connectUser(String nickname) throws IllegalNicknameException, IllegalGameIdException, WrongStateException {
        int gameId;
        synchronized (dataLock) {
            if (hasPlayerAGameAssigned(nickname)) {
                gameId = getGameIdByNickname(nickname).orElseThrow();
            }
            else {
                addPlayerToLobby(nickname);
                gameId = -1;
            }
        }
        networkLayer.update(new LoginSuccesfulEvent(nickname));
        setLastPingReceivedTime(nickname);
        if (gameId >=0) {
            getGameController(gameId).orElseThrow(() -> new IllegalGameIdException("Game with the specified ID does not exist"))
                    .onPlayerConnection(nickname, null);
        }
    }
    /**
     * Compiles a separate game core model node using structural factories, binds observers,
     * hooks up sub-controllers, and assigns the creating player into the newborn match workspace context.
     * <p>
     * <b>Non-Blocking IO Pattern:</b> Spawns an ephemeral worker thread named {@code "Save-Thread-pick"}
     * to perform a background disk write task right after creation, ensuring zero delay on network queues.
     * </p>
     *
     * @param nickname   the profile username creating the session lobby environment
     * @param numPlayers systemic scaling threshold sizing limits assigned to the room (2-5)
     * @param totemColor cosmetic identification option key chosen by the host player
     * @throws IllegalSetupException if structural scaling configurations fail boundary tests inside factories
     * @throws WrongStateException   if state validation constraints block structural insertions
     */
    public void createGame(String nickname, int numPlayers, String totemColor) throws IllegalSetupException, WrongStateException, IllegalNicknameException {
        synchronized (dataLock) {
            if (hasPlayerAGameAssigned(nickname)) {
                networkLayer.update(new ExceptionEvent("Player with the specified nickname is already assigned to a game"));
                return;
            }
            Game game = GameFactory.setup(numPlayers, gameIdCounter);
            game.addObserver(networkLayer);
            GameController gameController = new GameController(game);
            addGameController(game.getGameId(), gameController);
            assignPlayerToGame(nickname, game.getGameId());
            gameIdCounter++;
            gameController.onPlayerConnection(nickname, totemColor);
        }
        new Thread(() -> {
            synchronized (saveFileLock) {
                Codec parser = new JsonCodec();
                parser.writeSaveData(this, saveFilePath);
            }
        }, "Save-Thread-pick").start();
    }
    /**
     * Adds an authenticated staging player profile into an ongoing pre-existing match room.
     *
     * @param nickname   the client text username requesting room integration
     * @param gameId     the unique room number targeted for interaction
     * @param totemColor aesthetic marking option parameter picked by the returning player
     * @throws IllegalGameIdException if the target integer mapping index fails to return a valid instance
     * @throws WrongStateException    if room capacity boundaries or lifecycle states reject registration parameters
     */
    public void joinGame(String nickname, int gameId, String totemColor) throws WrongStateException, IllegalGameIdException {
        synchronized (dataLock) {
            if (hasPlayerAGameAssigned(nickname)) {
                networkLayer.update(new ExceptionEvent("Player with the specified nickname is already assigned to a game"));
                return;
            }
        }
        getGameController(gameId).orElseThrow(() -> new IllegalGameIdException("Game with the specified ID does not exist"))
                .onPlayerConnection(nickname, totemColor);
        synchronized (dataLock) {
            assignPlayerToGame(nickname, gameId);
        }
    }
    /**
     * Routes structural totem deployment commands directly to the assigned match sub-controller unit.
     * <p>
     * Immediately triggers an isolated background serialization thread named {@code "Save-Thread-place"}
     * to log changes safely onto physical disks.
     * </p>
     *
     * @throws IllegalNicknameException if tracking maps indicate the target user has no active game link
     */
    public void placeTotem(String nickname, char position) throws IllegalNicknameException, WrongStateException, WrongTurnException, UnknownPositionException, IllegalPlacementException, UnknownNicknameException, SQLException, IllegalPickException, ForcedPickException {
        getGameController(getGameIdByNickname(nickname).orElseThrow(() -> new IllegalNicknameException("Player with the specified nickname is not in the lobby")))
                .orElseThrow(() -> new IllegalNicknameException("Player with the specified nickname is not assigned to any game"))
                .placeTotem(nickname, position);
        new Thread(() -> {
            synchronized (saveFileLock) {
                Codec parser = new JsonCodec();
                parser.writeSaveData(this, saveFilePath);
            }
        }, "Save-Thread-place").start();
    }
    /**
     * Routes structural card draft requests directly down to the assigned match sub-controller unit.
     * <p>
     * Immediately triggers an isolated background serialization thread named {@code "Save-Thread-pick"}
     * to keep system recovery snapshots up to date on disk.
     * </p>
     *
     * @throws IllegalNicknameException if tracking maps indicate the target user has no active game link
     */
    public void pickCard(String nickname, int cardId, int row) throws IllegalNicknameException, WrongStateException, WrongTurnException, UnknownPositionException, IllegalPickException, ForcedPickException, IllegalPlacementException, UnknownNicknameException, SQLException {
        getGameController(getGameIdByNickname(nickname).orElseThrow(() -> new IllegalNicknameException("Player with the specified nickname is not in the lobby")))
                .orElseThrow(() -> new IllegalNicknameException("Player with the specified nickname is not assigned to any game"))
                .pickCard(nickname, cardId, row);
        new Thread(() -> {
            synchronized (saveFileLock) {
                Codec parser = new JsonCodec();
                parser.writeSaveData(this, saveFilePath);
            }
        }, "Save-Thread-pick").start();
    }
    /**
     * Performs an immediate structural purge of a client session due to an explicit disconnect request
     * or a watchdog heartbeat timeout.
     * <p>
     * <b>Toleranza ai Guasti / Fault Isolation:</b> If the departing client was inside a match, the system
     * securely purges the entire associated {@code GameController} from current rosters. Active, healthy co-players
     * are gently rolled back into the global lobby space. This design approach enables unaffected users to
     * safely re-queue or establish new rooms without deadlocking the global server thread pool.
     * </p>
     *
     * @param nickname user signature profile token undergoing extraction
     */
    public void disconnectUser(String nickname) {
        synchronized (dataLock) {
            int gameId = getGameIdByNickname(nickname).orElse(-1);

            removePlayerFromLobby(nickname);
            removeLastPingReceivedTime(nickname);

            if (gameId >= 0) {
                // if user was in a game, remove the game controller.
                removeGameController(gameId);
                getPlayersByGameId(gameId).forEach(playerNickname -> {
                    if (isPlayerConnected(playerNickname)) {
                        //If player is connected returns to lobby in order to be able to reconnect to the game if he wants to
                        addPlayerToLobby(playerNickname);
                    } else {
                        // If player is not connected (because the other players were waiting for him after a server crash and quit before his join)
                        // remove him from the lobby so that he can use his name again
                        removePlayerFromLobby(playerNickname);
                    }
                });
            }
            networkLayer.update(new PlayerDisconnectedEvent(nickname, gameId));
        }
    }
    /**
     * Intercepts incoming client check-ins and refreshes the corresponding liveness tracking entry inside memory.
     *
     * @param nickname text signature identifier of the verifying node
     */
    public void handlePingRequest(String nickname) {
        synchronized (dataLock) {
            setLastPingReceivedTime(nickname);
        }
    }
    /**
     * Scheduled heartbeat verification routine executed at regular intervals by internal clock loops.
     * <p>
     * It computes time deltas between the current system epoch and individual entry logs inside
     * {@code lastPingReceivedTime}. Any node whose lag duration exceeds the defined {@code PING_TIMEOUT} threshold
     * is forwarded to {@link #disconnectUser(String)} to free up system resources.
     * </p>
     */
    public void handlePingTimeOut() {
        synchronized (dataLock) {
            long currentTime = System.currentTimeMillis();
            HashSet<String> nicknames = new HashSet<>(lastPingReceivedTime.keySet());
            for (String nickname : nicknames) {
                long lastPingTime = lastPingReceivedTime.get(nickname);
                if (currentTime - lastPingTime > PING_TIMEOUT) {
                    disconnectUser(nickname);
                }
            }
        }
    }
    /**
     * Iterates through active sub-controller maps to link the updated communication layer
     * following a global network reset or deployment rebuild.
     *
     * @param networkLayer current active network transport dispatcher layer entity
     */
    public void registerObserversAfterReconnection(NetworkLayer networkLayer) {
        for (GameController gc: gameIdToGameController.values()) {
            gc.getGame().addObserver(networkLayer);
        }
    }
}