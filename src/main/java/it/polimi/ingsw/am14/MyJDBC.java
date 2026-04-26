package it.polimi.ingsw.am14;

import java.sql.*;
import java.util.ArrayList;

//we are going to add a tuple for each player after each game saving the results
//after this we will show to the client the ranking based on a group by nickname and the sum of the final score
// ordered by the final score in descending order

public class MyJDBC {
    private static final String url = "jdbc:mysql://localhost:3306/mesos_am_14";
    private static final String username = "root";
    private static final String password = "mesos14";

    public static void saveToDB(String nickname, int finalScore, int numPlayers) throws SQLException {
        String query = "INSERT INTO mesos_db (nickname, final_score, date, num_players) " +
                       "VALUES (?, ?, ?, ?)";
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(query);

        ps.setString(1, nickname);
        ps.setInt(2, finalScore);
        ps.setDate(3, new Date(System.currentTimeMillis()));
        ps.setInt(4, numPlayers);

        ps.executeUpdate();

    }
    public static ArrayList<String> readRanking(int numPlayers) throws SQLException {
        String query = "SELECT nickname, SUM(final_score) AS total_score " +
                       "FROM mesos_db " +
                       "WHERE num_players = ? " +
                       "GROUP BY nickname " +
                       "ORDER BY total_score DESC ";

        ArrayList<String> rank = new ArrayList<>();

        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, numPlayers);
        ResultSet rs = ps.executeQuery();

        int place = 1;
        while (rs.next()) {
            String tuple = place + ") " +
                           rs.getString("nickname") +
                           " - " +
                           rs.getInt("total_score") + " punti ";
            rank.add(tuple);
            place++;
        }
        return rank;
    }
}

