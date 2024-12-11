package at.fhtw.mtcg.database.repository;

import at.fhtw.mtcg.models.UserStats;
import at.fhtw.mtcg.database.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StatsRepository {
    private UnitOfWork unitOfWork;

    public StatsRepository(UnitOfWork unitOfWork)
    {
        this.unitOfWork = unitOfWork;
    }


    // holt die stats aus der db
    public UserStats getStats(String username){
        try {
            UserRepository userRepository = new UserRepository(new UnitOfWork());
            int userID = userRepository.getUserID(username);

            String statsQuery = "Select * from \"user\" where fk_user_id = ?";
            PreparedStatement statsStmt = unitOfWork.prepareStatement(statsQuery);
            statsStmt.setInt(1, userID);
            ResultSet resultSet = statsStmt.executeQuery();
            if (resultSet.next()){
                int coins = resultSet.getInt("coins");
                int losses = resultSet.getInt("losses");
                int wins = resultSet.getInt("wins");
                int mmr = resultSet.getInt("mmr");
                String name = resultSet.getString("in_game_name");
                UserStats userStats = new UserStats();
                userStats.setName(name);
                userStats.setCoins(coins);
                userStats.setMmr(mmr);
                userStats.setLosses(losses);
                userStats.setWins(wins);
                return userStats;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
