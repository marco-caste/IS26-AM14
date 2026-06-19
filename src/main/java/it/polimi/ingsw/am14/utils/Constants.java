package it.polimi.ingsw.am14.utils;

import java.util.List;
import java.util.Map;

/**
 * Contains all shared constants between client and server, such as ping timeout, message types, etc.
 */
public abstract class Constants {
    /**
     * The time in milliseconds after which a client is considered disconnected if it hasn't sent a ping message.
     */
    public static final int PING_TIMEOUT = 10000;
    /**
     * The interval at which clients should send ping messages to the server to keep the connection alive.
     */
    public static final int PING_INTERVAL = 2000;
    /**
     * Constants representing the different rows in the game board. These constants are used to identify the position of deckable and building rows for both the top and bottom players.
     */
    public static final int TOP_DECKABLE_ROW = 0;
    public static final int TOP_BUILDING_ROW = 1;
    public static final int BOTTOM_DECKABLE_ROW = 2;
    public static final int BOTTOM_BUILDING_ROW = 3;
    /**
     * The initial food resources available to players at the start of the game. Value {@code initialFood[i]} is the food that the {@code i}-th player obtains.
     */
    public static final int[] initialFood = {2, 3, 3, 4, 4};
    /**
     * The initial buildings available to players at the start of the game.
     * Value {@code initialBuildings[i][j]} is the number of buildings to place on the board for age {@code j} in a game of {@code i + 2} players.
     */
    public static final int[][] initialBuildings = {
            {1, 2, 3}, {2, 2, 4}, {2, 3, 4}, {2, 3, 5}
    };
    /**
     * The food resources obtained by players when they place a building of a certain age.
     * List {@code foodTable.get(i)} contains the food obtained/lost by each player when placing back on the turnOrderCard after a turn has ended.
     */
    public static final Map<Integer, List<Integer>> foodTable = Map.of(
            2, List.of(1, -1),
            3, List.of(2, 0, -1),
            4, List.of(2, 1, 0, -1),
            5, List.of(3, 1, 0, 0, -1)
    );
    /**
     * The name of the server, used for RMI registry lookup.
     */
    public static final String serverName = "AM14MesosServer";
    /**
     * The URL, username, and password for connecting to the MySQL database.
     */
    public static final String DBurl = "jdbc:mysql://localhost:3306/mesos_am_14";
    public static final String DBusername = "root";
    public static final String DBpassword = "mesos14";
    /**
     * The String representation of totem colors available in the game.
     */
    public static final String[] TotemColors = {"white", "purple", "yellow", "red", "blue"};
}
