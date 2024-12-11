package at.fhtw.mtcg.database;

import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UnitOfWork implements AutoCloseable{
    @Getter
    private Connection connection;
    private List<PreparedStatement> commands;

    public UnitOfWork() {
        this.connection = DatabaseConnector.INSTANCE.getConnection();
        this.commands = new ArrayList<>();
        try {
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DataAccessError("Autocommit nicht deaktivierbar", e);
        }
    }

    public void commitTransaction()
    {
        if (this.connection != null) {
            try {
                for (PreparedStatement command : commands) {
                    command.executeUpdate();
                }
                this.connection.commit();
                commands = new ArrayList<>();
            } catch (SQLException e) {
                throw new DataAccessError("Commit der Transaktion nicht erfolgreich", e);
            }
        }
    }
    public void rollbackTransaction()
    {
        if (this.connection != null) {
            try {
                this.connection.rollback();
            } catch (SQLException e) {
                throw new DataAccessError("Rollback der Transaktion nicht erfolgreich", e);
            }
        }
    }

    public void finishWork()
    {
        if (this.connection != null) {
            try {
                this.connection.close();
                this.connection = null;
            } catch (SQLException e) {
                throw new DataAccessError("Schließen der Connection nicht erfolgreich", e);
            }
        }
    }

    public PreparedStatement prepareStatement(String sql)
    {
        if (this.connection != null) {
            try {
                return this.connection.prepareStatement(sql);
            } catch (SQLException e) {
                throw new DataAccessError("Erstellen eines PreparedStatements nicht erfolgreich", e);
            }
        }
        throw new DataAccessError("UnitOfWork hat keine aktive Connection zur Verfügung");
    }

    public void registerNew(PreparedStatement stmt) {
        if (stmt != null) {
            commands.add(stmt);
        }
    }

    @Override
    public void close() throws Exception {
        this.finishWork();
    }
}
