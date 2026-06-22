# Run the application

The application is distributed as a single JAR file. It is provided with a dispatcher that can run both the server and client components. You can specify which component to run using command-line arguments.

## Run the Server

Running the server requires to specify, in order:
* `server` to indicate that you want to run the server component.
* `<SocketPort>` to specify the port number on which the Socket server will listen for incoming connections.
* `<RMIRegistryPort>` to specify the port number on which the RMI registry will listen for incoming connections. It must necessarily be different from the Socket port by 2: for example, if the Socket port is 5001, the RMI registry port must be 5003.
* `<SavePath>` to specify folder in which you want your server to save snapshots. If the path contains spaces, you need to wrap it in quotes.

Example command to run the server:

```bash
java -jar target/am14-1.0-SNAPSHOT.jar server 5001 5003 "/path/to/save/snapshots"
```

## Run the Client

Running the client requires to specify:
* `client` to indicate that you want to run the client component.
* `[cli|tui]` to specify the interface type.

Furthermore, to avoid the JLine dependency not working properly, you need to run the client in a terminal that supports ANSI escape codes. If you are using an IDE, make sure to enable ANSI support in the terminal settings.
The flag `--enable-native-access=ALL-UNNAMED` for the java command is also required.

Example command to run the client with TUI interface:

```bash
java -jar --enable-native-access=ALL-UNNAMED target/am14-1.0-SNAPSHOT.jar client cli
```

Example command to run the client with the GUI interface:
```bash
java -jar --enable-native-access=ALL-UNNAMED target/am14-1.0-SNAPSHOT.jar client gui
```

## Execution Order

There is no necessary execution order for the server and client, but the server must be running before the client can connect. You can start the server first, and then run the client to establish a connection.
