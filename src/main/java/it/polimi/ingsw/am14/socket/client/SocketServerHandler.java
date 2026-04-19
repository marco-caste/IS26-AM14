package it.polimi.ingsw.am14.socket.client;

import it.polimi.ingsw.am14.events.*;

import java.io.BufferedWriter;
import java.io.PrintWriter;

public class SocketServerHandler implements VirtualServerSocket{

    final PrintWriter output;

    public SocketServerHandler(BufferedWriter output) {
        this.output = new PrintWriter(output);
    }

    @Override
    public void connectUser(String nickname) {
        ConnectClientRequest request = new ConnectClientRequest(nickname);
        String message = jsonWriter.writeAsString(request);
        output.println(message);
        output.flush();
    }
    @Override
    public void createGame(String nickname, int numPlayers, String totemColor){
        CreateGameClientRequest request = new CreateGameClientRequest(nickname, numPlayers, totemColor);
        String message = jsonWriter.writeAsString(request);
        output.println(message);
        output.flush();
    }
    @Override
    public void joinGame(String nickname, int gameId, String totemColor){
        JoinGameClientRequest request = new JoinGameClientRequest(nickname, gameId, totemColor);
        String message = jsonWriter.writeAsString(request);
        output.println(message);
        output.flush();
    }
    @Override
    public void placeTotem(String nickname, char position){
        PlaceTotemClientRequest request = new PlaceTotemClientRequest(nickname, position);
        String message = jsonWriter.writeAsString(request);
        output.println(message);
        output.flush();
    }
    @Override
    public void pickCard(String nickname, int cardId, int row){
        PickCardClientRequest request = new PickCardClientRequest(nickname, cardId, row);
        String message = jsonWriter.writeAsString(request);
        output.println(message);
        output.flush();
    }
}
