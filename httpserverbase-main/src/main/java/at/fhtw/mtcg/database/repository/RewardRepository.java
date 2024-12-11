package at.fhtw.mtcg.database.repository;

import at.fhtw.mtcg.database.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RewardRepository {
    private UnitOfWork unitOfWork;

    public RewardRepository(UnitOfWork unitOfWork)
    {
        this.unitOfWork = unitOfWork;
    }

    public int getmmr(String username) {
        UserRepository userRepository = new UserRepository(new UnitOfWork());
        int userId = userRepository.getUserID(username);
        String query = "SELECT * FROM \"user\" WHERE \"fk_user_id\" = ?";

        try (PreparedStatement pstmt = unitOfWork.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet response = pstmt.executeQuery();
            if (response.next()){
                int mmr = response.getInt("mmr");
                return mmr;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public int getUserCoins(String username) {
        UserRepository userRepository = new UserRepository(new UnitOfWork());
        int userId = userRepository.getUserID(username);
        String query = "SELECT * FROM \"user\" WHERE \"fk_user_id\" = ?";

        try (PreparedStatement pstmt = unitOfWork.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet response = pstmt.executeQuery();
            if (response.next()){
                int coins = response.getInt("coins");
                return coins;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }


    public boolean getReward(int currentCoins, String username) {
        try{
            UserRepository userRepository = new UserRepository(new UnitOfWork());
            int userId = userRepository.getUserID(username);

            int should_coins = currentCoins + 10;
            String updateQuery = "Update \"user\" set coins = ? where fk_user_id = ? ";
            PreparedStatement preparedStatement = unitOfWork.prepareStatement(updateQuery);
            preparedStatement.setInt(1, should_coins);
            preparedStatement.setInt(2, userId);
            unitOfWork.registerNew(preparedStatement);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
