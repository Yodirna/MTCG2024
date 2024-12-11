package at.fhtw.mtcg.database;

public class DataAccessError extends RuntimeException{
    public DataAccessError(String message) {
        super(message);
    }

    public DataAccessError(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessError(Throwable cause) {
        super(cause);
    }
}
