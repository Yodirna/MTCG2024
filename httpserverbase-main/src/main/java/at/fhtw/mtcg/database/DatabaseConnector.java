package at.fhtw.mtcg.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum DatabaseConnector {
    INSTANCE;

    public Connection getConnection()
    {
        try {
            return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/swen1",
                    "postgres",
                    "postgres");
        } catch (SQLException e) {
            throw new DBAccessError("Datenbankverbindungsaufbau nicht erfolgreich", e);
        }
    }
}
