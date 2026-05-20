package db_lab.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe per la gestione della connessione al database MySQL
 */
public class DatabaseConnection {
    
    // Configurazione database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/animal_reserve?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "LilPeep2017_"; 
    
    private static Connection connection = null;
    
    /**
     * Ottiene una connessione al database
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Carica il driver MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Crea la connessione
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("✅ Connessione al database stabilita!");
                
            } catch (ClassNotFoundException e) {
                System.err.println("❌ Driver MySQL non trovato!");
                throw new SQLException("Driver MySQL non trovato", e);
            }
        }
        return connection;
    }
    
    /**
     * Chiude la connessione al database
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("✅ Connessione al database chiusa.");
            } catch (SQLException e) {
                System.err.println("❌ Errore nella chiusura: " + e.getMessage());
            }
        }
    }
    
    /**
     * Testa la connessione al database
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("❌ Test connessione fallito: " + e.getMessage());
            return false;
        }
    }
}