package at.fhtw.mtcg.database;

public class DBAccessError extends RuntimeException{
    public DBAccessError(String message) {
        super(message);
    }

    public DBAccessError(String message, Throwable cause) {
        super(message, cause);
    }

    public DBAccessError(Throwable cause) {
        super(cause);
    }
}
