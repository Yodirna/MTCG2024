package at.fhtw.mtcg.database.repository;

import at.fhtw.mtcg.security.Hash;
import at.fhtw.mtcg.database.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionRepository {
    private UnitOfWork unitOfWork;

    public SessionRepository(UnitOfWork unitOfWork)
    {
        this.unitOfWork = unitOfWork;
    }

    // Funktion um den Usern einzuloggen
    public  boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM \"userCredentials\" WHERE \"username\" = ?";
        try (PreparedStatement pstmt = unitOfWork.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet response = pstmt.executeQuery();

            if (response.next()){
                Hash hash = new Hash();
                String dbUsername =response.getString("username");
                String dbPassword =response.getString("password");
                boolean passwordMatch = hash.verifyPassword(password, dbPassword);

                return username.equals(dbUsername) && passwordMatch;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
