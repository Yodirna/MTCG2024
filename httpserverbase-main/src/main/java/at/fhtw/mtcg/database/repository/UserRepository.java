package at.fhtw.mtcg.database.repository;

import at.fhtw.mtcg.database.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {
    private UnitOfWork unitOfWork;

    public UserRepository(UnitOfWork unitOfWork)
    {
        this.unitOfWork = unitOfWork;
    }


    public boolean regUser(String username, String password) throws Exception {

        boolean operationSuccess = false;
        String query = "INSERT INTO \"userCredentials\" (username, password) VALUES (?, ?)";

        try(PreparedStatement pstmt = unitOfWork.prepareStatement(query);) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0 && CreateUserCoinsEtc(username)) {
                unitOfWork.commitTransaction();
                operationSuccess = true;
            } else {
                unitOfWork.rollbackTransaction();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction(); // Rollback im Fehlerfall
        }
        return operationSuccess;
    }

    // Create user coins
    public boolean CreateUserCoinsEtc(String username){
        int userId = getUserID(username);
        String query = "INSERT INTO \"user\" (fk_user_id, in_game_name) VALUES (?, ?)";
        try{
            PreparedStatement pstmt = unitOfWork.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setString(2, username);
            /*pstmt.executeUpdate();*/
            unitOfWork.registerNew(pstmt);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean updateUserData(String username, String name, String bio, String image){

        int userId = getUserID(username);
        try {
            String updateBioAndImageEtc = "Update \"user\" Set bio = ?, image = ?, in_game_name = ? where fk_user_id = ?";
            PreparedStatement updateBioAndImageStmt = unitOfWork.prepareStatement(updateBioAndImageEtc);


            updateBioAndImageStmt.setString(1, bio);
            updateBioAndImageStmt.setString(2, image);
            updateBioAndImageStmt.setString(3, name);
            updateBioAndImageStmt.setInt(4, userId);


            int updateResult = updateBioAndImageStmt.executeUpdate();
            if (updateResult == 1){
                unitOfWork.commitTransaction();
            }else{
                unitOfWork.rollbackTransaction();
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
        }
        return false;
    }


    public boolean checkIfUsernameIsTaken(String username) throws SQLException{
        String query = "SELECT * FROM \"userCredentials\" WHERE \"username\" = ?";
        try (PreparedStatement pstmt = unitOfWork.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet response = pstmt.executeQuery();
            if (response.next()){
                return true;
            }else{
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public String get_user_data(String username){
        int userId = getUserID(username);
        String query = "SELECT * FROM \"user\" WHERE \"fk_user_id\" = ?";

        try (PreparedStatement pstmt = unitOfWork.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet response = pstmt.executeQuery();
            if (response.next()){
                String bio = response.getString("bio");
                String image = response.getString("image");
                String name = response.getString("in_game_name");
                StringBuilder jsonBuilder = new StringBuilder("{");
                jsonBuilder.append("\"Name\": \"").append(name).append("\",");
                jsonBuilder.append("\"Bio\": \"").append(bio).append("\",");
                jsonBuilder.append("\"Image\": \"").append(image).append("\"");
                jsonBuilder.append("}");
                return jsonBuilder.toString();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }




    public int getUserID(String username){
        int userID = -1;
        try{
            String selectUserQuery = "SELECT * FROM \"userCredentials\" where username = ?";
            PreparedStatement selectUserStmt = unitOfWork.prepareStatement(selectUserQuery);
            selectUserStmt.setString(1, username);
            ResultSet userResultSet = selectUserStmt.executeQuery();

            if (userResultSet.next()){
                userID = userResultSet.getInt("id");
                return userID;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        if (userID == -1) {
            throw new RuntimeException("Benutzer nicht gefunden");
        }
        return userID;
    }



}
