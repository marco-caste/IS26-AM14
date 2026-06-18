package it.polimi.ingsw.am14.network.socket.server;
import it.polimi.ingsw.am14.network.messages.requests.ClientRequest;
import it.polimi.ingsw.am14.network.messages.events.Event;
import it.polimi.ingsw.am14.network.messages.events.ExceptionEvent;
import it.polimi.ingsw.am14.utils.Codec;
import it.polimi.ingsw.am14.utils.JsonCodec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClientHandler implements VirtualClientSocket{
    private final SocketClientHandlersManager server;
    private final BufferedReader input;
    private final PrintWriter output;
    private final Socket socket;
    private final Codec jsonCodec;
    private BufferedReader pingInput;
    private PrintWriter pingOutput;
    private Socket pingSocket;
    private String nickname;

    public SocketClientHandler(SocketClientHandlersManager server, BufferedReader input, PrintWriter output, Socket socket) {
        this.server = server;
        this.input = input;
        this.output = output;
        this.socket = socket;
        this.jsonCodec = new JsonCodec();
    }

    public void runVirtualView() throws IOException {
        String line;

        while ((line = input.readLine()) != null) {
            if (socket.isClosed()) {
                throw new IOException("Socket closed while reading");
            }
            
            ClientRequest request = jsonCodec.parseClientRequest(line);
            this.server.receiveRequest(request, this);
        }
    }

    public void setPingSocket(BufferedReader pingInput, PrintWriter pingOutput, Socket pingSocket, String nickname) {
        this.pingInput = pingInput;
        this.pingOutput = pingOutput;
        this.pingSocket = pingSocket;
        this.nickname = nickname;
    }

    public void runPingServer() throws IOException {
        //our ping consists in sending the client's name to the server
        while (pingInput.readLine() != null) {
            if (pingSocket.isClosed()) {
                throw new IOException("Socket closed while reading");
            }
            this.server.pingServer(nickname);
        }
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public void update(Event event){
        if (!socket.isClosed()) {
            String eventString = jsonCodec.writeEvent(event);
            output.println(eventString);
            output.flush();
        }
    }
    @Override
    public void reportError(ExceptionEvent error){
        if (!socket.isClosed()) {
            String errorString = jsonCodec.writeEvent(error);
            output.println(errorString);
            output.flush();
        }
    }

    @Override
    public void pingClient() {
        if (pingSocket != null && !pingSocket.isClosed()) {
            pingOutput.println("ping");
            pingOutput.flush();
        }
    }

    public void closeConnection() {
        try {
            input.close();
        } catch (Exception e) {
        }
        try {
            output.close();
        } catch (Exception e) {
        }
        try {
            if (pingInput != null) {
                pingInput.close();
            }
        } catch (Exception e) {
        }
        try {
            if (pingOutput != null) {
                pingOutput.close();
            }
        } catch (Exception e) {
        }
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
        }
        try {
            if (pingSocket != null && !pingSocket.isClosed()) {
                pingSocket.close();
            }
        } catch (IOException e) {
        }
    }
}
