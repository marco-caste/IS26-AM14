package it.polimi.ingsw.am14.controller;

import it.polimi.ingsw.am14.NetworkLayer;
import it.polimi.ingsw.am14.exceptions.*;
import it.polimi.ingsw.am14.model.gameLogic.Game;
import it.polimi.ingsw.am14.model.gameLogic.GameFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class LobbyController {
    private final Map<Integer, GameController> gameIdToGameController;
    private final Map<String, Integer> nicknameToGameId;
    private final Set<String> activeNicknames;
    private final NetworkLayer networkLayer;
    //TODO: quando andremo a salvare questi dati salveremo solo nicknameToGameId,
    //      mentre gli altri no, perchè i nickname non saranno attivi e i controller
    //      si ricreano solo se si riconnettono gli user della partita crashata

    public LobbyController(NetworkLayer networkLayer) {
        this.gameIdToGameController = new HashMap<>();
        this.nicknameToGameId = new HashMap<>();
        this.activeNicknames = new HashSet<>();
        this.networkLayer = networkLayer;
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
        for (Map.Entry<String, Integer> entry : nicknameToGameId.entrySet()) {
            if (entry.getValue() != null && entry.getValue() == gameId) {
                players.add(entry.getKey());
            }
        }
        return players;
    }

    public void connectUser(String nickname) throws IllegalNicknameException, IllegalGameIdException, WrongStateException {
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
                //update(PlayerReconnectedEvent(X));
                int gameId = getGameIdByNickname(nickname).orElseThrow();
//                if (!hasGameAController(gameId)) {
//                    //First person to connect to this game after the crash, need to set it up again
//                    //TODO: nuovo metodo di setup in GameFactory che si basa sul gameId?
//                    //Game game = GameFactory.setup(gameId);
//                    GameController gameController = new GameController(game);
//                    addGameController(game.getGameId(), gameController);
//                }
                //TODO: nuovo stato di WaitingRoomAfterCrash in Game onPlayerConncetion?
                //joinGame(nickname, gameId, null);
            }
            else {
                //Player is connecting for the first time, add to lobby
                addPlayerToLobby(nickname);

                //update(PlayerConnectedEvent(X));
            }
            // TODO: LOGIN SUCCESFUL EVENT
        }
    }

    public void createGame(String nickname, int numPlayers, String totemColor) throws IllegalSetupException, WrongStateException {
        Game game = GameFactory.setup(numPlayers);
        game.addObserver(networkLayer);
        GameController gameController = new GameController(game);
        addGameController(game.getGameId(), gameController);
        assignPlayerToGame(nickname, game.getGameId());
        gameController.onPlayerConnection(nickname, totemColor);
    }

    public void joinGame(String nickname, int gameId, String totemColor) throws WrongStateException, IllegalGameIdException {
        getGameController(gameId).orElseThrow(() -> new IllegalGameIdException("Game with the specified ID does not exist"))
                .onPlayerConnection(nickname, totemColor);
        assignPlayerToGame(nickname, gameId);
    }

    public void placeTotem(String nickname, char position) throws IllegalNicknameException, WrongStateException, WrongTurnException, UnknownPositionException, IllegalPlacementException, UnknownNicknameException {
        getGameController(getGameIdByNickname(nickname).orElseThrow(() -> new IllegalNicknameException("Player with the specified nickname is not in the lobby")))
                .orElseThrow(() -> new IllegalNicknameException("Player with the specified nickname is not assigned to any game"))
                .placeTotem(nickname, position);
    }

    public void pickCard(String nickname, int cardId, int row) throws IllegalNicknameException, WrongStateException, WrongTurnException, UnknownPositionException, IllegalPickException, ForcedPickException, IllegalPlacementException, UnknownNicknameException {
        getGameController(getGameIdByNickname(nickname).orElseThrow(() -> new IllegalNicknameException("Player with the specified nickname is not in the lobby")))
                .orElseThrow(() -> new IllegalNicknameException("Player with the specified nickname is not assigned to any game"))
                .pickCard(nickname, cardId, row);
    }

    public void disconnectUser(String nickname) {
        if (hasPlayerAGameAssigned(nickname)) {
            int gameId = getGameIdByNickname(nickname).orElseThrow();
            removeGameController(gameId);
            deactivatePlayer(nickname);
            removePlayerFromLobby(nickname);
            getPlayersByGameId(gameId).forEach(this::addPlayerToLobby);
            // TODO: TERMINATES THE THREAD THAT FLUSHES THE UPDATE QUEUE TO THIS CLIENT.
            //update(PlayerDisconnectedEvent(X));
            return;
        }
        removePlayerFromLobby(nickname);
    }
}