package it.polimi.ingsw.am14.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mysql.cj.xdevapi.JsonParser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import it.polimi.ingsw.am14.events.LoginSuccesfulEvent;
import it.polimi.ingsw.am14.events.PlayerDisconnectedEvent;
import it.polimi.ingsw.am14.events.PlayerReconnectedEvent;
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
    public Map<Integer, GameController> getGameIdToGameController() {
        return gameIdToGameController;
    }

    private final Map<Integer, GameController> gameIdToGameController;
    private final Map<String, Integer> nicknameToGameId;
    @JsonIgnore
    private final NetworkLayer networkLayer;
    @JsonIgnore
    private final Map<String, Long> lastPingReceivedTime;
    @JsonIgnore
    private final Object dataLock = new Object();

    public LobbyController(NetworkLayer networkLayer) {
        this.gameIdToGameController = new HashMap<>();
        this.nicknameToGameId = new HashMap<>();
        this.networkLayer = networkLayer;
        this.lastPingReceivedTime = new HashMap<>();
    }

    @JsonCreator
    public LobbyController(
            @JsonProperty("nicknameToGameId") Map<String, Integer> nicknameToGameId,
            @JsonProperty("gameIdToGameController") Map<Integer, GameController> gameIdToGameController) {
        this.gameIdToGameController = gameIdToGameController;
        this.nicknameToGameId = nicknameToGameId;
        this.networkLayer = new NetworkLayer();
        this.lastPingReceivedTime = new HashMap<>();
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

    private boolean hasGameAController(int gameId) {
        return gameIdToGameController.containsKey(gameId) && getGameController(gameId).isPresent();
    }

    private void assignPlayerToGame(String nickname, int gameId) {
        nicknameToGameId.put(nickname, gameId);
    }

    private void removePlayerFromLobby(String nickname) {
        nicknameToGameId.remove(nickname);
    }

    public Optional<Integer> getGameIdByNickname(String nickname) {
        return Optional.ofNullable(nicknameToGameId.get(nickname));
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
        lastPingReceivedTime.put(nickname, System.currentTimeMillis());
    }

    private void removeLastPingReceivedTime(String nickname) {
        lastPingReceivedTime.remove(nickname);
    }

    public void connectUser(String nickname) throws IllegalNicknameException, IllegalGameIdException, WrongStateException {
        nickname = nickname.toLowerCase();
        int gameId;
        synchronized (dataLock) {
            if (hasPlayerAGameAssigned(nickname)) {
                gameId = getGameIdByNickname(nickname).orElseThrow();
            }
            else {
                addPlayerToLobby(nickname);
                // TODO: contollare ping
                gameId = -1;
            }
        }
        if (gameId >=0) {
            this.joinGame(nickname, gameId, null);
        }
        else {
            networkLayer.update(new LoginSuccesfulEvent(nickname));
        }
    }

    public void createGame(String nickname, int numPlayers, String totemColor) throws IllegalSetupException, WrongStateException, IllegalNicknameException {
        synchronized (dataLock) {
            Game game = GameFactory.setup(numPlayers);
            game.addObserver(networkLayer);
            GameController gameController = new GameController(game);
            addGameController(game.getGameId(), gameController);
            assignPlayerToGame(nickname, game.getGameId());
            gameController.onPlayerConnection(nickname, totemColor);
        }
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
        JsonCodec parser = new JsonCodec();
        parser.writeSaveData(this, "src/main/Saves/save.json");
    }

    public void pickCard(String nickname, int cardId, int row) throws IllegalNicknameException, WrongStateException, WrongTurnException, UnknownPositionException, IllegalPickException, ForcedPickException, IllegalPlacementException, UnknownNicknameException, SQLException {
        getGameController(getGameIdByNickname(nickname).orElseThrow(() -> new IllegalNicknameException("Player with the specified nickname is not in the lobby")))
                .orElseThrow(() -> new IllegalNicknameException("Player with the specified nickname is not assigned to any game"))
                .pickCard(nickname, cardId, row);
    }

    public void disconnectUser(String nickname) {
        //Remove player from controller
        removePlayerFromLobby(nickname);
        removeLastPingReceivedTime(nickname);

        //Remove game if the user was in a game
        int gameId = getGameIdByNickname(nickname).orElse(-1);
        if (hasPlayerAGameAssigned(nickname)) {
            removeGameController(gameId);
            getPlayersByGameId(gameId).forEach(this::addPlayerToLobby);
        }
        networkLayer.update(new PlayerDisconnectedEvent(nickname, gameId));
    }

    // Called automatically after a certain timeout if server pings are not answered to TODO: da rimuovere giusto?
    public void disconnectServer() {

    }

    public void handlePingRequest(String nickname) {
        synchronized (dataLock) {
            setLastPingReceivedTime(nickname);
        }
    }

    public void handlePingTimeOut() {
        synchronized (dataLock) {
            long currentTime = System.currentTimeMillis();
            for (String nickname : lastPingReceivedTime.keySet()) {
                long lastPingTime = lastPingReceivedTime.get(nickname);
                if (currentTime - lastPingTime > PING_TIMEOUT) {
                    disconnectUser(nickname);
                }
            }
            //All players are disconnected TODO: da rimuovere giusto?
            //this.disconnectServer();
        }
    }
}