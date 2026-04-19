package it.polimi.ingsw.am14.socket.server;
import it.polimi.ingsw.am14.controller.LobbyController;
import it.polimi.ingsw.am14.events.ClientRequest;
import it.polimi.ingsw.am14.events.Event;
import it.polimi.ingsw.am14.events.ExceptionEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class SocketClientHandler implements VirtualClientSocket{
    final ClientHandlersManager server;
    final BufferedReader input;
    final PrintWriter output;

    public SocketClientHandler(ClientHandlersManager server, BufferedReader input, PrintWriter output) {
        this.server = server;
        this.input = input;
        this.output = output;
    }

    public void runVirtualView() throws IOException {
        String line;

        while ((line = input.readLine()) != null){
            ClientRequest request = jsonWriter.writeAsRequest(line);
            this.server.receiveRequest(request, this);
        }
    }

    @Override
    public void update(Event update){
         String updateString = jsonWriter.writeAsString(update);
         output.println(updateString);
         output.flush();
    }
    @Override
    public void reportError(ExceptionEvent error){
        String errorString = jsonWriter.writeAsString(error);
        output.println(errorString);
        output.flush();
    }
}
