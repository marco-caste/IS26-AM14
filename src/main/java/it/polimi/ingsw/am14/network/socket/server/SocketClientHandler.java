package it.polimi.ingsw.am14.network.socket.server;
import it.polimi.ingsw.am14.events.ClientRequest;
import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;
import it.polimi.ingsw.am14.utils.JsonCodec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class SocketClientHandler implements VirtualClientSocket{
    final SocketClientHandlersManager server;
    final BufferedReader input;
    final PrintWriter output;
    private final JsonCodec jsonCodec = new JsonCodec();

    public SocketClientHandler(SocketClientHandlersManager server, BufferedReader input, PrintWriter output) {
        this.server = server;
        this.input = input;
        this.output = output;
    }

    public void runVirtualView() throws IOException {
        String line;

        while ((line = input.readLine()) != null){
            ClientRequest request = jsonCodec.parseClientRequest(line);
            this.server.receiveRequest(request, this);
        }
    }

    @Override
    public void update(Event update){
        String updateString = jsonCodec.writeEvent(update);
        output.println(updateString);
        output.flush();
    }
    @Override
    public void reportError(ExceptionEvent error){
        String errorString = jsonCodec.writeEvent(error);
        output.println(errorString);
        output.flush();
    }

    @Override
    public void pingClient() {

    }
}
