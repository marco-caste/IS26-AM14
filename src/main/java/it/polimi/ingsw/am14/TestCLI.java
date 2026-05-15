package it.polimi.ingsw.am14;

import it.polimi.ingsw.am14.network.messages.events.Event;
import it.polimi.ingsw.am14.network.socket.client.SocketServerHandler;
import it.polimi.ingsw.am14.utils.JsonCodec;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TestCLI {

    public static void main(String[] args) throws Exception {
        String host = (args.length > 0) ? args[0] : "localhost";
        int port = (args.length > 1) ? Integer.parseInt(args[1]) : 12345;

        Socket socket = new Socket(host, port);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        SocketServerHandler server = new SocketServerHandler(out);
        JsonCodec codec = new JsonCodec();

        // Thread ricezione eventi dal server
        Thread listener = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    Event e = codec.parseEvent(line);
                    System.out.println("[EVENT] " + e.getDescription());
                    // opzionale: e.effect(modelDiTest);
                }
            } catch (Exception ex) {
                System.err.println("[LISTENER STOP] " + ex.getMessage());
            }
        });
        listener.setDaemon(true);
        listener.start();

        // CLI manuale
        Scanner sc = new Scanner(System.in);
        System.out.println("""
                Comandi:
                connect <nick>
                create <nick> <numPlayers> <totemColor>
                join <nick> <gameId> <totemColor>
                place <nick> <positionChar>
                pick <nick> <cardId> <row>
                quit
                """);

        while (true) {
            System.out.print("> ");
            String line = sc.nextLine();//.trim();
            if (line.isEmpty()) continue;
            if (line.equalsIgnoreCase("quit")) break;

            try {
                String[] p = line.split("\\s+");
                switch (p[0].toLowerCase()) {
                    case "connect" -> server.connectUser(p[1]);
                    case "create"  -> server.createGame(p[1], Integer.parseInt(p[2]), p[3]);
                    case "join"    -> server.joinGame(p[1], Integer.parseInt(p[2]), p[3]);
                    case "place"   -> server.placeTotem(p[1], p[2].charAt(0));
                    case "pick"    -> server.pickCard(p[1], Integer.parseInt(p[2]), Integer.parseInt(p[3]));
                    default -> System.out.println("Comando non valido");
                }
            } catch (Exception ex) {
                System.out.println("Uso comando errato: " + ex.getMessage());
            }
        }

        socket.close();
        System.out.println("Client test chiuso.");
    }
}