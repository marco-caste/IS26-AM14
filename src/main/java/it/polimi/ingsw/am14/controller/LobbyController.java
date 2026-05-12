package it.polimi.ingsw.am14.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mysql.cj.xdevapi.JsonParser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import it.polimi.ingsw.am14.events.PlayerDisconnectedEvent;
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
    private final Set<String> activeNicknames; //TODO: Da rimuovere se si tiene così l'update in Network layer
    @JsonIgnore
    private final NetworkLayer networkLayer;
    @JsonIgnore
    private final Map<String, Long> lastPingReceivedTime;
    @JsonIgnore
    private final Object dataLock = new Object();

    public LobbyController(NetworkLayer networkLayer) {
        this.gameIdToGameController = new HashMap<>();
        this.nicknameToGameId = new HashMap<>();
        this.activeNicknames = new HashSet<>();
        this.networkLayer = networkLayer;
        this.lastPingReceivedTime = new HashMap<>();
    }

    @JsonCreator
    public LobbyController(Map<String, Integer> nicknameToGameId) {
        this.gameIdToGameController = new HashMap<>();
        this.nicknameToGameId = nicknameToGameId;
        this.activeNicknames = new HashSet<>();
        this.networkLayer = new NetworkLayer();
        this.lastPingReceivedTime = new HashMap<>();
    }

    private void addGameController(int gameId, GameController gameController) {
        gameIdToGameController.put(gameId, gameController);
    }

    private void removeGameController(int gameId) {
        gameIdToGameController.remove(gameId);
        getPlayersByGameId(gameId).forEach(activeNicknames::remove);
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

    private void activatePlayer(String nickname) {
        activeNicknames.add(nickname);
    }

    private void deactivatePlayer(String nickname) {
        activeNicknames.remove(nickname);
    }

    private boolean isPlayerActive(String nickname) {
        return activeNicknames.contains(nickname);
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

    public void connectUser(String nickname) throws IllegalNicknameException {
        // TODO: ANCHE SINCRONIZZARE
        if (isPlayerActive(nickname)) {
            throw new IllegalNicknameException("Nickname is already in use in an active game");
        }
        else {
            //Nickname available, but might have a game assigned if the user is reconnecting after a server crash
            activatePlayer(nickname);
            //TODO: chiedere a Lorenzo se c'è un unico update di connessione o un altro per la riconnessione
            if (hasPlayerAGameAssigned(nickname)) {
                //Player is reconnecting after a server crash
                activatePlayer(nickname);
                int gameId = getGameIdByNickname(nickname).orElseThrow();
//                if (!hasGameAController(gameId)) {
//                    //First person to connect to this game after the crash, need to set it up again
//                    //TODO: restoreState da src/main/saves/gameId.json
//                    //Game game = GameFactory.setup(gameId);
//                        game.setCurrentState(new WaitingRoomAfterCrash(game));
//                            game.addObserver(networkLayer);
//                    GameController gameController = new GameController(game);
//                    addGameController(game.getGameId(), gameController);
//                }
                //TODO: nuovo stato di WaitingRoomAfterCrash in Game onPlayerConncetion?
                //joinGame(nickname, gameId, null);
                //update(PlayerReconnectedEvent(X));
            }
            else {
                //Player is connecting for the first time, add to lobby
                addPlayerToLobby(nickname);

                //update(PlayerConnectedEvent(X));
            }
            // TODO: LOGIN SUCCESFUL EVENT
        }
    }

    public void createGame(String nickname, int numPlayers, String totemColor) throws IllegalSetupException, WrongStateException, IllegalNicknameException {
        synchronized (dataLock) {
            // synchronizing on dataLock allows the creation of one game at a time, which is necessary in order to keep
            // gameIDs unique.

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
        deactivatePlayer(nickname); //TODO: networkLayer
        removeLastPingReceivedTime(nickname);

        //Remove game if the user was in a game
        int gameId = getGameIdByNickname(nickname).orElse(-1);
        if (hasPlayerAGameAssigned(nickname)) {
            removeGameController(gameId);
            getPlayersByGameId(gameId).forEach(this::addPlayerToLobby);
        }
        networkLayer.update(new PlayerDisconnectedEvent(nickname, gameId));
    }

    // Called automatically after a certain timeout if server pings are not answered to TODO
    public void disconnectServer() {
        activeNicknames.clear();
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
                if (currentTime - lastPingTime <= PING_TIMEOUT) {
                    //One player has a valid ping, but server could have crashed after
                    this.disconnectUserThatAreSurelyDisconnected(currentTime);
                    return;
                }
            }
            //All players are disconnected
            this.disconnectServer();
        }
    }

    private void disconnectUserThatAreSurelyDisconnected(long currentTime) {
        for (String nickname : lastPingReceivedTime.keySet()) {
            long lastPingTime = lastPingReceivedTime.get(nickname);
            if (currentTime - lastPingTime > 2*PING_TIMEOUT) {
                //I disconnect a user when I'm sure that the server didn't crash meanwhile
                disconnectUser(nickname);
            }
        }
    }
}