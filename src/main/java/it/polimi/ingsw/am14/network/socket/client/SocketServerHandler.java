package it.polimi.ingsw.am14.network.socket.client;

import it.polimi.ingsw.am14.events.*;
import it.polimi.ingsw.am14.utils.JsonCodec;

import java.io.BufferedWriter;
import java.io.PrintWriter;

import static it.polimi.ingsw.am14.utils.Constants.PING_INTERVAL;

public class SocketServerHandler implements VirtualServerSocket{

    private final PrintWriter output;
    private final JsonCodec jsonWriter = new JsonCodec();

    public SocketServerHandler(BufferedWriter output) {
        this.output = new PrintWriter(output);
    }

    @Override
    public void connectUser(String nickname) {
        ConnectClientRequest request = new ConnectClientRequest(nickname);
        String message = jsonWriter.writeClientRequest(request);
        synchronized (output) {     // tecnicamente non serve, println e flush si sincronizzano su un lock interno
            output.println(message);
            output.flush();
        }
    }
    @Override
    public void createGame(String nickname, int numPlayers, String totemColor){
        CreateGameClientRequest request = new CreateGameClientRequest(nickname, numPlayers, totemColor);
        String message = jsonWriter.writeClientRequest(request);
        synchronized (output) {
            output.println(message);
            output.flush();
        }
    }
    @Override
    public void joinGame(String nickname, int gameId, String totemColor){
        JoinGameClientRequest request = new JoinGameClientRequest(nickname, totemColor, gameId);
        String message = jsonWriter.writeClientRequest(request);
        synchronized (output) {
            output.println(message);
            output.flush();
        }
    }
    @Override
    public void placeTotem(String nickname, char position){
        PlaceTotemClientRequest request = new PlaceTotemClientRequest(nickname, position);
        String message = jsonWriter.writeClientRequest(request);
        synchronized (output) {
            output.println(message);
            output.flush();
        }
    }
    @Override
    public void pickCard(String nickname, int cardId, int row){
        PickCardClientRequest request = new PickCardClientRequest(nickname, cardId, row);
        String message = jsonWriter.writeClientRequest(request);
        synchronized (output) {
            output.println(message);
            output.flush();
        }
    }


    public void pingServer(String nickname) {
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(PING_INTERVAL);

                    PingClientRequest request = new PingClientRequest(nickname);
                    String message = jsonWriter.writeClientRequest(request);
                    synchronized (output) {
                        output.println(message);
                        output.flush();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, nickname + "'s pinger client side").start();
    }
}
