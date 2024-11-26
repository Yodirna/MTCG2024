package cardgame.mtcg.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String url = "jdbc:postgresql://localhost:5432/mtcg";
    private static final String user = "postgres";
    private static final String password = "postgres";

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

}
