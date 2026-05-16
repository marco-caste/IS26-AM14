package it.polimi.ingsw.am14.utils;

import java.util.List;
import java.util.Map;

/**
 * Contains all shared constants between client and server, such as ping timeout, message types, etc.
 */
public abstract class Constants {
    public static final int PING_TIMEOUT = 30000; // TODO 15 seconds
    public static final int PING_INTERVAL = 15000; // TODO 5 seconds
    public static final int TOP_DECKABLE_ROW = 0;
    public static final int TOP_BUILDING_ROW = 1;
    public static final int BOTTOM_DECKABLE_ROW = 2;
    public static final int BOTTOM_BUILDING_ROW = 3;
    public static final int[] initialFood = {2, 3, 3, 4, 4};
    public static final int[][] initialBuildings = {
            {1, 2, 3}, {2, 2, 4}, {2, 3, 4}, {2, 3, 5}
    };
    public static final Map<Integer, List<Integer>> foodTable = Map.of(
            2, List.of(1, -1),
            3, List.of(2, 0, -1),
            4, List.of(2, 1, 0, -1),
            5, List.of(3, 1, 0, 0, -1)
    );
    public static final String serverName = "AM14MesosServer";
    public static final String DBurl = "jdbc:mysql://localhost:3306/mesos_am_14";
    public static final String DBusername = "root";
    public static final String DBpassword = "mesos14";
    public static final String[] TotemColors = {"white", "purple", "yellow", "red", "blue"};
}
