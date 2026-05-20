package db_lab.data;

import java.io.Serial;

// Eccezione runtime usata per incapsulare tutte le eccezioni provenienti dai DAO,
// in modo da non far emergere SQLException nel resto del codice.
//
public final class DAOException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public DAOException(String message) {
        super(message);
    }

    public DAOException(Throwable cause) {
        super(cause);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
