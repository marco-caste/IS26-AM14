package it.polimi.ingsw.am14.utils;

import java.sql.*;
import java.util.ArrayList;

import static it.polimi.ingsw.am14.utils.Constants.*;

//we are going to add a tuple for each player after each game saving the results
//after this we will show to the client the ranking based on a group by nickname and the sum of the final score
// ordered by the final score in descending order


/**
 * Utility class for interacting with the JDBC database.
 * Manages saving results for each player at the end of a game
 * and reading rankings grouped by player.
 */

public class MyJDBC {

    /**
     * Saves a player's final results for a specific game into the database.
     * The `date` and `time` fields are generated automatically upon insertion.
     *
     * @param game_id    the unique identifier of the game.
     * @param nickname   the name of the player.
     * @param finalScore the final score achieved by the player.
     * @param numPlayers the number of players who participated in the game.
     * @throws SQLException if a database access error or SQL syntax error occurs.
     */

    public static void saveToDB(int game_id, String nickname, int finalScore, int numPlayers) throws SQLException {
        String query = "INSERT INTO mesos_db (game_id, nickname, final_score, date, num_players, time) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        Connection connection = DriverManager.getConnection(DBurl, DBusername, DBpassword);
        PreparedStatement ps = connection.prepareStatement(query);

        ps.setInt(1, game_id);
        ps.setString(2, nickname);
        ps.setInt(3, finalScore);
        ps.setDate(4, new Date(System.currentTimeMillis()));
        ps.setInt(5, numPlayers);
        ps.setTime(6, new Time(System.currentTimeMillis()));

        ps.executeUpdate();

    }

    /**
     * Reads the aggregated player ranking from the database, filtered by the number of players in the game.
     * The ranking shows the sum of the final score (`total_score`) grouped by `nickname`,
     * ordered in descending order.
     *
     * @param numPlayers the number of players in the games you want to retrieve the ranking for.
     * @return an `ArrayList` of strings containing the formatted ranking positions.
     * @throws SQLException if a database access error occurs.
     */

    public static ArrayList<String> readRanking(int numPlayers) throws SQLException {
        String query = "SELECT nickname, SUM(final_score) AS total_score " +
                "FROM mesos_db " +
                "WHERE num_players = ? " +
                "GROUP BY nickname " +
                "ORDER BY total_score DESC ";

        ArrayList<String> rank = new ArrayList<>();

        Connection connection = DriverManager.getConnection(DBurl, DBusername, DBpassword);
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, numPlayers);
        ResultSet rs = ps.executeQuery();

        int place = 1;
        while (rs.next()) {
            String tuple = place + ") " +
                    rs.getString("nickname") +
                    "   " +
                    rs.getInt("total_score") + " points ";
            rank.add(tuple);
            place++;
        }
        return rank;
    }
}