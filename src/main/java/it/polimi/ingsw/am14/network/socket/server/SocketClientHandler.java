package it.polimi.ingsw.am14.network.socket.server;
import it.polimi.ingsw.am14.events.ClientRequest;
import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;
import it.polimi.ingsw.am14.events.PingServerEvent;
import it.polimi.ingsw.am14.utils.JsonCodec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClientHandler implements VirtualClientSocket{
    final SocketClientHandlersManager server;
    final BufferedReader input;
    final PrintWriter output;
    final Socket socket;
    private final JsonCodec jsonCodec = new JsonCodec();
    public SocketClientHandler(SocketClientHandlersManager server, BufferedReader input, PrintWriter output, Socket socket) {
        this.server = server;
        this.input = input;
        this.output = output;
        this.socket = socket;
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

    @Override
    public void update(Event update){
        if (!socket.isClosed()) {
            String updateString = jsonCodec.writeEvent(update);
            output.println(updateString);
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
        if (!socket.isClosed()) {
            String pingString = jsonCodec.writeEvent(new PingServerEvent("ping"));
            output.println(pingString);
            output.flush();
        }
    }

    public void closeConnection() {
        try {
            if (!input.readLine().isEmpty()) {
                input.close();
            }
        } catch (Exception e) {
            // Ignora, il socket potrebbe essere già chiuso
        }
        try {
            output.close();
        } catch (Exception e) {
            // Ignora
        }
        try {
            if (!socket.isClosed()) {
                socket.close();
                // TODO: socket = null?
            }
        } catch (IOException e) {
            System.err.println("[Server] Error closing socket: " + e.getMessage());
        }
    }
}
