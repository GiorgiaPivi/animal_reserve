package db_lab.data;

import db_lab.model.Recinto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO per la gestione dei Recinti
 */
public class RecintoDAO {
    
    /**
     * Recupera tutti i recinti
     */
    public List<Recinto> getAllRecinti() {
        List<Recinto> recinti = new ArrayList<>();
        String sql = "SELECT * FROM Recinto ORDER BY ID_recinto";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                recinti.add(extractRecintoFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Errore recupero recinti: " + e.getMessage());
        }
        
        return recinti;
    }
    
    /**
     * Recupera recinto per ID
     */
    public Recinto getById(int id) {
        String sql = "SELECT * FROM Recinto WHERE ID_recinto = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractRecintoFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Errore recupero recinto: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Inserisce un nuovo recinto
     */
    public boolean insertRecinto(Recinto recinto) {
        String sql = "INSERT INTO Recinto (tipologia) VALUES (?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, recinto.getTipologia());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        recinto.setIdRecinto(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Errore inserimento recinto: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Conta animali in un recinto
     */
    public int contaAnimaliInRecinto(int idRecinto) {
        String sql = "SELECT COUNT(*) as totale FROM Animale WHERE ID_recinto = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idRecinto);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("totale");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Errore conteggio animali: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Helper per estrarre Recinto da ResultSet
     */
    private Recinto extractRecintoFromResultSet(ResultSet rs) throws SQLException {
        Recinto recinto = new Recinto();
        recinto.setIdRecinto(rs.getInt("ID_recinto"));
        recinto.setTipologia(rs.getString("tipologia"));
        return recinto;
    }
}
