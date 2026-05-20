package db_lab.data;

import db_lab.model.Terapia;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO per la gestione delle Terapie
 * Implementa l'operazione OP11
 */
public class TerapiaDAO {
    
    /**
     * OP11 - Inserisce una nuova terapia
     */
    public boolean insertTerapia(Terapia terapia) {
        String sql = "INSERT INTO Terapia (farmaco, dosaggio, durata, data_inizio, data_fine, ID_controllo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, terapia.getFarmaco());
            pstmt.setString(2, terapia.getDosaggio());
            pstmt.setString(3, terapia.getDurata());
            pstmt.setDate(4, Date.valueOf(terapia.getDataInizio()));
            pstmt.setDate(5, Date.valueOf(terapia.getDataFine()));
            pstmt.setInt(6, terapia.getIdControllo());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        terapia.setIdTerapia(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✅ Terapia registrata!");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Errore inserimento terapia: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Recupera terapie per controllo sanitario
     */
    public List<Terapia> getTerapieByControllo(int idControllo) {
        List<Terapia> terapie = new ArrayList<>();
        String sql = "SELECT * FROM Terapia WHERE ID_controllo = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idControllo);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    terapie.add(extractTerapiaFromResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Errore recupero terapie: " + e.getMessage());
        }
        
        return terapie;
    }
    
    /**
     * Recupera terapie in corso (non ancora concluse)
     */
    public List<Terapia> getTerapieInCorso() {
        List<Terapia> terapie = new ArrayList<>();
        String sql = "SELECT T.*, A.nome_animale " +
                     "FROM Terapia T " +
                     "JOIN Controllo_Sanitario CS ON T.ID_controllo = CS.ID_controllo " +
                     "JOIN Animale A ON CS.ID_animale = A.ID_animale " +
                     "WHERE T.data_fine >= CURDATE() " +
                     "ORDER BY T.data_fine ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                terapie.add(extractTerapiaFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Errore recupero terapie in corso: " + e.getMessage());
        }
        
        return terapie;
    }
    
    /**
     * Recupera terapia per ID
     */
    public Terapia getById(int id) {
        String sql = "SELECT * FROM Terapia WHERE ID_terapia = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractTerapiaFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Errore recupero terapia: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Aggiorna una terapia
     */
    public boolean updateTerapia(Terapia terapia) {
        String sql = "UPDATE Terapia SET farmaco=?, dosaggio=?, durata=?, data_inizio=?, data_fine=? " +
                     "WHERE ID_terapia=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, terapia.getFarmaco());
            pstmt.setString(2, terapia.getDosaggio());
            pstmt.setString(3, terapia.getDurata());
            pstmt.setDate(4, Date.valueOf(terapia.getDataInizio()));
            pstmt.setDate(5, Date.valueOf(terapia.getDataFine()));
            pstmt.setInt(6, terapia.getIdTerapia());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Errore aggiornamento terapia: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Helper per estrarre Terapia da ResultSet
     */
    private Terapia extractTerapiaFromResultSet(ResultSet rs) throws SQLException {
        Terapia terapia = new Terapia();
        terapia.setIdTerapia(rs.getInt("ID_terapia"));
        terapia.setFarmaco(rs.getString("farmaco"));
        terapia.setDosaggio(rs.getString("dosaggio"));
        terapia.setDurata(rs.getString("durata"));
        terapia.setDataInizio(rs.getDate("data_inizio").toLocalDate());
        terapia.setDataFine(rs.getDate("data_fine").toLocalDate());
        terapia.setIdControllo(rs.getInt("ID_controllo"));
        return terapia;
    }
}