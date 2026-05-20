package db_lab.data;

import db_lab.model.Utente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO per la gestione degli Utenti
 * Implementa le operazioni: OP01, OP02, OP12
 */
public class UtenteDAO {
    
    /**
     * OP01 - Registrazione nuovo utente
     */
    public boolean registraUtente(Utente utente) {
        String sql = "INSERT INTO Utente (nome, cognome, email, password, ruolo) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, utente.getNome());
            pstmt.setString(2, utente.getCognome());
            pstmt.setString(3, utente.getEmail());
            pstmt.setString(4, utente.getPassword());
            pstmt.setString(5, utente.getRuolo());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        utente.setIdUtente(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✅ Utente registrato con successo!");
                return true;
            }
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("❌ Email già registrata!");
            } else {
                System.err.println("❌ Errore registrazione: " + e.getMessage());
            }
        }
        
        return false;
    }
    
    /**
     * OP02 - Login utente
     */
    public Utente login(String email, String password) {
        String sql = "SELECT * FROM Utente WHERE email = ? AND password = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Utente utente = extractUtenteFromResultSet(rs);
                    System.out.println("✅ Login effettuato: " + utente.getNome() + " " + 
                                     utente.getCognome() + " (" + utente.getRuolo() + ")");
                    return utente;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Errore login: " + e.getMessage());
        }
        
        System.err.println("❌ Credenziali non valide");
        return null;
    }
    
    /**
     * OP12 - Gestione profilo utente (aggiornamento)
     */
    public boolean updateUtente(Utente utente) {
        String sql = "UPDATE Utente SET nome=?, cognome=?, email=?, ruolo=? WHERE ID_utente=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, utente.getNome());
            pstmt.setString(2, utente.getCognome());
            pstmt.setString(3, utente.getEmail());
            pstmt.setString(4, utente.getRuolo());
            pstmt.setInt(5, utente.getIdUtente());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Errore aggiornamento utente: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Recupera tutti gli utenti
     */
    public List<Utente> getAllUtenti() {
        List<Utente> utenti = new ArrayList<>();
        String sql = "SELECT * FROM Utente ORDER BY cognome, nome";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                utenti.add(extractUtenteFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Errore recupero utenti: " + e.getMessage());
        }
        
        return utenti;
    }
    
    /**
     * Recupera tutti i veterinari
     */
    public List<Utente> getVeterinari() {
        return getUtentiByRuolo("veterinario");
    }
    
    /**
     * Recupera tutti i volontari
     */
    public List<Utente> getVolontari() {
        return getUtentiByRuolo("volontario");
    }
    
    /**
     * Recupera utenti per ruolo
     */
    private List<Utente> getUtentiByRuolo(String ruolo) {
        List<Utente> utenti = new ArrayList<>();
        String sql = "SELECT * FROM Utente WHERE ruolo = ? ORDER BY cognome, nome";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ruolo);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    utenti.add(extractUtenteFromResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Errore recupero utenti per ruolo: " + e.getMessage());
        }
        
        return utenti;
    }
    
    /**
     * Recupera utente per ID
     */
    public Utente getById(int id) {
        String sql = "SELECT * FROM Utente WHERE ID_utente = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractUtenteFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Errore recupero utente: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Helper per estrarre Utente da ResultSet
     */
    private Utente extractUtenteFromResultSet(ResultSet rs) throws SQLException {
        Utente utente = new Utente();
        utente.setIdUtente(rs.getInt("ID_utente"));
        utente.setNome(rs.getString("nome"));
        utente.setCognome(rs.getString("cognome"));
        utente.setEmail(rs.getString("email"));
        utente.setPassword(rs.getString("password"));
        utente.setRuolo(rs.getString("ruolo"));
        return utente;
    }
}