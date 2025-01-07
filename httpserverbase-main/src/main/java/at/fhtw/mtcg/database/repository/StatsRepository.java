package at.fhtw.mtcg.database.repository;

import at.fhtw.mtcg.models.UserStats;
import at.fhtw.mtcg.database.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StatsRepository {
    private final UnitOfWork unitOfWork;

    public StatsRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    // Gets the stats of a user
    public UserStats getStats(String username) {
        try {
            UserRepository userRepository = new UserRepository(new UnitOfWork());
            int userID = userRepository.getUserID(username);

            String query = "SELECT * FROM \"user\" WHERE fk_user_id = ?";
            try (PreparedStatement stmt = unitOfWork.prepareStatement(query)) {
                stmt.setInt(1, userID);
                ResultSet resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    return extractUserStatsFromResultSet(resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Helper method to extract UserStats from the ResultSet
    private UserStats extractUserStatsFromResultSet(ResultSet resultSet) throws Exception {
        UserStats userStats = new UserStats();
        userStats.setName(resultSet.getString("in_game_name"));
        userStats.setCoins(resultSet.getInt("coins"));
        userStats.setMmr(resultSet.getInt("mmr"));
        userStats.setLosses(resultSet.getInt("losses"));
        userStats.setWins(resultSet.getInt("wins"));
        return userStats;
    }
}
