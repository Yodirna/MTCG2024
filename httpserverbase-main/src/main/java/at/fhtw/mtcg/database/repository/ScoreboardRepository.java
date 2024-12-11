package at.fhtw.mtcg.database.repository;

import at.fhtw.mtcg.models.UserStats;
import at.fhtw.mtcg.database.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

public class ScoreboardRepository {
    private UnitOfWork unitOfWork;

    public ScoreboardRepository(UnitOfWork unitOfWork)
    {
        this.unitOfWork = unitOfWork;
    }

    public Vector<UserStats> getStats(){
        try {

            String statsQuery = "Select * from \"user\" order by wins desc, mmr desc, losses asc";
            PreparedStatement statsStmt = unitOfWork.prepareStatement(statsQuery);

            Vector<UserStats> list2 = new Vector<UserStats>();
            ResultSet resultSet = statsStmt.executeQuery();
            while (resultSet.next()){
                UserStats userStats = new UserStats();

                int coins = resultSet.getInt("coins");
                int losses = resultSet.getInt("losses");
                int wins = resultSet.getInt("wins");
                int mmr = resultSet.getInt("mmr");
                String name = resultSet.getString("in_game_name");

                userStats.setName(name);
                userStats.setCoins(coins);
                userStats.setMmr(mmr);
                userStats.setLosses(losses);
                userStats.setWins(wins);
                list2.add(userStats);
            }
            return list2;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
