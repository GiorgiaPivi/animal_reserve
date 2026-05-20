package db_lab.data;

import db_lab.model.ControlloSanitario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO per la gestione dei Controlli Sanitari
 * Implementa le operazioni: OP09, OP10
 */
public class ControlloSanitarioDAO {
    
    /**
     * OP09 - Inserisce un nuovo controllo sanitario
     */
    public boolean insertControllo(ControlloSanitario controllo) {
        String sql = "INSERT INTO Controllo_Sanitario (data, ora, tipologia, esito, ID_animale, ID_veterinario) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setDate(1, Date.valueOf(controllo.getData()));
            pstmt.setTime(2, Time.valueOf(controllo.getOra()));
            pstmt.setString(3, controllo.getTipologia());
            pstmt.setString(4, controllo.getEsito());
            pstmt.setInt(5, controllo.getIdAnimale());
            pstmt.setInt(6, controllo.getIdVeterinario());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        controllo.setIdControllo(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✅ Controllo sanitario registrato!");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Errore inserimento controllo: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * OP10 - Recupera storico controlli di un animale
     */
    public List<ControlloSanitario> getStoricoByAnimale(int idAnimale) {
        List<ControlloSanitario> controlli = new ArrayList<>();
        String sql = "SELECT CS.*, U.nome, U.cognome " +
                     "FROM Controllo_Sanitario CS " +
                     "JOIN Utente U ON CS.ID_veterinario = U.ID_utente " +
                     "WHERE CS.ID_animale = ? " +
                     "ORDER BY CS.data DESC, CS.ora DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idAnimale);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    controlli.add(extractControlloFromResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Errore recupero storico controlli: " + e.getMessage());
        }
        
        return controlli;
    }
    
    /**
     * Recupera tutti i controlli
     */
    public List<ControlloSanitario> getAllControlli() {
        List<ControlloSanitario> controlli = new ArrayList<>();
        String sql = "SELECT * FROM Controllo_Sanitario ORDER BY data DESC, ora DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                controlli.add(extractControlloFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Errore recupero controlli: " + e.getMessage());
        }
        
        return controlli;
    }
    
    /**
     * Recupera controlli per veterinario
     */
    public List<ControlloSanitario> getControlliByVeterinario(int idVeterinario) {
        List<ControlloSanitario> controlli = new ArrayList<>();
        String sql = "SELECT * FROM Controllo_Sanitario WHERE ID_veterinario = ? ORDER BY data DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idVeterinario);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    controlli.add(extractControlloFromResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Errore recupero controlli veterinario: " + e.getMessage());
        }
        
        return controlli;
    }
    
    /**
     * Recupera controllo per ID
     */
    public ControlloSanitario getById(int id) {
        String sql = "SELECT * FROM Controllo_Sanitario WHERE ID_controllo = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractControlloFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Errore recupero controllo: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Aggiorna un controllo sanitario
     */
    public boolean updateControllo(ControlloSanitario controllo) {
        String sql = "UPDATE Controllo_Sanitario SET tipologia=?, esito=? WHERE ID_controllo=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, controllo.getTipologia());
            pstmt.setString(2, controllo.getEsito());
            pstmt.setInt(3, controllo.getIdControllo());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Errore aggiornamento controllo: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Helper per estrarre ControlloSanitario da ResultSet
     */
    private ControlloSanitario extractControlloFromResultSet(ResultSet rs) throws SQLException {
        ControlloSanitario controllo = new ControlloSanitario();
        controllo.setIdControllo(rs.getInt("ID_controllo"));
        controllo.setData(rs.getDate("data").toLocalDate());
        controllo.setOra(rs.getTime("ora").toLocalTime());
        controllo.setTipologia(rs.getString("tipologia"));
        controllo.setEsito(rs.getString("esito"));
        controllo.setIdAnimale(rs.getInt("ID_animale"));
        controllo.setIdVeterinario(rs.getInt("ID_veterinario"));
        return controllo;
    }
}
