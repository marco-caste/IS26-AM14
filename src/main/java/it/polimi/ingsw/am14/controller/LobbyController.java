package it.polimi.ingsw.am14.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.network.messages.events.LoginSuccesfulEvent;
import it.polimi.ingsw.am14.network.messages.events.PlayerDisconnectedEvent;
import it.polimi.ingsw.am14.network.NetworkLayer;
import it.polimi.ingsw.am14.exceptions.*;
import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.GameFactory;
import it.polimi.ingsw.am14.utils.JsonCodec;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static it.polimi.ingsw.am14.utils.Constants.PING_TIMEOUT;

public class LobbyController {
    // TODO: USATO SOLO NEI TEST
    public Map<Integer, GameController> getGameIdToGameController() {
        return gameIdToGameController;
    }

    private final Map<Integer, GameController> gameIdToGameController;
    private final Map<String, Integer> nicknameToGameId;
    private int gameIdCounter;
    @JsonIgnore
    private NetworkLayer networkLayer;        // TODO: CHECK SINCRONIZZAZIONE
    @JsonIgnore
    private final Map<String, Long> lastPingReceivedTime;
    @JsonIgnore
    private final Object dataLock = new Object();
    @JsonIgnore
    private final String saveFilePath = "src/main/Saves/save.json";

    public LobbyController(NetworkLayer networkLayer) {
        this.gameIdToGameController = new HashMap<>();
        this.nicknameToGameId = new HashMap<>();
        this.networkLayer = networkLayer;
        this.lastPingReceivedTime = new HashMap<>();
        this.gameIdCounter = 0;
    }

    @JsonCreator
    public LobbyController(
            @JsonProperty("nicknameToGameId") Map<String, Integer> nicknameToGameId,
            @JsonProperty("gameIdToGameController") Map<Integer, GameController> gameIdToGameController,
            @JsonProperty("gameIdCounter") int gameIdCounter) {
        this.gameIdToGameController = gameIdToGameController;
        this.nicknameToGameId = nicknameToGameId;
        this.networkLayer = new NetworkLayer();
        this.lastPingReceivedTime = new HashMap<>();
        this.gameIdCounter = gameIdCounter;
    }

    private void addGameController(int gameId, GameController gameController) {
        gameIdToGameController.put(gameId, gameController);
    }

    private void removeGameController(int gameId) {
        gameIdToGameController.remove(gameId);
    }

    private Optional<GameController> getGameController(int gameId) {
        return Optional.ofNullable(gameIdToGameController.get(gameId));
    }

    private void assignPlayerToGame(String nickname, int gameId) {
        nicknameToGameId.put(nickname, gameId);
    }

    private void removePlayerFromLobby(String nickname) {
        nicknameToGameId.remove(nickname);
    }

    public Optional<Integer> getGameIdByNickname(String nickname) {
        synchronized (dataLock) {
            return Optional.ofNullable(nicknameToGameId.get(nickname));
        }
    }

    private void addPlayerToLobby(String nickname) {
        nicknameToGameId.put(nickname, null);
    }

    private boolean hasPlayerAGameAssigned(String nickname) {
        return nicknameToGameId.containsKey(nickname) && nicknameToGameId.get(nickname) != null;
    }

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

    private void setLastPingReceivedTime(String nickname) {
        synchronized (dataLock) {
            lastPingReceivedTime.put(nickname, System.currentTimeMillis());
        }
    }

    public void setNetworkLayer(NetworkLayer networkLayer) {
        this.networkLayer = networkLayer;
    }

    private void removeLastPingReceivedTime(String nickname) {
        lastPingReceivedTime.remove(nickname);
    }

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
        if (gameId >=0) {
            this.joinGame(nickname, gameId, null);
        }
        else {
            networkLayer.update(new LoginSuccesfulEvent(nickname));
        }
        setLastPingReceivedTime(nickname);
    }

    public void createGame(String nickname, int numPlayers, String totemColor) throws IllegalSetupException, WrongStateException, IllegalNicknameException {
        synchronized (dataLock) {
            Game game = GameFactory.setup(numPlayers, gameIdCounter);
            game.addObserver(networkLayer);
            GameController gameController = new GameController(game);
            addGameController(game.getGameId(), gameController);
            assignPlayerToGame(nickname, game.getGameId());
            gameIdCounter++;
            gameController.onPlayerConnection(nickname, totemColor);
        }
        new Thread(() -> {
            synchronized (saveFilePath) {
                JsonCodec parser = new JsonCodec();
                parser.writeSaveData(this, saveFilePath);
            }
        }, "Save-Thread-pick").start();
    }

    public void joinGame(String nickname, int gameId, String totemColor) throws WrongStateException, IllegalGameIdException {
        getGameController(gameId).orElseThrow(() -> new IllegalGameIdException("Game with the specified ID does not exist"))
                .onPlayerConnection(nickname, totemColor);
        synchronized (dataLock) {
            assignPlayerToGame(nickname, gameId);
        }
    }

    public void placeTotem(String nickname, char position) throws IllegalNicknameException, WrongStateException, WrongTurnException, UnknownPositionException, IllegalPlacementException, UnknownNicknameException {
        getGameController(getGameIdByNickname(nickname).orElseThrow(() -> new IllegalNicknameException("Player with the specified nickname is not in the lobby")))
                .orElseThrow(() -> new IllegalNicknameException("Player with the specified nickname is not assigned to any game"))
                .placeTotem(nickname, position);
        new Thread(() -> {
            synchronized (saveFilePath) {
                JsonCodec parser = new JsonCodec();
                parser.writeSaveData(this, saveFilePath);
            }
        }, "Save-Thread-place").start();
    }

    public void pickCard(String nickname, int cardId, int row) throws IllegalNicknameException, WrongStateException, WrongTurnException, UnknownPositionException, IllegalPickException, ForcedPickException, IllegalPlacementException, UnknownNicknameException, SQLException {
        getGameController(getGameIdByNickname(nickname).orElseThrow(() -> new IllegalNicknameException("Player with the specified nickname is not in the lobby")))
                .orElseThrow(() -> new IllegalNicknameException("Player with the specified nickname is not assigned to any game"))
                .pickCard(nickname, cardId, row);
        new Thread(() -> {
            synchronized (saveFilePath) {
                JsonCodec parser = new JsonCodec();
                parser.writeSaveData(this, saveFilePath);
            }
        }, "Save-Thread-pick").start();
    }

    public void disconnectUser(String nickname) {
        synchronized (dataLock) {
            int gameId = getGameIdByNickname(nickname).orElse(-1);

            removePlayerFromLobby(nickname);
            removeLastPingReceivedTime(nickname);

            if (gameId >= 0) {
                // if user was in a game, remove the game controller.
                removeGameController(gameId);
                getPlayersByGameId(gameId).forEach(this::addPlayerToLobby);
            }
            networkLayer.update(new PlayerDisconnectedEvent(nickname, gameId));
        }
    }

    public void handlePingRequest(String nickname) {
        synchronized (dataLock) {
            setLastPingReceivedTime(nickname);
        }
    }

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

    public void registerObserversAfterReconnection(NetworkLayer networkLayer) {
        for (GameController gc: gameIdToGameController.values()) {
            gc.getGame().addObserver(networkLayer);
        }
    }
}