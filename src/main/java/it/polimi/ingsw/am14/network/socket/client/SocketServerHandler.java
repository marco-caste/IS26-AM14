package it.polimi.ingsw.am14.network.socket.client;

import it.polimi.ingsw.am14.network.messages.requests.*;
import it.polimi.ingsw.am14.utils.JsonCodec;

import java.io.BufferedWriter;
import java.io.PrintWriter;

import static it.polimi.ingsw.am14.utils.Constants.PING_INTERVAL;

public class SocketServerHandler implements VirtualServerSocket{

    private final PrintWriter output;
    private PrintWriter pingOutput;
    private final JsonCodec jsonWriter = new JsonCodec();
    private final SocketClient client;

    public SocketServerHandler(BufferedWriter output, SocketClient client) {
        this.output = new PrintWriter(output);
        this.client = client;
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

    @Override
    public Thread pingServer(String nickname) {
        BufferedWriter output = client.setPingSocket();
        if (output!=null) {
            pingOutput = new PrintWriter(output);
            Thread thread = new Thread(() -> {
                try {
                    while (true) {
                        Thread.sleep(PING_INTERVAL);

                        PingClientRequest request = new PingClientRequest(nickname);
                        String message = jsonWriter.writeClientRequest(request);
                        synchronized (output) {
                            pingOutput.println(message);
                            pingOutput.flush();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "SocketClient-pinger-" + nickname);
            thread.start();
            return thread;
        }
        return null;
    }
}
