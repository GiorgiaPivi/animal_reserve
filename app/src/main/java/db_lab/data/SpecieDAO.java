package db_lab.data;

import db_lab.model.Specie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO per la gestione delle Specie
 */
public class SpecieDAO {
    
    /**
     * Recupera tutte le specie
     */
    public List<Specie> getAllSpecie() {
        List<Specie> specie = new ArrayList<>();
        String sql = "SELECT * FROM Specie ORDER BY nome_specie";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                specie.add(extractSpecieFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Errore recupero specie: " + e.getMessage());
        }
        
        return specie;
    }
    
    /**
     * Recupera specie per ID
     */
    public Specie getById(int id) {
        String sql = "SELECT * FROM Specie WHERE ID_specie = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractSpecieFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Errore recupero specie: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Inserisce una nuova specie
     */
    public boolean insertSpecie(Specie specie) {
        String sql = "INSERT INTO Specie (nome_specie) VALUES (?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, specie.getNomeSpecie());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        specie.setIdSpecie(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Errore inserimento specie: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Conta il numero di animali per specie
     */
    public int contaAnimaliPerSpecie(int idSpecie) {
        String sql = "SELECT COUNT(*) as totale FROM Animale WHERE ID_specie = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idSpecie);
            
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
     * Helper per estrarre Specie da ResultSet
     */
    private Specie extractSpecieFromResultSet(ResultSet rs) throws SQLException {
        Specie specie = new Specie();
        specie.setIdSpecie(rs.getInt("ID_specie"));
        specie.setNomeSpecie(rs.getString("nome_specie"));
        return specie;
    }
}
